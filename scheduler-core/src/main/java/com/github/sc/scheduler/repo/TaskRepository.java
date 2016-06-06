package com.github.sc.scheduler.repo;

import com.github.sc.scheduler.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    List<Task> getAll();

    Optional<Task> get(String taskId);

    Task create(Task task);

    void remove(String taskId);
}
