package com.github.sc.scheduler.core;

import com.github.sc.scheduler.core.engine.Engine;
import com.github.sc.scheduler.core.model.*;
import com.github.sc.scheduler.core.repo.ActiveRunsRepository;
import com.github.sc.scheduler.core.repo.HistoryRunsRepository;
import com.github.sc.scheduler.core.repo.TaskRepository;
import com.github.sc.scheduler.core.repo.TimetableRepository;
import com.github.sc.scheduler.core.utils.LocalTaskScheduler;
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
        List<Task> tasks = taskRepository.getAll();
        Multimap<String, Run> activeRuns = activeRunsRepository.getRuns(
                tasks.stream().map(Task::getId).collect(toList())
        );
        Map<String, Run> lastActiveRuns = new HashMap<>();
        for (final Map.Entry<String, Run> entry : activeRuns.entries()) {
            Run last = lastActiveRuns.get(entry.getKey());
            if (last == null
                    || last.getQueuedTime().isBefore(entry.getValue().getQueuedTime())) {
                lastActiveRuns.put(entry.getKey(), entry.getValue());
            }
        }
        for (final Task task : tasks) {
            Instant lastActiveRunTime = getLastRunTime(task, lastActiveRuns);
            if (!Objects.equals(task.getLastRunTime(), lastActiveRunTime)) {
                // should be rare, so not batch for simplicity
                taskRepository.tryUpdateLastRunTime(task.getId(), lastActiveRunTime);
            }
        }
    }

    private Instant getLastRunTime(Task task, Map<String, Run> lastActiveRuns) {
        Run run = lastActiveRuns.get(task.getId());
        if (run == null) return task.getLastRunTime();
        if (task.getLastRunTime() == null) return run.getQueuedTime();
        return run.getQueuedTime().isBefore(task.getLastRunTime()) ? task.getLastRunTime() : run.getQueuedTime();
    }

    private void restoreStartingHost() {
        final Instant now = Instant.now(clock);
        List<Task> tasks = taskRepository.getAll();
        tasks.stream()
                .filter(t -> t.getStartingHost() != null)
                .filter(t -> !isAfter(t.getStartingTime(), t.getLastRunTime()) ||
                        t.getStartingTime().plus(START_INTERVAL).isBefore(now))
                .forEach(this::tryRelease);
    }

    private void startTasks() {
        final Instant now = Instant.now(clock);
        // load time table
        log.info("Run master begin task starting at {}", now);
        List<Task> tasks = taskRepository.getAll();
        Map<String, SchedulingParams> paramsById = timetableRepository.getAll().stream().collect(toMap(SchedulingParams::getTaskId, i -> i));
        log.info("Found {} tasks.", tasks.size());
        // found tasks to start
        Map<String, Task> tasksToStart = tasks.stream()
                .filter(t -> t.getStartingHost() == null)
                .filter(t -> {
                    if (!paramsById.containsKey(t.getId())) log.error("Missing params for task {}", t.getId());
                    return paramsById.containsKey(t.getId());
                })
                .filter(t -> {
                    SchedulingParams params = paramsById.get(t.getId());
                    return params.getType().canStart(params.getParam(), now, t.getLastRunTime(), periodSeconds, clock.getZone());
                })
                .collect(toMap(Task::getId, t -> t));
        log.debug("Tasks to start ({}): {}", tasksToStart.size(), tasksToStart);
        // create runs for them and put into queue
        Set<String> startedTasks = tasksToStart.values().stream()
                .map(t -> tryStart(t, paramsById.get(t.getId())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Run::getTaskId)
                .collect(toSet());
        log.trace("Started {} tasks: {}", startedTasks.size(), startedTasks);
        if (!engines.isEmpty()) {
            engineTouchingService.execute(() -> engines.forEach(Engine::touch));
        }
    }

    private Optional<Run> tryStart(Task task, SchedulingParams param) {
        Task acquiredTask = tryAcquire(task);
        if (acquiredTask == null) {
            log.warn("Failed to acquired task, because no such task found: " + param.getTaskId());
            return Optional.empty();
        }
        if (!Objects.equals(acquiredTask.getStartingHost(), host)) {
            log.debug("Other instance acquire task {}", acquiredTask.getId());
            return Optional.empty();
        }
        log.debug("Task {} acquired on host {}", acquiredTask.getId(), acquiredTask.getStartingHost());
        Run run = activeRunsRepository.create(RunImpl.newRun(param)
                .withQueuedTime(Instant.now(clock))
                .build());
        log.info("Task {} started. Run: {}", task.getId(), run);
        tryRelease(TaskImpl.builder(acquiredTask).withLastRunTime(Instant.now(clock)).build());
        return Optional.of(run);
    }

    private Task tryAcquire(Task task) {
        return taskRepository.tryUpdate(TaskImpl.builder(task)
                .withStartingHost(host)
                .withStartingTime(Instant.now(clock))
                .build());
    }

    private Task tryRelease(Task task) {
        return taskRepository.tryUpdate(TaskImpl.builder(task)
                .withStartingHost(null)
                .withStartingTime(null)
                .build());
    }

    private void archiveCompletedRuns() {
        List<Run> runs = activeRunsRepository.getAll();
        List<Run> completedRuns = runs.stream()
                .filter(r -> r.getEndTime() != null)
                .collect(toList());
        log.trace("Ready to archive {} completed runs: {}", completedRuns.size(), completedRuns.stream().map(Run::getRunId).collect(toList()));
        historyRunsRepository.createIfNotExists(completedRuns);
        log.trace("Ready to remove completed runs");
        activeRunsRepository.remove(completedRuns);
        log.info("Archived {} completed runs", completedRuns.size());
    }

    /* Utility methods */

    /**
     * Return is a after b. If b is null, then return true
     */
    private boolean isAfter(@Nonnull Instant a, @Nullable Instant b) {
        return b == null || a.isAfter(b);
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
