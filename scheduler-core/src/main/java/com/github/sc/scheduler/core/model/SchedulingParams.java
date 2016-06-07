package com.github.sc.scheduler.core.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Contains fields, that necessary for task scheduling and that don't change by run master while task scheduling
 */
public interface SchedulingParams {

    String getTaskId();

    @Nonnull
    Optional<String> getName();

    @Nonnull
    EngineRequirements getEngineRequirements();

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

    // todo maybe put it in task, or put engine requirements here

    /**
     * Number of task that can be executed concurrently. Run master will start this number of tasks on the first
     * run and then will keep this level on each next run time. For example if on the next run time there will be some
     * tasks, which haven't completed yet, then run master will start {@code cuncurrenclyLevel} minus that number of
     * tasks
     * <p>
     * For disallowing concurrent execution set it to 0
     *
     * @return concurrency level
     */
    int getConcurrencyLevel();

    /**
     * Does scheduler have to restart task after fail automatically. The run will be restarted if and only if it will not
     * violate concurrency level constraint
     *
     * @return true if scheduler have to restart run after fail automatically.
     * @see #getConcurrencyLevel()
     */
    boolean isRestartOnFail();

    /**
     * Does scheduler have to restart task after reboot of engine, that executes this task automatically. The run will
     * be restarted if and only if it will not violate concurrency level constraint
     *
     * @return true if scheduler have to restart run after engine reboot.
     * @see #getConcurrencyLevel()
     */
    boolean isRestartOnReboot();
}
