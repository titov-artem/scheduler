package com.github.sc.scheduler.core.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

/**
 * Contains information about run time. With this params RunMaster will determine
 * when it is necessary to put this task into execution queue
 */
public interface SchedulingParams {

    String getTaskId();

    @Nonnull
    SchedulingType getType();

    /**
     * Contains string representation of necessary param for scheduling type. For example for
     * {@link SchedulingType#CRON} it will contains CRON expression, for {@link SchedulingType#PERIOD}
     * it will contains period and so on
     *
     * @return param value
     */
    @Nullable
    String getParam();

    @Nullable
    Instant getLastRunTime();

    /**
     * @return host, that acquire this task for creating its run and putting it into queue
     */
    @Nullable
    String getStartingHost();

    /**
     * @return time, when starting host acquire this task
     */
    @Nullable
    Instant getStartingTime();

    int getVersion();
}
