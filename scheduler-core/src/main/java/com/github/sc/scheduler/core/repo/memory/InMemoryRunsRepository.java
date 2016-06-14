package com.github.sc.scheduler.core.repo.memory;

import com.github.sc.scheduler.core.model.Run;
import com.github.sc.scheduler.core.model.RunImpl;
import com.github.sc.scheduler.core.repo.RunsRepository;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Simple in-memory repo with emulation of per document locks;
 */
public class InMemoryRunsRepository implements RunsRepository {

    protected final ReadWriteLock globalLock = new ReentrantReadWriteLock();
    protected final ConcurrentMap<Long, RunContainer> data = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @Override
    public Optional<Run> get(long runId) {
        RunContainer container = data.get(runId);
        if (container == null) {
            return Optional.empty();
        }
        return Optional.of(container.run.get());
    }

    @Override
    public List<Run> get(String taskId) {
        List<Run> out = new ArrayList<>();
        for (final RunContainer container : data.values()) {
            Run run = container.run.get();
            if (run.getTaskId().equals(taskId)) {
                out.add(run);
            }
        }
        return out;
    }

    @Override
    public List<Run> getAll() {
        List<Run> out = new ArrayList<>();
        for (final RunContainer container : data.values()) {
            out.add(container.run.get());
        }
        return out;
    }

    /**
     * Return multi map from task id to it's runs without any ordering guaranties
     * <p>
     * Use {@code taskIds.contains(String)} method to check does run match request or not
     *
     * @param taskIds
     * @return
     */
    @Override
    public Multimap<String, Run> getRuns(Collection<String> taskIds) {
        Multimap<String, Run> out = ArrayListMultimap.create();
        for (final RunContainer container : data.values()) {
            Run run = container.run.get();
            if (taskIds.contains(run.getTaskId())) {
                out.put(run.getTaskId(), run);
            }
        }
        return out;
    }

    @Override
    public Run create(Run run) {
        long id = idGenerator.incrementAndGet();
        Run out = RunImpl.builder(run)
                .withRunId(id)
                .build();
        data.put(id, new RunContainer(out));
        return out;
    }

    @Override
    public void createIfNotExists(Collection<Run> runs) {
        for (final Run run : runs) {
            if (run.getRunId() == Run.FAKE_RUN_ID) continue;
            data.putIfAbsent(run.getRunId(), new RunContainer(run));
        }
    }

    @Override
    public Optional<Run> tryUpdate(Run run) {
        RunContainer container = data.get(run.getRunId());
        if (container == null) return null;

        container.lock.lock();
        Run out = null;
        try {
            if (container.run.get().getVersion() == run.getVersion()) {
                container.run.set(RunImpl.builder(run)
                        .withVersion(run.getVersion() + 1)
                        .build());
            }
            out = container.run.get();
        } finally {
            container.lock.unlock();
        }
        return Optional.ofNullable(out);
    }

    @Override
    public void remove(Collection<Run> runs) {
        for (final Run run : runs) {
            data.remove(run.getRunId());
        }
    }

    protected static final class RunContainer {
        final Lock lock;
        AtomicReference<Run> run = new AtomicReference<>();

        private RunContainer(Run run) {
            this.run.set(run);
            this.lock = new ReentrantLock();
        }
    }
}
