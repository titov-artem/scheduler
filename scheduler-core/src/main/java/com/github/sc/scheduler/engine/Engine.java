package com.github.sc.scheduler.engine;

import com.github.sc.scheduler.engine.executor.lookup.ExecutorLookupService;
import com.github.sc.scheduler.model.EngineDescriptor;
import com.github.sc.scheduler.model.Run;
import com.github.sc.scheduler.model.RunImpl;
import com.github.sc.scheduler.model.TaskArgs;
import com.github.sc.scheduler.repo.ActiveRunsRepository;
import com.github.sc.scheduler.repo.TaskArgsRepository;
import com.github.sc.scheduler.utils.LocalTaskScheduler;
import com.github.sc.scheduler.utils.SchedulerHostProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

import static java.util.stream.Collectors.toList;

/**
 * Engine starts and execute runs in scheduler.
 * <p>
 * In main loop it fix failed to start runs
 */
public class Engine {
    /**
     * Time interval that is given to run master to create task run and store it into repository
     */
    public static final Duration START_INTERVAL = Duration.ofMillis(TimeUnit.MINUTES.toMillis(1));
    private static final Logger log = LoggerFactory.getLogger(Engine.class);
    private static final long DEFAULT_PICK_PERIOD = TimeUnit.MINUTES.toSeconds(1);
    /* Dependencies */
    private ActiveRunsRepository activeRunsRepository;
    private TaskArgsRepository taskArgsRepository;
    private ExecutorLookupService executorLookupService;
    private TaskPicker taskPicker;
    private Clock clock = Clock.systemDefaultZone();

