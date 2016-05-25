package ru.yandex.qe.common.scheduler.repo.memory;

import ru.yandex.qe.common.scheduler.model.TaskArgs;
import ru.yandex.qe.common.scheduler.repo.TaskParamsRepository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryTaskParamsRepository implements TaskParamsRepository {

    private final ConcurrentMap<String, TaskArgs> data = new ConcurrentHashMap<>();

    @Override
    public Optional<TaskArgs> get(String taskId) {
        return Optional.ofNullable(data.get(taskId));
    }

    @Override
    public void save(String taskId, TaskArgs taskArgs) {
        data.put(taskId, taskArgs);
    }
}
