package com.github.scheduler;

import com.github.scheduler.engine.Engine;
import com.github.scheduler.model.*;
import com.github.scheduler.repo.ActiveRunsRepository;
import com.github.scheduler.repo.HistoryRunsRepository;
import com.github.scheduler.repo.TaskRepository;
import com.github.scheduler.repo.TimetableRepository;
import com.github.scheduler.utils.LocalTaskScheduler;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.scheduler.model.SchedulingParamsImpl.builder;
import static java.util.stream.Collectors.*;

/**
 * Responsible for next activities:
 * <ol>
 * <li>create runs for tasks from timetable</li>
 * <li>remove completed runs from queue to history</li>
 * </ol>
 * Only one {@code RunMaster} is permitted for timetable per host
 */
public class RunMaster implements Runnable {
    /**
     * Time interval that is given to run master to create task run and store it into repository
     */
    public static final Duration START_INTERVAL = Duration.ofMillis(TimeUnit.MINUTES.toMillis(1));
    private static final Logger log = LoggerFactory.getLogger(RunMaster.class);
    private static final long DEFAULT_PERIOD_SECONDS = TimeUnit.MINUTES.toSeconds(1);
    /* Dependencies */
    private TaskRepository taskRepository;
    private TimetableRepository timetableRepository;
    private ActiveRunsRepository activeRunsRepository;
    private HistoryRunsRepository historyRunsRepository;
    /**
     * Engines, which can executes task, if any are presented on this instance
     */
    private Collection<Engine> engines = Collections.emptyList();

    private Clock clock = Clock.systemDefaultZone();

    /* Properties */
    /**
     * Run master wake up interval in seconds
     */
    private long periodSeconds = DEFAULT_PERIOD_SECONDS;
    private String host;

    /* Internal fields */
    /**
     * Main loop scheduler
     */
    private LocalTaskScheduler localTaskScheduler;
    /**
     * Service for touching local engines immediately after runs creation
     * to speed up runs execution
     */
    private ExecutorService engineTouchingService;
    /**
     * Main loop lock
     */
    private Lock runLock = new ReentrantLock();
    private AtomicBoolean started = new AtomicBoolean();

