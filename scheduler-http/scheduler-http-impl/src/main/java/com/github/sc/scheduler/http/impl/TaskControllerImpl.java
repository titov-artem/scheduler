package com.github.sc.scheduler.http.impl;

import com.github.sc.scheduler.core.model.Run;
import com.github.sc.scheduler.core.model.SchedulingParams;
import com.github.sc.scheduler.core.model.Task;
import com.github.sc.scheduler.core.model.TaskArgs;
import com.github.sc.scheduler.core.repo.ActiveRunsRepository;
import com.github.sc.scheduler.core.repo.TaskArgsRepository;
import com.github.sc.scheduler.core.repo.TaskRepository;
import com.github.sc.scheduler.core.repo.TimetableRepository;
import com.github.sc.scheduler.core.service.TaskService;
import com.github.sc.scheduler.http.TaskController;
import com.github.sc.scheduler.http.dto.TaskForm;
import com.github.sc.scheduler.http.dto.TaskView;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;

public class TaskControllerImpl implements TaskController {

    private TaskService taskService;

    private TaskRepository taskRepository;
    private TaskArgsRepository taskArgsRepository;
    private TimetableRepository timetableRepository;
    private ActiveRunsRepository activeRunsRepository;

    @Override
    public List<TaskView> get() {
        Map<String, Task> tasks = taskRepository.getAll().stream().collect(toMap(Task::getId, i -> i));
        Map<String, TaskArgs> args = taskArgsRepository.getAll().stream().collect(toMap(TaskArgs::getTaskId, i -> i));
        Map<String, SchedulingParams> params = timetableRepository.getAll().stream().collect(toMap(SchedulingParams::getTaskId, i -> i));
        ListMultimap<String, Run> activeRuns = activeRunsRepository.getAll()
                .stream()
                .filter(run -> run.getStatus() == Run.Status.PENDING || run.getStatus() == Run.Status.RUNNING)
                .collect(
                        ArrayListMultimap::create,
                        (map, run) -> map.put(run.getTaskId(), run),
                        (map1, map2) -> map1.putAll(map2)
                );

        List<TaskView> out = new ArrayList<>();
        tasks.forEach((id, task) -> out.add(new TaskView(
                task,
                args.get(id),
                params.get(id),
                activeRuns.get(id)
        )));
        return out;
    }

    @Override
    public TaskView get(String taskId) {
        Optional<Task> maybeTask = taskRepository.get(taskId);
        if (!maybeTask.isPresent()) {
            throw new NotFoundException();
        }
        Task task = maybeTask.get();
        Optional<TaskArgs> args = taskArgsRepository.get(task.getId());
        Optional<SchedulingParams> params = timetableRepository.getTask(task.getId());
        return new TaskView(task, args.orElse(null), params.get(), activeRunsRepository.get(taskId));
    }

    @Override
    public TaskView create(TaskForm taskForm) {
        Task task = taskService.createTask(
                taskForm.getTask(),
                taskForm.getArgs(),
                taskForm.getSchedulingParams()
        );
        Optional<TaskArgs> args = taskArgsRepository.get(task.getId());
        Optional<SchedulingParams> params = timetableRepository.getTask(task.getId());
        return new TaskView(task, args.orElse(null), params.get(), emptyList());
    }

    @Override
    public void delete(String taskId) {
        taskService.removeTask(taskId);
    }

    /*
        Setters
         */
    @Required
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

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
    public void setActiveRunsRepository(ActiveRunsRepository activeRunsRepository) {
        this.activeRunsRepository = activeRunsRepository;
    }
}
