package com.github.sc.scheduler.core.engine;

import com.github.sc.scheduler.core.engine.executor.lookup.ExecutorLookupService;
import com.github.sc.scheduler.core.model.*;
import com.github.sc.scheduler.core.repo.ActiveRunsRepository;
import com.github.sc.scheduler.core.repo.TaskArgsRepository;
import com.github.sc.scheduler.core.repo.TimetableRepository;
import com.github.sc.scheduler.core.utils.LocalTaskScheduler;
import com.github.sc.scheduler.core.utils.SchedulerHostProvider;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.stream.Collectors.*;

/**
 * Engine starts and execute runs in scheduler.
 * <p>
 * In main loop it fix failed to start runs
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class Engine {
    /**
     * Time interval that is given to run master to create task run and store it into repository
     */
    public static final Duration START_INTERVAL = Duration.ofMillis(TimeUnit.MINUTES.toMillis(1));
    /**
     * Default hanged multiplier, that used to determine hanged runs
     *
     * @see #setHangedMultiplier(int)
     */
    public static final int DEFAULT_HANGED_MULTIPLIER = 10;

    /**
     * Default pick period, that used to pick runs from repository and start them
     *
     * @see #setPickPeriodSeconds(long)
     */
    public static final long DEFAULT_PICK_PERIOD_SECONDS = TimeUnit.MINUTES.toSeconds(1);

    private static final Logger log = LoggerFactory.getLogger(Engine.class);

    /* Dependencies */
    private ActiveRunsRepository activeRunsRepository;
    private TaskArgsRepository taskArgsRepository;
    private TimetableRepository timetableRepository;
    private ExecutorLookupService executorLookupService;
    private TaskPicker taskPicker;
    private Clock clock = Clock.systemDefaultZone();

    /* Properties */
    private String host;
    private int threadsCount;
    private int capacity;
    private String service;
    private long pickPeriodSeconds = DEFAULT_PICK_PERIOD_SECONDS;
    private int hangedMultiplier = DEFAULT_HANGED_MULTIPLIER;

    /* internal fields */
    /**
     * Engine state
     */
    private EngineState state;
    /**
     * Executor service for task's runs execution
     */
    private ExecutorService executorService;
    /**
     * Engine main loop scheduler
     */
    private LocalTaskScheduler localTaskScheduler = new LocalTaskScheduler(1);
    /**
     * Main loop lock
     */
    private Lock runLock = new ReentrantLock();

    @PostConstruct
    public void start() {
        state = new EngineState(threadsCount, capacity);
        executorService = Executors.newFixedThreadPool(threadsCount);
        localTaskScheduler.scheduleWithFixRate(getClass().getSimpleName(), this::run, 0, pickPeriodSeconds, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() {
        localTaskScheduler.shutdownNow();
        executorService.shutdownNow();
    }

    /**
     * Force engine to pick up tasks and starts their execution, if it has free resources
     */
    public void touch() {
        run();
    }

    private void run() {
        try {
            if (runLock.tryLock(1, TimeUnit.MILLISECONDS)) {
                try {
                    runUnsafe();
                } finally {
                    runLock.unlock();
                }
            }
        } catch (InterruptedException ignore) {
        }
    }

    private void runUnsafe() {
        pingRunning();

        EngineState.Capacity capacity = state.getCapacity();
        if (capacity.freeThreads == 0) {
            log.debug("No free scheduler engine threads");
            return;
        }
        if (capacity.freeCapacity == 0) {
            log.debug("No free scheduler engine capacity");
            return;
        }

        log.debug("Restoring acquired runs");
        freeOrphanedAcquiredRuns();
        processedHangedRuns();

        List<Run> runs = activeRunsRepository.getAll();
        if (runs.isEmpty()) {
            log.debug("No any task in the queue");
            return;
        }
        log.debug("Found {} tasks in queue", runs.size());

        List<Run> pendingRuns = runs.stream()
                .filter(r -> r.getStatus() == Run.Status.PENDING)
                .filter(r -> r.getHost() == null)
                .collect(toList());
        List<Run> runsToStart = taskPicker.pickRuns(pendingRuns, toDescriptor(state));
        if (runsToStart.isEmpty()) {
            log.debug("No appropriate tasks to start");
            return;
        }
        log.debug("Found {} tasks to start", runsToStart.size());

        for (final Run run : runsToStart) {
            try {
                if (state.occupy(run)) {
                    if (!tryStart(run)) {
                        state.free(run);
                    }
                } else {
                    log.debug("Engine capacity ended: {}. No more task can be started", state);
                    break;
                }
            } catch (Throwable e) {
                state.free(run);
                String message = String.format("Failed to start run %d for task %s due to %s",
                        run.getRunId(), run.getTaskId(), e instanceof Error ? "error" : "exception");
                if (e instanceof Error) {
                    log.error(MarkerFactory.getMarker("FATAL"), message, e);
                    throw e;
                }
                log.error(message, e);
            }
        }
    }

    private void pingRunning() {
        List<RunFuture> toRemove = new ArrayList<>();
        for (Iterator<RunFuture> iterator = state.runningTasks.iterator(); iterator.hasNext(); ) {
            RunFuture r = iterator.next();
            if (!r.future.isDone()) {
                activeRunsRepository.ping(r.runId, Instant.now(clock));
                continue;
            }
            toRemove.add(r);
        }
        if (!toRemove.isEmpty()) {
            state.runningTasks.removeAll(toRemove);
        }
    }

    /**
     * Remove host and acquired time for runs with expired START_MULTIPLIER
     */
    private void freeOrphanedAcquiredRuns() {
        Instant now = Instant.now(clock);
        activeRunsRepository.getAll().stream()
                .filter(r -> r.getStatus() == Run.Status.PENDING)
                .filter(r -> r.getHost() != null)
                .filter(r -> r.getAcquiredTime() != null && r.getAcquiredTime().plus(START_INTERVAL).isBefore(now))
                .peek(r -> log.debug("Restoring run {} for task {}", r.getRunId(), r.getTaskId()))
                .forEach(r -> activeRunsRepository.tryUpdate(
                        RunImpl.builder(r)
                                .withHost(null)
                                .withAcquiredTime(null)
                                .build()
                ));
    }

    private void processedHangedRuns() {
        Instant now = Instant.now(clock);
        List<Run> hangedRuns = activeRunsRepository.getAll().stream()
                .filter(r -> r.getStatus() == Run.Status.RUNNING)
                .filter(r -> r.getPingTime() != null && r.getPingTime().plusSeconds(pickPeriodSeconds * hangedMultiplier).isBefore(now))
                .map(this::hanged)
                .collect(toList());
        hangedRuns.stream()
                .filter(run -> !run.isRestartOnReboot())
                .peek(r -> log.debug("Failing hanged run {} for task {}", r.getRunId(), r.getTaskId()))
                .map(activeRunsRepository::tryUpdate);

        List<Run> runsToRestart = hangedRuns.stream()
                .filter(Run::isRestartOnReboot)
                .collect(toList());
        rescheduleRuns(runsToRestart);
    }

    private void rescheduleRuns(List<Run> runs) {
        Set<String> taskIds = runs.stream().map(Run::getTaskId).collect(toSet());
        Map<String, Integer> taskConcurrencyLevel = timetableRepository.getAll(taskIds).stream()
                .collect(toMap(SchedulingParams::getTaskId, SchedulingParams::getConcurrencyLevel));
        runs.forEach(run -> {
            Optional<Run> started = activeRunsRepository.recreate(run, taskConcurrencyLevel.get(run.getTaskId()));
            if (!started.isPresent()) {
                log.info("Can't recreated run {}", run.getRunId());
                return;
            }
            log.info("Run {} restarted. Reason: {}, new run id {}", run.getRunId(), run.getStatus(), started.get().getRunId());
        });
    }

    private boolean tryStart(Run run) {
        Optional<Run> acquiredRunOpt = tryAcquire(run);
        if (!acquiredRunOpt.isPresent() || !Objects.equals(acquiredRunOpt.get().getHost(), host)) {
            log.debug("Can't {} acquire run", run.getRunId());
            return false;
        }
        Run acquiredRun = acquiredRunOpt.get();
        Optional<TaskExecutor> executor = executorLookupService.get(acquiredRun.getEngineRequirements().getExecutor());
        if (!executor.isPresent()) {
            activeRunsRepository.tryUpdate(failed(acquiredRun, "No executor found"));
            return false;
        }
        Optional<TaskArgs> taskArgs = taskArgsRepository.get(acquiredRun.getTaskId());
        log.debug("Starting run {} with params {}", acquiredRun, taskArgs);
        RunContext context = new RunContext(acquiredRun, taskArgs.orElse(null));
        start(acquiredRun, executor.get(), context);
        return true;
    }

    private void start(Run r, TaskExecutor executor, RunContext context) {
        state.runningTasks.add(new RunFuture(r.getRunId(), executorService.submit(new Runner(r, executor, context))));
    }

    // todo think, maybe move this methods into repository to use db time instead of local machine time
    // todo it is possible to use custom clock, which will be initialized from db clock and sync with it sometimes

    private Optional<Run> tryAcquire(Run run) {
        return activeRunsRepository.tryUpdate(
                RunImpl.builder(run)
                        .withHost(host)
                        .withAcquiredTime(Instant.now(clock))
                        .build()
        );
    }

    private Optional<Run> tryRunning(Run run) {
        return activeRunsRepository.tryUpdate(
                RunImpl.builder(run)
                        .withStatus(Run.Status.RUNNING)
                        .withStartTime(Instant.now(clock))
                        .withPingTime(Instant.now(clock))
                        .build()
        );
    }

    private Optional<Run> complete(Run run, String message) {
        return activeRunsRepository.tryUpdate(
                RunImpl.builder(run)
                        .withStatus(Run.Status.COMPLETE)
                        .withEndTime(Instant.now(clock))
                        .withMessage(message)
                        .build()
        );
    }

    private Run failed(Run run, String reason) {
        return RunImpl.builder(run)
                .withStatus(Run.Status.FAILED)
                .withEndTime(Instant.now(clock))
                .withMessage(reason)
                .build();
    }

    private Run hanged(Run run) {
        return RunImpl.builder(run)
                .withStatus(Run.Status.HANGED)
                .withEndTime(Instant.now(clock))
                .withMessage("Hanged!")
                .build();
    }

    private EngineDescriptor toDescriptor(EngineState state) {
        EngineState.Capacity capacity = state.getCapacity();
        return new EngineDescriptor() {
            @Override
            public int getMaxCapacity() {
                return Engine.this.capacity;
            }

            @Override
            public int getFreeCapacity() {
                return capacity.freeCapacity;
            }

            @Override
            public int getMaxThreads() {
                return Engine.this.threadsCount;
            }

            @Override
            public int getFreeThreads() {
                return capacity.freeThreads;
            }

            @Nonnull
            @Override
            public String getService() {
                return Engine.this.service;
            }

            @Nonnull
            @Override
            public String getHost() {
                return Engine.this.host;
            }
        };
    }

    private final class Runner implements Runnable {

        private final Run r;
        private final TaskExecutor executor;
        private final RunContext context;

        Runner(Run r, TaskExecutor executor, RunContext context) {
            this.r = r;
            this.executor = executor;
            this.context = context;
        }

        @Override
        public void run() {
            executeTask();
            Engine.this.touch();
        }

        private void executeTask() {
            log.debug("Set status RUNNING to run {}", r.getRunId());
            Optional<Run> runOpt = tryRunning(r);
            if (!runOpt.isPresent()) {
                log.debug("No run with id {} for task {} found!", r.getRunId(), r.getTaskId());
                return;
            }
            Run run = runOpt.get();
            if (!Objects.equals(run.getHost(), host)) {
                log.debug("Host {} lost lock on run {}", host, run.getRunId());
                return;
            }
            if (run.getStatus() != Run.Status.RUNNING) {
                log.error("Failed to mark run with id {} as RUNNING", r.getRunId());
                return;
            }
            RunContext.set(context);
            try {
                log.debug("Starting run {}", run.getRunId());
                executor.run(context);
                Optional<Run> completed = complete(run, context.getMessage());
                if (!completed.isPresent()) {
                    log.error("No run with id {} found!", r.getRunId());
                    return;
                }
                if (completed.get().getStatus() != Run.Status.COMPLETE) {
                    log.error("Failed to mark run with id {} as COMPLETED", r.getRunId());
                }
            } catch (Throwable e) {
                String reason = getFailReason(run);
                Run failed = failed(run, reason);
                if (run.isRestartOnFail()) {
                    rescheduleRuns(Collections.singletonList(failed));
                } else {
                    activeRunsRepository.tryUpdate(failed);
                }
                if (e instanceof Exception) {
                    log.error(reason, e);
                } else {
                    log.error(MarkerFactory.getMarker("FATAL"), reason, e);
                    throw Throwables.propagate(e);
                }
            } finally {
                RunContext.clear();
                state.free(run);
            }
        }

        private String getFailReason(Run run) {
            String userMessage = context.getMessage();
            String reason = String.format("Run %d of task %s failed with throwable", run.getRunId());
            reason = userMessage == null ? reason : userMessage + "; " + reason;
            return reason;
        }
    }

    /* Setters */
    @Required
    public void setActiveRunsRepository(ActiveRunsRepository activeRunsRepository) {
        this.activeRunsRepository = activeRunsRepository;
    }

    @Required
    public void setTaskArgsRepository(TaskArgsRepository taskArgsRepository) {
        this.taskArgsRepository = taskArgsRepository;
    }

    @Required
    public void setTimetableRepository(TimetableRepository timetableRepository) {
        this.timetableRepository = timetableRepository;
    }

    @Required
    public void setExecutorLookupService(ExecutorLookupService executorLookupService) {
        this.executorLookupService = executorLookupService;
    }

    @Required
    public void setTaskPicker(TaskPicker taskPicker) {
        this.taskPicker = taskPicker;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Required
    public void setHostProvider(SchedulerHostProvider provider) {
        this.host = provider.getHost();
    }

    @Required
    public void setThreadsCount(int threadsCount) {
        this.threadsCount = threadsCount;
    }

    @Required
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Required
    public void setService(String service) {
        this.service = service;
    }

    /**
     * The period after which engine tries to obtain tasks from the repository and run them
     *
     * @param pickPeriodSeconds period in seconds
     */
    public void setPickPeriodSeconds(long pickPeriodSeconds) {
        this.pickPeriodSeconds = pickPeriodSeconds;
    }

    /**
     * Hanged multiplier tell engine when it can assume, that run is hanged. On each cycle engine ping all runs,
     * that it has. If run's ping time is "enough" old, then engine assume that run hanged. As "enough" engine
     * use {@code hangMultiplier * pickPeriod}. If run's {@code pingTime + hangMultiplier * pickPeriod} is
     * less than now, than run is assumed hanged
     *
     * @param hangedMultiplier multiplier
     */
    public void setHangedMultiplier(int hangedMultiplier) {
        this.hangedMultiplier = hangedMultiplier;
    }
}
