package com.github.sc.scheduler.repo.memory;

import com.github.sc.scheduler.model.Task;
import com.github.sc.scheduler.model.TaskImpl;
import com.github.sc.scheduler.repo.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTaskRepository implements TaskRepository {

    private final ConcurrentMap<String, Task> data = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @Override
    public List<Task> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<Task> get(String taskId) {
        return Optional.ofNullable(data.get(taskId));
    }

    @Override
    public Task create(Task task) {
        Task out = new TaskImpl(Long.toString(idGenerator.incrementAndGet()), task.getName(), task.getEngineRequirements());
        data.put(out.getId(), out);
        return out;
    }

    @Override
    public void remove(String taskId) {
        data.remove(taskId);
    }
}
