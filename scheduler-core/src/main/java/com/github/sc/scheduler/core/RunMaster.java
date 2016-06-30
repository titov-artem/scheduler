package com.github.sc.scheduler.core;

import com.github.sc.scheduler.core.engine.Engine;
import com.github.sc.scheduler.core.model.*;
import com.github.sc.scheduler.core.repo.ActiveRunsRepository;
import com.github.sc.scheduler.core.repo.HistoryRunsRepository;
import com.github.sc.scheduler.core.repo.TaskRepository;
import com.github.sc.scheduler.core.repo.TimetableRepository;
import com.github.sc.scheduler.core.utils.LocalTaskScheduler;
import com.github.sc.scheduler.core.utils.SchedulerHostProvider;
import com.google.common.annotations.VisibleForTesting;
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
 * <p>
 * <p>
 * Algorithm description:
 * <p>
 * Main loop perform sequentially three steps:
 * <ol>
 * <li>
 * restore - restore two scheduler invariants:
 * <ul>
 * <li>each task has it's last run time</li>
 * <li>task is acquired not more then {@link RunMaster#START_INTERVAL}</li>
 * </ul>
 * </li>
 * <li>start tasks - find tasks to start and for each perform sequentially these operations:
 * <ol>
 * <li>acquire lock on task</li>
 * <li>terminate task start sequence if failed to acquire task lock</li>
 * <li>create run</li>
 * <li>update task's {@code lastRunTime}</li>
 * <li>release lock on task</li>
 * </ol>
 * </li>
 * <li>archive completed - move completed runs from active runs repository to history runs repository,
 * if run {@code queuedTime} less then it's task {@code lastRunTime}</li>
 * </ol>
 * <p>
 * Proof of thread safety and correctness:
 * <ol>
 * <li>
 * Restoring:
 * <p>
 * To maintain first invariant RunMaster look up task's runs in active runs repository. It's enough to scan
 * only active runs repository, because if RunMaster fails between run creation and updating task, run won't
 * be archived until it's {@code queuedTime} greater task's {@code lastRunTime}
 * <p>
 * To maintain second invariant scheduler release all task's locks after {@link RunMaster#START_INTERVAL}
 * </li>
 * <li>
 * Task starting:
 * <p>
 * To prevent creation of runs from two instances scheduler acquire lock on {@link RunMaster#START_INTERVAL}, so
 * another instance won't be able to acquire this lock for this time and create runs
 * </li>
 * <li>
 * Archiving:
 * <p>
 * When archiving, RunMaster use {@link HistoryRunsRepository#createIfNotExists(Collection)} method to prevent
 * duplicate creation
 * </li>
 * </ol>
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class RunMaster implements Runnable {
    /**
     * Time interval that is given to run master to create task run and store it into repository. If task
     * acquired more than this interval, RunMaster assume, that host, acquired this task gone away
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
        runUnsafe();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void run() {
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

    @VisibleForTesting
    protected void runUnsafe() {
        restore();
        startTasks();
        archiveCompletedRuns();
    }

    @VisibleForTesting
    protected void restore() {
        restoreLastRunTime();
        restoreStartingHost();
    }

    /**
     * For all tasks try to find last run and set task's {@code lastRunTime}.
     */
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

    @VisibleForTesting
    protected void startTasks() {
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
                .flatMap(o -> o.get().stream())
                .map(Run::getTaskId)
                .collect(toSet());
        log.trace("Started {} tasks: {}", startedTasks.size(), startedTasks);
        if (!engines.isEmpty()) {
            engineTouchingService.execute(() -> engines.forEach(Engine::touch));
        }
    }

    private Optional<List<Run>> tryStart(Task task, SchedulingParams param) {
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
        List<Run> runs = activeRunsRepository.create(RunImpl.newRun(param)
                .withQueuedTime(Instant.now(clock))
                .build(), param.getConcurrencyLevel(), param.getConcurrencyLevel());
        log.info("Task {} started. Created {} runs with ids: {}",
                task.getId(), runs.size(), runs.stream().map(Run::getRunId).collect(toList()));
        tryRelease(TaskImpl.builder(acquiredTask).withLastRunTime(Instant.now(clock)).build());
        return Optional.of(runs);
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

    @VisibleForTesting
    protected void archiveCompletedRuns() {
        // todo maybe recreate failed and hanged runs when archiving. It looks like we can
        // todo avoid transactions if we will do it here
        List<Run> runs = activeRunsRepository.getAll();
        Map<String, Task> taskById = taskRepository.getAll().stream().collect(toMap(Task::getId, t -> t));

        List<Run> archivableRuns = runs.stream()
                .filter(r -> r.getEndTime() != null)
                .filter(r -> {
                    Task task = taskById.get(r.getTaskId());
                    if (task == null) {
                        // if there no tasks in scheduler for this run anymore, we can safely archive it
                        return true;
                    }
                    Instant lastRunTime = task.getLastRunTime();
                    if (lastRunTime == null) {
                        // if lastRunTime is null, but run exists, it means, that starting it instance fails and
                        // this run is still necessary for task's lastRunTime restoring
                        return false;
                    }
                    // if run queuedTime is not before task's lastRunTime it also means that run necessary for
                    // restoring
                    return r.getQueuedTime().isBefore(lastRunTime);
                })
                .collect(toList());
        log.trace("Ready to archive {} completed runs: {}", archivableRuns.size(), archivableRuns.stream().map(Run::getRunId).collect(toList()));
        historyRunsRepository.createIfNotExists(archivableRuns);
        log.trace("Ready to remove completed runs");
        activeRunsRepository.remove(archivableRuns);
        log.info("Archived {} completed runs", archivableRuns.size());
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
    public void setHostProvider(SchedulerHostProvider hostProvider) {
        this.host = hostProvider.getHost();
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
