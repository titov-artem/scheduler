package com.github.sc.scheduler.repo;

import com.github.sc.scheduler.model.Task;

import java.util.Optional;

public interface TaskRepository {

    Optional<Task> get(String taskId);

    Task create(Task task);
}
