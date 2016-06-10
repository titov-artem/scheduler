package com.github.sc.scheduler.core.repo;

import com.github.sc.scheduler.core.model.TaskArgs;

import java.util.List;
import java.util.Optional;

/**
 * Store args that will be passed to task
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public interface TaskArgsRepository {

    List<TaskArgs> getAll();

    Optional<TaskArgs> get(String taskId);

    void save(String taskId, TaskArgs taskArgs);

    void remove(String taskId);
}
