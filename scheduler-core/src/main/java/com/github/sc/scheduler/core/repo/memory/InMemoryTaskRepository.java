package com.github.sc.scheduler.core.repo.memory;

import com.github.sc.scheduler.core.model.Task;
import com.github.sc.scheduler.core.repo.TaskRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.sc.scheduler.core.model.TaskImpl.builder;
import static java.util.stream.Collectors.toList;

public class InMemoryTaskRepository implements TaskRepository {

    private final ConcurrentMap<String, TaskContainer> data = new ConcurrentHashMap<>();

    @Override
    public List<Task> getAll() {
        return data.values().stream().map(c -> c.task.get()).collect(toList());
    }

    @Override
    public Optional<Task> get(String taskId) {
        TaskContainer container = data.get(taskId);
        if (container == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(container.task.get());
    }

    @Override
    public void save(Task task) {
        data.put(task.getId(), new TaskContainer(task));
    }

    @Override
    public void remove(String taskId) {
        data.remove(taskId);
    }

    @Override
    public Task tryUpdate(Task task) {
        TaskContainer container = data.get(task.getId());
        if (container == null) {
            return null;
        }

        container.lock.lock();
        Task out = null;
        try {
            if (container.task.get().getVersion() == task.getVersion()) {
                container.task.set(builder(task)
                        .withVersion(task.getVersion() + 1)
                        .build());
            }
            out = container.task.get();
        } finally {
            container.lock.unlock();
        }
        return out;
    }

    @Override
    public void tryUpdateLastRunTime(String taskId, Instant lastRunTime) {
        TaskContainer container = data.get(taskId);
        if (container == null) return;

        container.lock.lock();
        try {
            if (container.task.get().getLastRunTime() == null ||
                    container.task.get().getLastRunTime().isBefore(lastRunTime)) {
                container.task.set(builder(container.task.get())
                        .withLastRunTime(lastRunTime)
                        .build());
            }
        } finally {
            container.lock.unlock();
        }
    }

    private class TaskContainer {
        final Lock lock = new ReentrantLock();
        final AtomicReference<Task> task = new AtomicReference<>();

        public TaskContainer(Task task) {
            this.task.set(task);
        }
    }

}
