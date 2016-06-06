package com.github.sc.scheduler.core.service.impl;

import com.github.sc.scheduler.core.model.SchedulingParams;
import com.github.sc.scheduler.core.model.SchedulingParamsImpl;
import com.github.sc.scheduler.core.model.Task;
import com.github.sc.scheduler.core.model.TaskArgs;
import com.github.sc.scheduler.core.repo.TaskArgsRepository;
import com.github.sc.scheduler.core.repo.TaskRepository;
import com.github.sc.scheduler.core.repo.TimetableRepository;
import com.github.sc.scheduler.core.service.TaskService;
import com.github.sc.scheduler.core.utils.TransactionSupport;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;

public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private TaskArgsRepository taskArgsRepository;
    private TimetableRepository timetableRepository;

    private TransactionSupport transactionSupport;

    @Override
    public Task createTask(Task task,
                           Optional<TaskArgs> args,
                           SchedulingParams params) {
        return transactionSupport.doInTransaction(() -> {
            Task created = taskRepository.create(task);
            if (args.isPresent()) {
                taskArgsRepository.save(created.getId(), args.get());
            }
            timetableRepository.save(SchedulingParamsImpl.builder(created.getId(), params).build());
            return created;
        });
    }

    @Override
    public void removeTask(String taskId) {
        transactionSupport.doInTransaction(() -> {
            taskRepository.remove(taskId);
            taskArgsRepository.remove(taskId);
            timetableRepository.removeTask(taskId);
        });
    }

    /*
    Setters
     */
    @Required
    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Required
    public void setTaskArgsRepository(TaskArgsRepository taskArgsRepository) {
        this.taskArgsRepository = taskArgsRepository;
    }

    @Required
    public void setTimetableRepository(TimetableRepository timetableRepository) {
        this.timetableRepository = timetableRepository;
    }

    @Required
    public void setTransactionSupport(TransactionSupport transactionSupport) {
        this.transactionSupport = transactionSupport;
    }
}
