package com.github.sc.scheduler.core.model;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.Instant;
import java.util.Objects;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
@Immutable
public class TaskImpl implements Task {

    private final String id;
    private final Instant lastRunTime;
    private final String startingHost;
    private final Instant startingTime;
    private final int version;

    private TaskImpl(String id, Instant lastRunTime, String startingHost, Instant startingTime, int version) {
        this.id = id;
        this.lastRunTime = lastRunTime;
        this.startingHost = startingHost;
        this.startingTime = startingTime;
        this.version = version;
    }

    public static Task newTask(String id) {
        return new TaskImpl(id, null, null, null, 1);
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static Builder builder(Task task) {
        return new Builder(task);
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }

    @Override
    public Instant getLastRunTime() {
        return lastRunTime;
    }

    @Override
    public String getStartingHost() {
        return startingHost;
    }

    @Override
    public Instant getStartingTime() {
        return startingTime;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskImpl task = (TaskImpl) o;
        return version == task.version &&
                Objects.equals(id, task.id) &&
                Objects.equals(lastRunTime, task.lastRunTime) &&
                Objects.equals(startingHost, task.startingHost) &&
                Objects.equals(startingTime, task.startingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastRunTime, startingHost, startingTime, version);
    }

    @Override
    public String toString() {
        return "TaskImpl{" +
                "id='" + id + '\'' +
                ", lastRunTime=" + lastRunTime +
                ", startingHost='" + startingHost + '\'' +
                ", startingTime=" + startingTime +
                ", version=" + version +
                '}';
    }

    public static final class Builder {
        private Task instance;

        private Builder(String taskId) {
            instance = TaskImpl.newTask(taskId);
        }

        private Builder(Task instance) {
            this.instance = instance;
        }

        public Builder withLastRunTime(Instant time) {
            instance = new TaskImpl(instance.getId(),
                    time, instance.getStartingHost(), instance.getStartingTime(),
                    instance.getVersion());
            return this;
        }

        public Builder withStartingHost(String host) {
            instance = new TaskImpl(instance.getId(),
                    instance.getLastRunTime(), host, instance.getStartingTime(),
                    instance.getVersion());
            return this;
        }

        public Builder withStartingTime(Instant time) {
            instance = new TaskImpl(instance.getId(),
                    instance.getLastRunTime(), instance.getStartingHost(), time,
                    instance.getVersion());
            return this;
        }

        public Builder withVersion(int version) {
            instance = new TaskImpl(instance.getId(),
                    instance.getLastRunTime(), instance.getStartingHost(), instance.getStartingTime(),
                    version);
            return this;
        }

        public Task build() {
            return instance;
        }
    }
}
