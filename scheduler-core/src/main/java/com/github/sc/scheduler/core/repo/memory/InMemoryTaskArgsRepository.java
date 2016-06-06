package com.github.sc.scheduler.core.repo.memory;

import com.github.sc.scheduler.core.model.TaskArgs;
import com.github.sc.scheduler.core.repo.TaskArgsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryTaskArgsRepository implements TaskArgsRepository {

    private final ConcurrentMap<String, TaskArgs> data = new ConcurrentHashMap<>();

    @Override
    public List<TaskArgs> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<TaskArgs> get(String taskId) {
        return Optional.ofNullable(data.get(taskId));
    }

    @Override
    public void save(String taskId, TaskArgs taskArgs) {
        data.put(taskId, taskArgs);
    }

    @Override
    public void remove(String taskId) {
        data.remove(taskId);
    }
}
