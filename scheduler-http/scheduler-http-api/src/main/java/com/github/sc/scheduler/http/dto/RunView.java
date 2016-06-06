package com.github.sc.scheduler.http.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.sc.scheduler.core.model.Run;

import javax.annotation.concurrent.Immutable;
import java.time.Instant;

@Immutable
public class RunView {
    private final long id;
    private final String taskId;
    private final Run.Status status;
    private final String host;
    private final Instant queuedTime;
    private final Instant acquiredTime;
    private final Instant startTime;
    private final Instant endTime;
    private final Instant pingTime;
    private final String message;
    private final EngineRequirementsDto engineRequirements;

    public RunView(Run run) {
        this.id = run.getRunId();
        this.taskId = run.getTaskId();
        this.status = run.getStatus();
        this.host = run.getHost();
        this.queuedTime = run.getQueuedTime();
        this.acquiredTime = run.getAcquiredTime();
        this.startTime = run.getStartTime();
        this.endTime = run.getEndTime();
        this.pingTime = run.getPingTime();
        this.message = run.getMessage();
        this.engineRequirements = new EngineRequirementsDto(run.getEngineRequirements());
    }

    @JsonGetter
    public long getId() {
        return id;
    }

    @JsonGetter
    public String getTaskId() {
        return taskId;
    }

    @JsonGetter
    public Run.Status getStatus() {
        return status;
    }

    @JsonGetter
    public String getHost() {
        return host;
    }

    @JsonGetter
    public Instant getQueuedTime() {
        return queuedTime;
    }

    @JsonGetter
    public Instant getAcquiredTime() {
        return acquiredTime;
    }

    @JsonGetter
    public Instant getStartTime() {
        return startTime;
    }

    @JsonGetter
    public Instant getEndTime() {
        return endTime;
    }

    @JsonGetter
    public Instant getPingTime() {
        return pingTime;
    }

    @JsonGetter
    public String getMessage() {
        return message;
    }

    @JsonGetter
    public EngineRequirementsDto getEngineRequirements() {
        return engineRequirements;
    }
}
