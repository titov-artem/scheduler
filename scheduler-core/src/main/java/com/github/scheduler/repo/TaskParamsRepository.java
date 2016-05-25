package com.github.scheduler.repo;

import com.github.scheduler.model.TaskArgs;

import java.util.Optional;

/**
 * Store params that will be passed to task
 */
public interface TaskParamsRepository {

    Optional<TaskArgs> get(String taskId);

    void save(String taskId, TaskArgs taskArgs);

}
