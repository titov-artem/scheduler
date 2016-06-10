package com.github.sc.scheduler.core.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
@Immutable
public class SchedulingParamsImpl implements SchedulingParams {

    private final String taskId;
    private final Optional<String> name;
    private final EngineRequirements engineRequirements;
    private final SchedulingType type;
    private final String param;
    private final int concurrencyLevel;
    private final boolean restartOnFail;
    private final boolean restartOnReboot;

    private SchedulingParamsImpl(String taskId,
                                 Optional<String> name,
                                 EngineRequirements engineRequirements,
                                 SchedulingType type,
                                 String param,
                                 int concurrencyLevel,
                                 boolean restartOnFail,
                                 boolean restartOnReboot) {
        this.taskId = taskId;
        this.name = name;
        this.engineRequirements = engineRequirements;
        this.type = type;
        this.param = param;
        this.concurrencyLevel = concurrencyLevel;
        this.restartOnFail = restartOnFail;
        this.restartOnReboot = restartOnReboot;
    }

    public static Builder newTask(EngineRequirements engineRequirements, SchedulingType type, String param) {
        return new Builder(engineRequirements, type, param);
    }


    public static Builder newTask(@Nullable String name, EngineRequirements engineRequirements, SchedulingType type, String param) {
        return new Builder(name, engineRequirements, type, param);
    }

    public static Builder newTask(@Nullable String name,
                                  EngineRequirements engineRequirements,
                                  SchedulingType type,
                                  String param,
                                  int concurrencyLevel,
                                  boolean restartOnFail,
                                  boolean restartOnReboot) {
        return new Builder(name, engineRequirements, type, param, concurrencyLevel, restartOnFail, restartOnReboot);
    }

    public static Builder builder(SchedulingParams params) {
        return new Builder(params);
    }

    @Override
    public String getTaskId() {
        return taskId;
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
    public int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    @Override
    public boolean isRestartOnFail() {
        return restartOnFail;
    }

    @Override
    public boolean isRestartOnReboot() {
        return restartOnReboot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchedulingParamsImpl that = (SchedulingParamsImpl) o;
        return concurrencyLevel == that.concurrencyLevel &&
                restartOnFail == that.restartOnFail &&
                restartOnReboot == that.restartOnReboot &&
                Objects.equals(taskId, that.taskId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(engineRequirements, that.engineRequirements) &&
                type == that.type &&
                Objects.equals(param, that.param);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, name, engineRequirements, type, param, concurrencyLevel, restartOnFail, restartOnReboot);
    }

    @Override
    public String toString() {
        return "SchedulingParamsImpl{" +
                "taskId='" + taskId + '\'' +
                ", name=" + name +
                ", engineRequirements=" + engineRequirements +
                ", type=" + type +
                ", param='" + param + '\'' +
                ", concurrencyLevel=" + concurrencyLevel +
                ", restartOnFail=" + restartOnFail +
                ", restartOnReboot=" + restartOnReboot +
                '}';
    }

    public static final class Builder {
        private SchedulingParams instance;

        private Builder(EngineRequirements engineRequirements, SchedulingType type, String param) {
            this(null, engineRequirements, type, param, 1, false, false);
        }


        private Builder(@Nullable String name, EngineRequirements engineRequirements, SchedulingType type, String param) {
            this(name, engineRequirements, type, param, 1, false, false);
        }

        private Builder(@Nullable String name,
                        EngineRequirements engineRequirements,
                        SchedulingType type,
                        String param,
                        int concurrencyLevel,
                        boolean restartOnFail,
                        boolean restartOnReboot) {
            this(null, name, engineRequirements, type, param, concurrencyLevel, restartOnFail, restartOnReboot);
        }

        public Builder(String taskId,
                       @Nullable String name,
                       EngineRequirements engineRequirements,
                       SchedulingType type,
                       String param,
                       int concurrencyLevel,
                       boolean restartOnFail,
                       boolean restartOnReboot) {
            instance = new SchedulingParamsImpl(taskId, Optional.ofNullable(name), engineRequirements, type, param, concurrencyLevel, restartOnFail, restartOnReboot);
        }

        private Builder(SchedulingParams instance) {
            this.instance = instance;
        }

        public Builder withTaskId(String taskId) {
            instance = new SchedulingParamsImpl(taskId, instance.getName(), instance.getEngineRequirements(),
                    instance.getType(), instance.getParam(),
                    instance.getConcurrencyLevel(), instance.isRestartOnFail(), instance.isRestartOnReboot());
            return this;
        }

        public Builder withName(@Nullable String name) {
            instance = new SchedulingParamsImpl(instance.getTaskId(), Optional.ofNullable(name), instance.getEngineRequirements(),
                    instance.getType(), instance.getParam(),
                    instance.getConcurrencyLevel(), instance.isRestartOnFail(), instance.isRestartOnReboot());
            return this;
        }

        public Builder withEngineRequirements(EngineRequirements engineRequirements) {
            instance = new SchedulingParamsImpl(instance.getTaskId(), instance.getName(), engineRequirements,
                    instance.getType(), instance.getParam(),
                    instance.getConcurrencyLevel(), instance.isRestartOnFail(), instance.isRestartOnReboot());
            return this;
        }

        public Builder withSchedulingType(SchedulingType type, String param) {
            instance = new SchedulingParamsImpl(instance.getTaskId(), instance.getName(), instance.getEngineRequirements(),
                    type, param,
                    instance.getConcurrencyLevel(), instance.isRestartOnFail(), instance.isRestartOnReboot());
            return this;
        }


        public Builder withConcurrencyLevel(int concurrencyLevel) {
            instance = new SchedulingParamsImpl(instance.getTaskId(), instance.getName(), instance.getEngineRequirements(),
                    instance.getType(), instance.getParam(),
                    concurrencyLevel, instance.isRestartOnFail(), instance.isRestartOnReboot());
            return this;
        }

        public Builder withRestartOnFail(boolean restartOnFail) {
            instance = new SchedulingParamsImpl(instance.getTaskId(), instance.getName(), instance.getEngineRequirements(),
                    instance.getType(), instance.getParam(),
                    instance.getConcurrencyLevel(), restartOnFail, instance.isRestartOnReboot());
            return this;
        }

        public Builder withRestartOnReboot(boolean restartOnReboot) {
            instance = new SchedulingParamsImpl(instance.getTaskId(), instance.getName(), instance.getEngineRequirements(),
                    instance.getType(), instance.getParam(),
                    instance.getConcurrencyLevel(), instance.isRestartOnFail(), restartOnReboot);
            return this;
        }

        public SchedulingParams build() {
            return instance;
        }
    }
}
