package com.github.sc.scheduler.service;

import com.github.sc.scheduler.model.SchedulingParams;
import com.github.sc.scheduler.model.Task;
import com.github.sc.scheduler.model.TaskArgs;

import java.util.Optional;

public interface TaskService {

    Task createTask(Task task,
                    Optional<TaskArgs> args,
                    SchedulingParams params);

    void removeTask(String taskId);
}
