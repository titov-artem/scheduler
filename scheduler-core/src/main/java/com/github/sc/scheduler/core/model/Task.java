package com.github.sc.scheduler.core.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

/**
 * Task in scheduler. Contains internal scheduler field for correct task execution and locking
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public interface Task {

    @Nonnull
    String getId();

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
