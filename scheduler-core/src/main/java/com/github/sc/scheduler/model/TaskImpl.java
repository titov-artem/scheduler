package com.github.sc.scheduler.model;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Optional;

@Immutable
public class TaskImpl implements Task {

    private final String id;
    private final Optional<String> name;
    private final EngineRequirements engineRequirements;

    public TaskImpl(String id, Optional<String> name, EngineRequirements engineRequirements) {
        this.id = id;
        this.name = name;
        this.engineRequirements = engineRequirements;
    }

    public static Task newTask(String name, EngineRequirements engineRequirements) {
        return new TaskImpl(null, Optional.ofNullable(name), engineRequirements);
    }

    public static Task newTask(EngineRequirements engineRequirements) {
        return new TaskImpl(null, Optional.empty(), engineRequirements);
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }

    @Nonnull
    @Override
    public Optional<String> getName() {
        return name;
    }

    @Nonnull
    @Override
    public EngineRequirements getEngineRequirements() {
        return engineRequirements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskImpl task = (TaskImpl) o;
        return Objects.equals(id, task.id) &&
                Objects.equals(name, task.name) &&
                Objects.equals(engineRequirements, task.engineRequirements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, engineRequirements);
    }

    @Override
    public String toString() {
        return "TaskImpl{" +
                "id='" + id + '\'' +
                ", name=" + name +
                ", engineRequirements=" + engineRequirements +
                '}';
    }
}
