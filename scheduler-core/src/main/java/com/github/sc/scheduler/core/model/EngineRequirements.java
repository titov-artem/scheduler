package com.github.sc.scheduler.core.model;

import javax.annotation.Nonnull;

/**
 * Requirements for Engine that will start task
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public interface EngineRequirements {

    /**
     * @return task weight. Engine must have its free capacity greater than this weight, when starts task
     */
    int getWeight();

    /**
     * To run task engine must have such executor. Availability of executor will be checked after picking
     * task on engine, so task can fail if all other requirements will be met, but executor will not be
     * found
     *
     * @return executor name
     */
    @Nonnull
    String getExecutor();

    @Nonnull
    String getService();

}
