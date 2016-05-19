package ru.yandex.qe.common.scheduler.repo.memory;

import ru.yandex.qe.common.scheduler.model.Task;
import ru.yandex.qe.common.scheduler.model.TaskImpl;
import ru.yandex.qe.common.scheduler.repo.TaskRepository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTaskRepository implements TaskRepository {

    private final ConcurrentMap<String, Task> data = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

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
}
