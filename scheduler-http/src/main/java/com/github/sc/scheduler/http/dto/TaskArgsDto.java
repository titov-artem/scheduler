package com.github.sc.scheduler.http.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.sc.scheduler.model.TaskArgs;
import com.github.sc.scheduler.model.TaskArgsImpl;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Immutable
public class TaskArgsDto {

    private final String taskId;
    private final Map<String, String> args;

    @JsonCreator
    public TaskArgsDto(@JsonProperty("taskId") String taskId,
                       @JsonProperty("args") Map<String, String> args) {
        this.taskId = taskId;
        this.args = new HashMap<>(args);
    }

    public TaskArgsDto(TaskArgs taskArgs) {
        this.taskId = taskArgs.getTaskId();
        this.args = new HashMap<>();
        for (String name : taskArgs.getNames()) {
            args.put(name, taskArgs.get(name));
        }
    }

    @JsonGetter
    public String getTaskId() {
        return taskId;
    }

    @JsonGetter
    public Map<String, String> getArgs() {
        return Collections.unmodifiableMap(args);
    }

    public TaskArgs toTaskArgs() {
        TaskArgsImpl.Builder builder = TaskArgsImpl.builder(taskId);
        args.forEach(builder::append);
        return builder.build();
    }
}
