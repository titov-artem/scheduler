package com.github.sc.scheduler.http.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.sc.scheduler.core.model.SchedulingParams;
import com.github.sc.scheduler.core.model.SchedulingParamsImpl;
import com.github.sc.scheduler.core.model.SchedulingType;
import com.github.sc.scheduler.core.model.TaskArgs;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 * @see SchedulingParams
 */
@Immutable
public class TaskForm {

    private final String id;
    @Nullable
    private final String name;
    private final EngineRequirementsDto engineRequirements;
    private final SchedulingType type;
    @Nullable
    private final String param;
    private final int concurrencyLevel;
    private final boolean restartOnFail;
    private final boolean restartOnReboot;
    @Nullable
    private final TaskArgsDto args;

    @JsonCreator
    public TaskForm(@JsonProperty("id") String id,
                    @JsonProperty("name") @Nullable String name,
                    @JsonProperty("engineRequirements") EngineRequirementsDto engineRequirements,
                    @JsonProperty("type") SchedulingType type,
                    @JsonProperty("param") @Nullable String param,
                    @JsonProperty("concurrencyLevel") int concurrencyLevel,
                    @JsonProperty("restartOnFail") boolean restartOnFail,
                    @JsonProperty("restartOnReboot") boolean restartOnReboot,
                    @JsonProperty("args") @Nullable TaskArgsDto args) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.param = param;
        this.concurrencyLevel = concurrencyLevel;
        this.restartOnFail = restartOnFail;
        this.restartOnReboot = restartOnReboot;
        this.args = args;
        this.engineRequirements = engineRequirements;
    }

    public TaskForm(SchedulingParams params,
                    @Nullable TaskArgs taskArgs) {
        this(
                params.getTaskId(),
                params.getName().orElse(null),
                new EngineRequirementsDto(params.getEngineRequirements()),
                params.getType(),
                params.getParam(),
                params.getConcurrencyLevel(),
                params.isRestartOnFail(),
                params.isRestartOnReboot(),
                taskArgs != null ? new TaskArgsDto(taskArgs) : null
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

    @JsonGetter
    public SchedulingType getType() {
        return type;
    }

    @Nullable
    @JsonGetter
    public String getParam() {
        return param;
    }

    @JsonGetter
    public int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    @JsonGetter
    public boolean isRestartOnFail() {
        return restartOnFail;
    }

    @JsonGetter
    public boolean isRestartOnReboot() {
        return restartOnReboot;
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

    @JsonIgnore
    public Optional<TaskArgs> getArgs() {
        return Optional.ofNullable(args.toTaskArgs());
    }

    @JsonIgnore
    public SchedulingParams getSchedulingParams() {
        return new SchedulingParamsImpl.Builder(id, name, engineRequirements.toEngineRequirements(), type, param, 0, false, false).build();
    }
}
