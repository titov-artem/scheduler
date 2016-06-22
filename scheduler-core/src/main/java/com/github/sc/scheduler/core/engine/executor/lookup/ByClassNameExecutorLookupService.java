package com.github.sc.scheduler.core.engine.executor.lookup;

import com.github.sc.scheduler.core.engine.RunnableTaskExecutor;
import com.github.sc.scheduler.core.engine.TaskExecutor;
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
    public Optional<TaskExecutor> get(String name) {
        try {
            Class<?> executorClass = Class.forName(name);
            if (executorClass.getConstructor() == null) {
                log.warn("Executor class {} hasn't constructor without arguments", executorClass.getName());
                return Optional.empty();
            }

            if (Runnable.class.isAssignableFrom(executorClass)) {
                return Optional.of(new RunnableTaskExecutor((Runnable) executorClass.newInstance()));
            }
            if (TaskExecutor.class.isAssignableFrom(executorClass)) {
                return Optional.of((TaskExecutor) executorClass.newInstance());
            }

            log.warn("Executor class {} isn't Runnable or TaskExecutor", executorClass.getName());
        } catch (Exception e) {
            log.warn("Failed to get executor class by name " + name, e);
        }
        return Optional.empty();
    }
}
