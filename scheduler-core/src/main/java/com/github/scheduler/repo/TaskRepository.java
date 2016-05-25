package com.github.scheduler.repo;

import com.github.scheduler.model.Task;

import java.util.Optional;

public interface TaskRepository {

    Optional<Task> get(String taskId);

    Task create(Task task);
}
