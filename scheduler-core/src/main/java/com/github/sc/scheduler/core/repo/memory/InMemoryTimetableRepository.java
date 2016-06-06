package com.github.sc.scheduler.core.repo.memory;

import com.github.sc.scheduler.core.model.SchedulingParams;
import com.github.sc.scheduler.core.repo.TimetableRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.sc.scheduler.core.model.SchedulingParamsImpl.builder;
import static java.util.stream.Collectors.toList;

public class InMemoryTimetableRepository implements TimetableRepository {

    private final ConcurrentMap<String, SchedulingParamsContainer> data = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @Override
    public List<SchedulingParams> getAll() {
        return data.values().stream().map(c -> c.params.get()).collect(toList());
    }

    @Override
    public Optional<SchedulingParams> getTask(String taskId) {
        SchedulingParamsContainer container = data.get(taskId);
        if (container == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(container.params.get());
    }

    @Override
    public void save(SchedulingParams params) {
        SchedulingParamsContainer p = data.putIfAbsent(params.getTaskId(), new SchedulingParamsContainer(params));
        if (p != null) {
            throw new IllegalArgumentException("Params for task " + params.getTaskId() + " already exists, use tryUpdate to modify them");
        }
    }

    @Override
    public SchedulingParams tryUpdate(SchedulingParams task) {
        SchedulingParamsContainer container = data.get(task.getTaskId());
        if (container == null) {
            return null;
        }

        container.lock.lock();
        SchedulingParams out = null;
        try {
            if (container.params.get().getVersion() == task.getVersion()) {
                container.params.set(builder(task)
                        .withVersion(task.getVersion() + 1)
                        .build());
            }
            out = container.params.get();
        } finally {
            container.lock.unlock();
        }
        return out;
    }

    @Override
    public void removeTask(String taskId) {
        data.remove(taskId);
    }

    @Override
    public void removeTasks(Collection<String> taskIds) {
        taskIds.forEach(data::remove);
    }

    @Override
    public void tryUpdateLastRunTime(String taskId, Instant lastRunTime) {
        SchedulingParamsContainer container = data.get(taskId);
        if (container == null) return;

        container.lock.lock();
        try {
            if (container.params.get().getLastRunTime() == null ||
                    container.params.get().getLastRunTime().isBefore(lastRunTime)) {
                container.params.set(builder(container.params.get())
                        .withLastRunTime(lastRunTime)
                        .build());
            }
        } finally {
            container.lock.unlock();
        }
    }

    private class SchedulingParamsContainer {
        final Lock lock = new ReentrantLock();
        final AtomicReference<SchedulingParams> params = new AtomicReference<>();

        public SchedulingParamsContainer(SchedulingParams params) {
            this.params.set(params);
        }
    }
}
