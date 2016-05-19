package ru.yandex.qe.common.scheduler.model;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

/**
 * Отдельный запуск задачи. Создаётся с пустым хостом и статусом PENDING
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