    @PostConstruct
    public void start() {
        started.set(true);
        if (!engines.isEmpty()) {
            engineTouchingService = Executors.newFixedThreadPool(1);
        }
        localTaskScheduler = new LocalTaskScheduler(1);
        localTaskScheduler.scheduleWithFixRate(getClass().getSimpleName(), this, 0, periodSeconds, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() {
        localTaskScheduler.shutdownNow();
        engineTouchingService.shutdownNow();
        started.set(false);
    }

    /**
     * Force run master perform task scheduling
     */
    public void touch() {
        runInternal();
    }

    @Override
    public void run() {
        runInternal();
    }

    private void runInternal() {
        try {
            if (runLock.tryLock(1, TimeUnit.MILLISECONDS)) {
                try {
                    restore();
                    startTasks();
                    archiveCompletedRuns();
                } finally {
                    runLock.unlock();
                }
            }
        } catch (InterruptedException ignore) {
        }
    }

    private void restore() {
        restoreLastRunTime();
        restoreStartingHost();
    }

    private void restoreLastRunTime() {
        List<SchedulingParams> tasks = timetableRepository.getTasks();
        Multimap<String, Run> activeRuns = activeRunsRepository.getRuns(
                tasks.stream().map(SchedulingParams::getTaskId).collect(toList())
        );
        Map<String, Run> lastActiveRuns = new HashMap<>();
        for (final Map.Entry<String, Run> entry : activeRuns.entries()) {
            Run last = lastActiveRuns.get(entry.getKey());
            if (last == null
                    || last.getQueuedTime().isBefore(entry.getValue().getQueuedTime())) {
                lastActiveRuns.put(entry.getKey(), entry.getValue());
            }
        }
        for (final SchedulingParams task : tasks) {
            Instant lastActiveRunTime = getLastRunTime(task, lastActiveRuns);
            if (!Objects.equals(task.getLastRunTime(), lastActiveRunTime)) {
                // should be rare, so not batch for simplicity
                timetableRepository.tryUpdateLastRunTime(task.getTaskId(), lastActiveRunTime);
            }
        }
    }

    private Instant getLastRunTime(SchedulingParams task, Map<String, Run> lastActiveRuns) {
        Run run = lastActiveRuns.get(task.getTaskId());
        if (run == null) return task.getLastRunTime();
        if (task.getLastRunTime() == null) return run.getQueuedTime();
        return run.getQueuedTime().isBefore(task.getLastRunTime()) ? task.getLastRunTime() : run.getQueuedTime();
    }

    private void restoreStartingHost() {
        final Instant now = Instant.now(clock);
        List<SchedulingParams> tasks = timetableRepository.getTasks();
        tasks.stream()
                .filter(t -> t.getStartingHost() != null)
                .filter(t -> !isAfter(t.getStartingTime(), t.getLastRunTime()) ||
                        t.getStartingTime().plus(START_INTERVAL).isBefore(now))
                .forEach(this::tryRelease);
    }

    /**
     * Return is a after b. If b is null, then return true
     */
    private boolean isAfter(@Nonnull Instant a, @Nullable Instant b) {
        return b == null || a.isAfter(b);
    }

    private void startTasks() {
        final Instant now = Instant.now(clock);
        // load time table
        log.info("Run master begin task starting at {}", now);
        List<SchedulingParams> tasks = timetableRepository.getTasks();
        log.info("Found {} tasks.", tasks.size());
        // found tasks to start
        Map<String, SchedulingParams> tasksToStart = tasks.stream()
                .filter(t -> t.getStartingHost() == null)
                .filter(t -> t.getType().canStart(t.getParam(), now, t.getLastRunTime(), periodSeconds, clock.getZone()))
                .collect(toMap(SchedulingParams::getTaskId, t -> t));
        log.debug("Tasks to start ({}): {}", tasksToStart.size(), tasksToStart);
        // create runs for them and put into queue
        Set<String> startedTasks = tasksToStart.values().stream()
                .map(this::tryStart)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Run::getTaskId)
                .collect(toSet());
        log.trace("Started {} tasks: {}", startedTasks.size(), startedTasks);
        // remove ONCE tasks
        // todo think about removing this task when archiving
        List<String> startedRunOnceTasks = tasksToStart.entrySet().stream()
                .filter(t -> t.getValue().getType() == SchedulingType.ONCE)
                .filter(t -> startedTasks.contains(t.getKey()))
                .map(Map.Entry::getKey)
                .collect(toList());
        log.trace("Started {} run ONCE tasks: {}", startedRunOnceTasks.size(), startedRunOnceTasks);
        timetableRepository.removeTasks(startedRunOnceTasks);
        if (!engines.isEmpty()) {
            engineTouchingService.execute(() -> engines.forEach(Engine::touch));
        }
    }

    private Optional<Run> tryStart(SchedulingParams param) {
        SchedulingParams acquiredParams = tryAcquire(param);
        if (acquiredParams == null) {
            log.warn("Failed to acquired task, because no such task found: " + param.getTaskId());
            return Optional.empty();
        }
        if (!Objects.equals(acquiredParams.getStartingHost(), host)) {
            log.debug("Other instance acquire task {}", acquiredParams.getTaskId());
            return Optional.empty();
        }
        log.debug("Task {} acquired on host {}", acquiredParams.getTaskId(), acquiredParams.getStartingHost());
        Optional<Task> task = taskRepository.get(param.getTaskId());
        if (!task.isPresent()) {
            log.warn("Failed to get task for params {}", param);
            return Optional.empty();
        }
        Run run = activeRunsRepository.create(RunImpl.newRun(task.get())
                .withQueuedTime(Instant.now(clock))
                .build());
        log.info("Task {} started. Run: {}", task.get().getId(), run);
        tryRelease(builder(acquiredParams).withLastRunTime(Instant.now(clock)).build());
        return Optional.of(run);
    }

    private SchedulingParams tryRelease(SchedulingParams params) {
        return timetableRepository.tryUpdate(builder(params)
                .withStartingHost(null)
                .withStartingTime(null)
                .build());
    }

    private SchedulingParams tryAcquire(SchedulingParams params) {
        return timetableRepository.tryUpdate(builder(params)
                .withStartingHost(host)
                .withStartingTime(Instant.now(clock))
                .build());
    }

    private void archiveCompletedRuns() {
        List<Run> runs = activeRunsRepository.getRuns();
        List<Run> completedRuns = runs.stream()
                .filter(r -> r.getEndTime() != null)
                .collect(toList());
        log.trace("Ready to archive {} completed runs: {}", completedRuns.size(), completedRuns.stream().map(Run::getRunId).collect(toList()));
        historyRunsRepository.createIfNotExists(completedRuns);
        log.trace("Ready to remove completed runs");
        activeRunsRepository.remove(completedRuns);
        log.info("Archived {} completed runs", completedRuns.size());
    }

    /* Setters */
    @Required
    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Required
    public void setTimetableRepository(TimetableRepository timetableRepository) {
        this.timetableRepository = timetableRepository;
    }

    @Required
    public void setActiveRunsRepository(ActiveRunsRepository activeRunsRepository) {
        this.activeRunsRepository = activeRunsRepository;
    }

    @Required
    public void setHistoryRunsRepository(HistoryRunsRepository historyRunsRepository) {
        this.historyRunsRepository = historyRunsRepository;
    }

    /**
     * Set up engines, which will be waked up, when run master schedule any task
     *
     * @param engines engines to wake up
     * @throws IllegalStateException if run master is running
     */
    public void setEngines(Collection<Engine> engines) {
        if (started.get()) throw new IllegalStateException("Can't set engines while run master is running");
        this.engines = engines;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Required
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Set wake up periodSeconds in seconds
     *
     * @param periodSeconds periodSeconds in seconds
     */
    public void setPeriodSeconds(long periodSeconds) {
        this.periodSeconds = periodSeconds;
    }
}
