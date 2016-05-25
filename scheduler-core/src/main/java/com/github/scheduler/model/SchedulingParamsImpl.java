package com.github.scheduler.model;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.Instant;
import java.util.Objects;

@Immutable
public class SchedulingParamsImpl implements SchedulingParams {

    private final String taskId;
    private final SchedulingType type;
    private final String param;
    private final Instant lastRunTime;
    private final String startingHost;
    private final Instant startingTime;
    private final int version;

    private SchedulingParamsImpl(String taskId,
                                 SchedulingType type,
                                 String param,
                                 Instant lastRunTime,
                                 String startingHost,
                                 Instant startingTime,
                                 int version) {
        this.taskId = taskId;
        this.type = type;
        this.param = param;
        this.lastRunTime = lastRunTime;
        this.startingHost = startingHost;
        this.startingTime = startingTime;
        this.version = version;
    }

    public static Builder builder(SchedulingParams instance) {
        return new Builder(instance);
    }

    public static Builder builder(String taskId, SchedulingType type, String param) {
        return new Builder(taskId, type, param);
    }

    @Override
    public String getTaskId() {
        return taskId;
    }

    @Nonnull
    @Override
    public SchedulingType getType() {
        return type;
    }

    @Override
    public String getParam() {
        return param;
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
        SchedulingParamsImpl that = (SchedulingParamsImpl) o;
        return Objects.equals(version, that.version) &&
                Objects.equals(taskId, that.taskId) &&
                Objects.equals(type, that.type) &&
                Objects.equals(param, that.param) &&
                Objects.equals(lastRunTime, that.lastRunTime) &&
                Objects.equals(startingHost, that.startingHost) &&
                Objects.equals(startingTime, that.startingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, type, param, lastRunTime, startingHost, startingTime, version);
    }

    @Override
    public String toString() {
        return "SchedulingParamsImpl{" +
                "taskId='" + taskId + '\'' +
                ", type=" + type +
                ", param='" + param + '\'' +
                ", lastRunTime=" + lastRunTime +
                ", startingHost='" + startingHost + '\'' +
                ", startingTime=" + startingTime +
                ", version=" + version +
                '}';
    }

    public static final class Builder {
        private SchedulingParams instance;

        private Builder(String taskId, SchedulingType type, String param) {
            instance = new SchedulingParamsImpl(taskId, type, param, null, null, null, 1);
        }

        private Builder(SchedulingParams instance) {
            this.instance = instance;
        }

        public Builder withLastRunTime(Instant time) {
            instance = new SchedulingParamsImpl(instance.getTaskId(), instance.getType(), instance.getParam(),
                    time, instance.getStartingHost(), instance.getStartingTime(),
                    instance.getVersion());
            return this;
        }

        public Builder withStartingHost(String host) {
            instance = new SchedulingParamsImpl(instance.getTaskId(), instance.getType(), instance.getParam(),
                    instance.getLastRunTime(), host, instance.getStartingTime(),
                    instance.getVersion());
            return this;
        }

        public Builder withStartingTime(Instant time) {
            instance = new SchedulingParamsImpl(instance.getTaskId(), instance.getType(), instance.getParam(),
                    instance.getLastRunTime(), instance.getStartingHost(), time,
                    instance.getVersion());
            return this;
        }

        public Builder withVersion(int version) {
            instance = new SchedulingParamsImpl(instance.getTaskId(), instance.getType(), instance.getParam(),
                    instance.getLastRunTime(), instance.getStartingHost(), instance.getStartingTime(),
                    version);
            return this;
        }

        public SchedulingParams build() {
            return instance;
        }
    }
}
