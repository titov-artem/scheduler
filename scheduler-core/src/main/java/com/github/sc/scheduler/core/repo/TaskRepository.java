package com.github.sc.scheduler.core.repo;

import com.github.sc.scheduler.core.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    List<Task> getAll();

    Optional<Task> get(String taskId);

    Task create(Task task);

    void remove(String taskId);
}
