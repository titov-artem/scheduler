package com.github.scheduler.engine.executor.lookup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Lookup task executor searching throw registered ones
 */
public class KnownExecutorLookupService implements ExecutorLookupService {
    private static final Logger log = LoggerFactory.getLogger(KnownExecutorLookupService.class);

    private final Map<String, Runnable> knownExecutors;

    public KnownExecutorLookupService(Map<String, Runnable> knownExecutors) {
        this.knownExecutors = new HashMap<>(knownExecutors);
    }

    @Override
    public Optional<Runnable> get(String name) {
        Runnable executor = knownExecutors.get(name);
        if (executor == null) {
            log.warn("No known executor found for name {}", name);
        }
        return Optional.ofNullable(executor);
    }
}
