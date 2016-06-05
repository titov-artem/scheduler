package com.github.sc.scheduler.engine.executor.lookup;

import java.util.Optional;

/**
 * Lookup task executor by its name
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
    Optional<Runnable> get(String name);

}
