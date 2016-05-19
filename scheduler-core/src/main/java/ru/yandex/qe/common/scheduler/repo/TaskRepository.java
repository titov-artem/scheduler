package ru.yandex.qe.common.scheduler.repo;

import ru.yandex.qe.common.scheduler.model.Task;

import java.util.Optional;

public interface TaskRepository {

    Optional<Task> get(String taskId);

    Task create(Task task);
}
