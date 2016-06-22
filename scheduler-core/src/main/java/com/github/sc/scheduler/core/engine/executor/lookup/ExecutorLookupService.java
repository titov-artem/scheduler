package com.github.sc.scheduler.core.engine.executor.lookup;

import com.github.sc.scheduler.core.engine.TaskExecutor;

import java.util.Optional;

/**
 * Lookup task executor by its name
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public interface ExecutorLookupService {

    /**
     * Return executor by it's name.
     * <p>
     * <b>Implementation mustn't throw any exception</b>
     * </p>
     *
     * @param name executor name
     * @return executor if it found by service, or empty otherwise.
     */
    Optional<TaskExecutor> get(String name);

}
