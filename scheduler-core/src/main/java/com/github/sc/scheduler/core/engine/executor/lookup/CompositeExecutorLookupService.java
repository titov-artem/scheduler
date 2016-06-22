package com.github.sc.scheduler.core.engine.executor.lookup;

import com.github.sc.scheduler.core.engine.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Lookup task executor in underling registered services in order of registration, until the first one will
 * be found
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class CompositeExecutorLookupService implements ExecutorLookupService {
    private static final Logger log = LoggerFactory.getLogger(CompositeExecutorLookupService.class);

    private final List<ExecutorLookupService> services;

    public CompositeExecutorLookupService(List<ExecutorLookupService> services) {
        this.services = new ArrayList<>(services);
    }

    @Override
    public Optional<TaskExecutor> get(String name) {
        for (final ExecutorLookupService service : services) {
            Optional<TaskExecutor> executor = service.get(name);
            if (executor.isPresent()) {
                return executor;
            }
        }
        log.warn("No executor found for name {}", name);
        return Optional.empty();
    }
}
