package com.github.sc.scheduler.core.repo.memory;

import com.github.sc.scheduler.core.model.SchedulingParams;
import com.github.sc.scheduler.core.model.SchedulingParamsImpl;
import com.github.sc.scheduler.core.repo.TimetableRepository;
import com.github.sc.scheduler.core.utils.IdGenerator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.stream.Collectors.toList;

public class InMemoryTimetableRepository implements TimetableRepository {

    private final ConcurrentMap<String, SchedulingParams> data = new ConcurrentHashMap<>();

    @Override
    public List<SchedulingParams> getAll() {
        return data.values().stream().collect(toList());
    }

    @Override
    public Optional<SchedulingParams> getTask(String taskId) {
        return Optional.ofNullable(data.get(taskId));
    }

    @Override
    public SchedulingParams save(SchedulingParams params) {
        SchedulingParams out = SchedulingParamsImpl.builder(params).withTaskId(IdGenerator.nextId()).build();
        data.put(out.getTaskId(), out);
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
}
