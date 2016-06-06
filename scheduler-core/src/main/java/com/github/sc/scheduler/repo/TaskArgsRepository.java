package com.github.sc.scheduler.repo;

import com.github.sc.scheduler.model.TaskArgs;

import java.util.List;
import java.util.Optional;

/**
 * Store params that will be passed to task
 */
public interface TaskArgsRepository {

    List<TaskArgs> getAll();

    Optional<TaskArgs> get(String taskId);

    void save(String taskId, TaskArgs taskArgs);

    void remove(String taskId);
}