    /* Properties */
    private String host;
    private int threadCount;
    private int capacity;
    private String service;
    private long pickPeriod = DEFAULT_PICK_PERIOD;

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
        state = new EngineState(threadCount, capacity);
        executorService = Executors.newFixedThreadPool(threadCount);
        localTaskScheduler.scheduleWithFixRate(getClass().getSimpleName(), this::run, 0, pickPeriod, TimeUnit.SECONDS);
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
            log.trace("No free scheduler engine threads");
            return;
        }
        if (capacity.freeCapacity == 0) {
            log.trace("No free scheduler engine capacity");
            return;
        }

        log.trace("Restoring acquired runs");
        restoreAcquiredRuns();
        restoreHangedRuns();

        List<Run> runs = activeRunsRepository.getRuns();
        if (runs.isEmpty()) {
            log.trace("No any task in the queue");
            return;
        }
        log.trace("Found {} tasks in queue", runs.size());

        List<Run> pendingRuns = runs.stream()
                .filter(r -> r.getStatus() == Run.Status.PENDING)
                .filter(r -> r.getHost() == null)
                .collect(toList());
        List<Run> runsToStart = taskPicker.pickRuns(pendingRuns, toDescriptor(state));
        if (runsToStart.isEmpty()) {
            log.trace("No appropriate tasks to start");
            return;
        }
        log.trace("Found {} tasks to start", runsToStart.size());

        for (final Run run : runsToStart) {
            try {
                if (state.occupy(run)) {
                    if (!tryStart(run)) {
                        state.free(run);
                    }
                } else {
                    log.trace("Engine capacity ended: {}. No more task can be started", state);
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
     * Remove host and acquired time for runs with expired START_INTERVAL
     */
    private void restoreAcquiredRuns() {
        Instant now = Instant.now(clock);
        activeRunsRepository.getRuns().stream()
                .filter(r -> r.getStatus() == Run.Status.PENDING)
                .filter(r -> r.getHost() != null)
                .filter(r -> r.getAcquiredTime() != null && r.getAcquiredTime().plus(START_INTERVAL).isBefore(now))
                .peek(r -> log.trace("Restoring run {} for task {}", r.getRunId(), r.getTaskId()))
                .forEach(r -> activeRunsRepository.tryUpdate(
                        RunImpl.builder(r)
                                .withHost(null)
                                .withAcquiredTime(null)
                                .build()
                ));
    }

    private void restoreHangedRuns() {
        Instant now = Instant.now(clock);
        activeRunsRepository.getRuns().stream()
                .filter(r -> r.getStatus() == Run.Status.RUNNING)
                .filter(r -> r.getPingTime() != null && r.getPingTime().plusSeconds(pickPeriod * 3).isBefore(now))
                .peek(r -> log.trace("Failing hanged run {} for task {}", r.getRunId(), r.getTaskId()))
                .forEach(r -> fail(r, "Task hanged"));
    }

    private boolean tryStart(Run run) {
        Run acquiredRun = tryAcquire(run);
        if (acquiredRun == null) {
            log.error("Failed to start run {} because it wasn't found", run);
            return false;
        }
        if (!Objects.equals(acquiredRun.getHost(), host)) {
            log.trace("Run {} acquired by another host", acquiredRun.getRunId());
            return false;
        }
        Optional<Runnable> executor = executorLookupService.get(acquiredRun.getEngineRequirements().getExecutor());
        if (!executor.isPresent()) {
            fail(acquiredRun, "No executor found");
            return false;
        }
        Optional<TaskArgs> taskArgs = taskArgsRepository.get(acquiredRun.getTaskId());
        log.trace("Starting run {} with params {}", acquiredRun, taskArgs);
        RunContext context = new RunContext(acquiredRun, taskArgs.orElse(null));
        start(acquiredRun, executor.get(), context);
        return true;
    }

    private void start(Run r, Runnable executor, RunContext context) {
        state.runningTasks.add(new RunFuture(r.getRunId(), executorService.submit(new Runner(r, executor, context))));
    }

    // todo think, maybe move this methods into repository to use db time instead of local machine time
    // todo it is possible to use custom clock, which will be initialized from db clock and sync with it sometimes

    @Nullable
    private Run tryAcquire(Run run) {
        return activeRunsRepository.tryUpdate(
                RunImpl.builder(run)
                        .withHost(host)
                        .withAcquiredTime(Instant.now(clock))
                        .build()
        );
    }

    @Nullable
    private Run tryRunning(Run run) {
        return activeRunsRepository.tryUpdate(
                RunImpl.builder(run)
                        .withStatus(Run.Status.RUNNING)
                        .withStartTime(Instant.now(clock))
                        .withPingTime(Instant.now(clock))
                        .build()
        );
    }

    @Nullable
    private Run complete(Run run, String message) {
        return activeRunsRepository.tryUpdate(
                RunImpl.builder(run)
                        .withStatus(Run.Status.COMPLETE)
                        .withEndTime(Instant.now(clock))
                        .withMessage(message)
                        .build()
        );
    }

    @Nullable
    private Run fail(Run run, String reason) {
        return activeRunsRepository.tryUpdate(
                RunImpl.builder(run)
                        .withStatus(Run.Status.FAILED)
                        .withEndTime(Instant.now(clock))
                        .withMessage(reason)
                        .build()
        );
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
        private final Runnable executor;
        private final RunContext context;

        Runner(Run r, Runnable executor, RunContext context) {
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
            log.trace("Set status RUNNING to run {}", r.getRunId());
            Run run = tryRunning(r);
            if (run == null) {
                log.trace("No run with id {} for task {} found!", r.getRunId(), r.getTaskId());
                return;
            }
            if (!Objects.equals(run.getHost(), host)) {
                log.trace("Host {} lost lock on run {}", host, run.getRunId());
                return;
            }
            if (run.getStatus() != Run.Status.RUNNING) {
                log.error("Failed to mark run with id {} as RUNNING", r.getRunId());
                return;
            }
            RunContext.set(context);
            try {
                log.trace("Starting run {}", run.getRunId());
                executor.run();
                Run completed = complete(run, context.getMessage());
                if (completed == null) {
                    log.error("No run with id {} found!", r.getRunId());
                    return;
                }
                if (completed.getStatus() != Run.Status.COMPLETE) {
                    log.error("Failed to mark run with id {} as COMPLETED", r.getRunId());
                }
            } catch (Throwable e) {
                String userMessage = context.getMessage();
                String reason = String.format("Run %d of task %s failed with %s: %s",
                        run.getRunId(), run.getTaskId(),
                        e instanceof Error ? "ERROR" : "exception", e.getMessage());
                reason = userMessage == null ? reason : userMessage + "; " + reason;
                if (e instanceof Error) {
                    log.error(MarkerFactory.getMarker("FATAL"), reason, e);
                    fail(run, reason);
                    throw e;
                }
                log.error(reason, e);
                fail(run, reason);
            } finally {
                RunContext.clear();
                state.free(run);
            }
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
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    @Required
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Required
    public void setService(String service) {
        this.service = service;
    }

    public void setPickPeriod(long pickPeriod) {
        this.pickPeriod = pickPeriod;
    }
}
