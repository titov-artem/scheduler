package com.github.sc.scheduler.core.model;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
public class EngineRequirementsImpl implements EngineRequirements {

    private final int weight;
    private final String executor;
    private final String service;

    public EngineRequirementsImpl(int weight, String executor, String service) {
        this.weight = weight;
        this.executor = executor;
        this.service = service;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Nonnull
    @Override
    public String getExecutor() {
        return executor;
    }

    @Nonnull
    @Override
    public String getService() {
        return service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EngineRequirementsImpl that = (EngineRequirementsImpl) o;
        return Objects.equals(weight, that.weight) &&
                Objects.equals(executor, that.executor) &&
                Objects.equals(service, that.service);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weight, executor, service);
    }

    @Override
    public String toString() {
        return "EngineRequirementsImpl{" +
                ", weight=" + weight +
                ", executor='" + executor + '\'' +
                ", service='" + service + '\'' +
                '}';
    }
}
