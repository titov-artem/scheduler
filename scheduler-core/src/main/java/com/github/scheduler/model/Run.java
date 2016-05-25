package com.github.scheduler.model;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

/**
 * Single run of the task. Must be created with empty host and status PENDING
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

    @Nonnull
    Instant getQueuedTime();

    @Nullable
    String getHost();

    @Nullable
    Instant getAcquiredTime();

    @Nullable
    Instant getStartTime();

    @Nullable
    Instant getPingTime();

    @Nullable
    Instant getEndTime();

    @Nullable
    String getMessage();

    int getVersion();

    enum Status {
        PENDING, RUNNING, COMPLETE, FAILED
    }

}
