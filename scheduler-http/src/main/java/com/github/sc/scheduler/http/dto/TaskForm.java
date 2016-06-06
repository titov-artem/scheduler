package com.github.sc.scheduler.http.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.sc.scheduler.model.SchedulingParams;
import com.github.sc.scheduler.model.Task;
import com.github.sc.scheduler.model.TaskArgs;
import com.github.sc.scheduler.model.TaskImpl;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
public class TaskForm {

    private final String id;

    @Nullable
    private final String name;

    @Nullable
    private final TaskArgsDto args;

    private final EngineRequirementsDto engineRequirements;

    private final SchedulingParamsDto schedulingParams;

    @JsonCreator
    public TaskForm(@JsonProperty("id") String id,
                    @JsonProperty("name") @Nullable String name,
                    @JsonProperty("args") @Nullable TaskArgsDto args,
                    @JsonProperty("engineRequirements") EngineRequirementsDto engineRequirements,
                    @JsonProperty("schedulingParams") SchedulingParamsDto schedulingParams) {
        this.id = id;
        this.name = name;
        this.args = args;
        this.engineRequirements = engineRequirements;
        this.schedulingParams = schedulingParams;
    }

    public TaskForm(Task task,
                    @Nullable TaskArgs taskArgs,
                    SchedulingParams schedulingParams) {
        this(
                task.getId(),
                task.getName().isPresent() ? task.getName().get() : null,
                taskArgs != null ? new TaskArgsDto(taskArgs) : null,
                new EngineRequirementsDto(task.getEngineRequirements()),
                new SchedulingParamsDto(schedulingParams)
        );
    }

    @JsonGetter
    public String getId() {
        return id;
    }

    @Nullable
    @JsonGetter
    public String getName() {
        return name;
    }

    @Nullable
    @JsonGetter("args")
    public TaskArgsDto getArgsDto() {
        return args;
    }

    @JsonGetter
    public EngineRequirementsDto getEngineRequirements() {
        return engineRequirements;
    }

    @JsonGetter("schedulingParams")
    public SchedulingParamsDto getSchedulingParamsDto() {
        return schedulingParams;
    }

    @JsonIgnore
    public Task getTask() {
        return new TaskImpl(id, Optional.ofNullable(name), engineRequirements.toEngineRequirements());
    }

    @JsonIgnore
    public Optional<TaskArgs> getArgs() {
        return Optional.ofNullable(args.toTaskArgs());
    }

    @JsonIgnore
    public SchedulingParams getSchedulingParams() {
        return schedulingParams.toSchedulingParams();
    }
}
