package com.github.sc.scheduler.http.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.sc.scheduler.model.SchedulingParams;
import com.github.sc.scheduler.model.SchedulingParamsImpl;
import com.github.sc.scheduler.model.SchedulingType;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class SchedulingParamsDto {

    private final String taskId;
    private final SchedulingType type;
    @Nullable
    private final String param;

    @JsonCreator
    public SchedulingParamsDto(@JsonProperty("taskId") String taskId,
                               @JsonProperty("type") SchedulingType type,
                               @JsonProperty("param") @Nullable String param) {
        this.taskId = taskId;
        this.type = type;
        this.param = param;
    }

    public SchedulingParamsDto(SchedulingParams schedulingParams) {
        this(
                schedulingParams.getTaskId(),
                schedulingParams.getType(),
                schedulingParams.getParam()
        );
    }

    @JsonGetter
    public String getTaskId() {
        return taskId;
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

    public SchedulingParams toSchedulingParams() {
        return SchedulingParamsImpl.builder(taskId, type, param).build();
    }
}
