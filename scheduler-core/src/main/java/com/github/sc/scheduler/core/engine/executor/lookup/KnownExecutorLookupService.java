package com.github.sc.scheduler.core.engine.executor.lookup;

import com.github.sc.scheduler.core.engine.RunnableTaskExecutor;
import com.github.sc.scheduler.core.engine.TaskExecutor;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Lookup task executor searching throw registered ones
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class KnownExecutorLookupService implements ExecutorLookupService {
    private static final Logger log = LoggerFactory.getLogger(KnownExecutorLookupService.class);

    private final Map<String, ?> knownExecutors;

    public KnownExecutorLookupService(Map<String, ?> knownExecutors) {
        knownExecutors.values().stream().forEach(
                ex -> Preconditions.checkArgument(
                        ex instanceof Runnable || ex instanceof TaskExecutor,
                        "Executor must implement Runnable or TaskExecutor"
                )
        );
        this.knownExecutors = new HashMap<>(knownExecutors);
    }

    @Override
    public Optional<TaskExecutor> get(String name) {
        Object executor = knownExecutors.get(name);
        if (executor == null) {
            log.warn("No known executor found for name {}", name);
        }
        if (executor instanceof TaskExecutor) {
            return Optional.of((TaskExecutor) executor);
        } else {
            return Optional.of(new RunnableTaskExecutor((Runnable) executor));
        }
    }
}
