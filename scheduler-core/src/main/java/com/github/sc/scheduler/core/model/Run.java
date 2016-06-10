package com.github.sc.scheduler.core.model;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

/**
 * Single run of the task. Must be created with empty host and status PENDING
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public interface Run {

    long FAKE_RUN_ID = -1;

    long getRunId();

    @Nonnull
    String getTaskId();

    @Nonnull
    EngineRequirements getEngineRequirements();

    @Nonnull
    Status getStatus();

    /**
     * Does scheduler have to restart task after fail automatically. The run will be restarted if and only if it will not
     * break concurrency level constraint
     *
     * @return true if scheduler have to restart run after fail automatically.
     * @see SchedulingParams#getConcurrencyLevel()
     */
    boolean isRestartOnFail();

    /**
     * Does scheduler have to restart task after reboot of engine, that executes this task automatically. The run will
     * be restarted if and only if it will not break concurrency level constraint
     *
     * @return true if scheduler have to restart run after engine reboot.
     * @see SchedulingParams#getConcurrencyLevel()
     */
    boolean isRestartOnReboot();

    /**
     * @return time when run was added to queue
     */
    @Nonnull
    Instant getQueuedTime();

    /**
     * @return host by which run was acquired and where it will be executed
     */
    @Nullable
    String getHost();

    /**
     * @return time when run was acquired by engine to execution
     */
    @Nullable
    Instant getAcquiredTime();

    /**
     * @return time when run was started on engine
     */
    @Nullable
    Instant getStartTime();

    /**
     * @return time when run was ping last time from engine, that executes it
     */
    @Nullable
    Instant getPingTime();

    /**
     * @return time when run was complete
     */
    @Nullable
    Instant getEndTime();

    /**
     * @return message passed from run execution (can be set by executor,
     * or contains error message from system)
     */
    @Nullable
    String getMessage();

    int getVersion();

    enum Status {
        PENDING, RUNNING, COMPLETE, HANGED, FAILED
    }

}
