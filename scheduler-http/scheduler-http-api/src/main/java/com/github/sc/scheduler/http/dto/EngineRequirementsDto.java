package com.github.sc.scheduler.http.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.sc.scheduler.core.model.EngineRequirements;
import com.github.sc.scheduler.core.model.EngineRequirementsImpl;

import javax.annotation.concurrent.Immutable;

@Immutable
public class EngineRequirementsDto {

    private final int weight;
    private final String executor;
    private final String service;

    @JsonCreator
    public EngineRequirementsDto(@JsonProperty("weight") int weight,
                                 @JsonProperty("executor") String executor,
                                 @JsonProperty("service") String service) {
        this.weight = weight;
        this.executor = executor;
        this.service = service;
    }

    public EngineRequirementsDto(EngineRequirements engineRequirements) {
        this(
                engineRequirements.getWeight(),
                engineRequirements.getExecutor(),
                engineRequirements.getService()
        );
    }

    @JsonGetter
    public int getWeight() {
        return weight;
    }

    @JsonGetter
    public String getExecutor() {
        return executor;
    }

    @JsonGetter
    public String getService() {
        return service;
    }

    public EngineRequirements toEngineRequirements() {
        return new EngineRequirementsImpl(weight, executor, service);
    }
}
