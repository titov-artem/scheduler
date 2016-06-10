package com.github.sc.scheduler.core.service;

import com.github.sc.scheduler.core.model.SchedulingParams;
import com.github.sc.scheduler.core.model.TaskArgs;

import java.util.Optional;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
public interface TaskService {

    SchedulingParams createTask(SchedulingParams params,
                                Optional<TaskArgs> args);

    void removeTask(String taskId);
}
