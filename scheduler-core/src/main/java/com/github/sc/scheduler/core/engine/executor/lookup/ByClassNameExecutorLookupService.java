package com.github.sc.scheduler.core.engine.executor.lookup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Lookup task executor on class path, using it's name as class name
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class ByClassNameExecutorLookupService implements ExecutorLookupService {
    private static final Logger log = LoggerFactory.getLogger(ByClassNameExecutorLookupService.class);

    @Override
    public Optional<Runnable> get(String name) {
        try {
            Class<?> executorClass = Class.forName(name);
            if (!Runnable.class.isAssignableFrom(executorClass)) {
                log.warn("Executor class {} isn't runnable", executorClass.getName());
                return Optional.empty();
            }
            if (executorClass.getConstructor() == null) {
                log.warn("Executor class {} has constructor without arguments", executorClass.getName());
                return Optional.empty();
            }
            return Optional.of((Runnable) executorClass.newInstance());
        } catch (Exception e) {
            log.warn("Failed to get executor class by name " + name, e);
            return Optional.empty();
        }
    }
}
