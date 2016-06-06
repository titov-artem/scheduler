package com.github.sc.scheduler.core.service;

import com.github.sc.scheduler.core.model.SchedulingParams;
import com.github.sc.scheduler.core.model.Task;
import com.github.sc.scheduler.core.model.TaskArgs;

import java.util.Optional;

public interface TaskService {

    Task createTask(Task task,
                    Optional<TaskArgs> args,
                    SchedulingParams params);

    void removeTask(String taskId);
}
