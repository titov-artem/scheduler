package com.github.sc.scheduler.core.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.time.Instant;
import java.util.Objects;

@Immutable
public class RunImpl implements Run {

    private final long runId;
    private final String taskId;
    private final EngineRequirements engineRequirements;
    private final Instant queuedTime;
    private final String host;
    private final Instant acquiredTime;
    private final Instant startTime;
    private final Instant pingTime;
    private final Instant endTime;
    private final Status status;
    private final boolean restartOnFail;
    private final boolean restartOnReboot;
    private final String message;
    private final int version;

    private RunImpl(long runId,
                    String taskId,
                    EngineRequirements engineRequirements,
                    Instant queuedTime,
                    String host,
                    Instant acquiredTime,
                    Instant startTime,
                    Instant pingTime,
                    Instant endTime,
                    Status status,
                    boolean restartOnFail,
                    boolean restartOnReboot,
                    String message,
                    int version) {
        this.runId = runId;
        this.taskId = taskId;
        this.engineRequirements = engineRequirements;
        this.host = host;
        this.queuedTime = queuedTime;
        this.acquiredTime = acquiredTime;
        this.startTime = startTime;
        this.pingTime = pingTime;
        this.endTime = endTime;
        this.status = status;
        this.restartOnReboot = restartOnReboot;
        this.restartOnFail = restartOnFail;
        this.message = message;
        this.version = version;
    }

    public static Builder builder(Run run) {
        return new Builder(run);
    }

    public static Builder newRun(SchedulingParams task) {
        return new Builder(task);
    }

    public static Builder builder(long runId,
                                  String taskId,
                                  EngineRequirements engineRequirements,
                                  boolean restartOnFail,
                                  boolean restartOnReboot,
                                  Status status,
                                  Instant queuedTime) {
        return new Builder(runId, taskId, engineRequirements, restartOnFail, restartOnReboot, status, queuedTime);
    }

    @Override
    public long getRunId() {
        return runId;
    }

    @Nonnull
    @Override
    public String getTaskId() {
        return taskId;
    }

    @Nonnull
    @Override
    public EngineRequirements getEngineRequirements() {
        return engineRequirements;
    }

    @Nonnull
    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean isRestartOnFail() {
        return restartOnFail;
    }

    @Override
    public boolean isRestartOnReboot() {
        return restartOnReboot;
    }

    @Nonnull
    @Override
    public Instant getQueuedTime() {
        return queuedTime;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public Instant getAcquiredTime() {
        return acquiredTime;
    }

    @Override
    public Instant getStartTime() {
        return startTime;
    }

    @Nullable
    @Override
    public Instant getPingTime() {
        return pingTime;
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunImpl run = (RunImpl) o;
        return Objects.equals(runId, run.runId) &&
                Objects.equals(version, run.version) &&
                Objects.equals(taskId, run.taskId) &&
                Objects.equals(engineRequirements, run.engineRequirements) &&
                Objects.equals(host, run.host) &&
                Objects.equals(queuedTime, run.queuedTime) &&
                Objects.equals(acquiredTime, run.acquiredTime) &&
                Objects.equals(startTime, run.startTime) &&
                Objects.equals(endTime, run.endTime) &&
                Objects.equals(status, run.status) &&
                Objects.equals(restartOnFail, run.restartOnFail) &&
                Objects.equals(restartOnReboot, run.restartOnReboot) &&
                Objects.equals(message, run.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, taskId, engineRequirements, host, queuedTime, acquiredTime, startTime, endTime,
                status, restartOnFail, restartOnReboot, message, version);
    }

    @Override
    public String toString() {
        return "RunImpl{" +
                "runId=" + runId +
                ", taskId='" + taskId + '\'' +
                ", engineRequirements=" + engineRequirements +
                ", host='" + host + '\'' +
                ", queuedTime=" + queuedTime +
                ", acquiredTime=" + acquiredTime +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", restartOnFail=" + restartOnFail +
                ", restartOnReboot=" + restartOnReboot +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", version=" + version +
                '}';
    }

    public static final class Builder {
        private Run run;

        private Builder(SchedulingParams task) {
            run = new RunImpl(FAKE_RUN_ID, task.getTaskId(), task.getEngineRequirements(), null, null, null, null, null, null,
                    Status.PENDING, task.isRestartOnReboot(), task.isRestartOnFail(), null, 0);
        }

        private Builder(Run run) {
            this.run = run;
        }

        private Builder(long runId,
                        String taskId,
                        EngineRequirements engineRequirements,
                        boolean restartOnFail,
                        boolean restartOnReboot,
                        Status status,
                        Instant queuedTime) {
            run = new RunImpl(runId, taskId, engineRequirements, queuedTime, null, null, null, null, null, status, restartOnFail, restartOnReboot, null, 0);
        }

        public Builder withRunId(long runId) {
            run = new RunImpl(runId, run.getTaskId(), run.getEngineRequirements(), run.getQueuedTime(),
                    run.getHost(), run.getAcquiredTime(), run.getStartTime(), run.getPingTime(), run.getEndTime(), run.getStatus(),
                    run.isRestartOnFail(), run.isRestartOnReboot(), run.getMessage(),
                    run.getVersion());
            return this;
        }

        public Builder withQueuedTime(Instant time) {
            run = new RunImpl(run.getRunId(), run.getTaskId(), run.getEngineRequirements(), time,
                    run.getHost(), run.getAcquiredTime(), run.getStartTime(), run.getPingTime(), run.getEndTime(), run.getStatus(),
                    run.isRestartOnFail(), run.isRestartOnReboot(), run.getMessage(),
                    run.getVersion());
            return this;

        }


        public Builder withHost(String host) {
            run = new RunImpl(run.getRunId(), run.getTaskId(), run.getEngineRequirements(), run.getQueuedTime(),
                    host, run.getAcquiredTime(), run.getStartTime(), run.getPingTime(), run.getEndTime(), run.getStatus(),
                    run.isRestartOnFail(), run.isRestartOnReboot(), run.getMessage(),
                    run.getVersion());
            return this;
        }

        public Builder withAcquiredTime(Instant time) {
            run = new RunImpl(run.getRunId(), run.getTaskId(), run.getEngineRequirements(), run.getQueuedTime(),
                    run.getHost(), time, run.getStartTime(), run.getPingTime(), run.getEndTime(), run.getStatus(),
                    run.isRestartOnFail(), run.isRestartOnReboot(), run.getMessage(),
                    run.getVersion());
            return this;
        }

        public Builder withStartTime(Instant time) {
            run = new RunImpl(run.getRunId(), run.getTaskId(), run.getEngineRequirements(), run.getQueuedTime(),
                    run.getHost(), run.getAcquiredTime(), time, run.getPingTime(), run.getEndTime(), run.getStatus(),
                    run.isRestartOnFail(), run.isRestartOnReboot(), run.getMessage(),
                    run.getVersion());
            return this;
        }

        public Builder withPingTime(Instant time) {
            run = new RunImpl(run.getRunId(), run.getTaskId(), run.getEngineRequirements(), run.getQueuedTime(),
                    run.getHost(), run.getAcquiredTime(), run.getStartTime(), time, run.getEndTime(), run.getStatus(),
                    run.isRestartOnFail(), run.isRestartOnReboot(), run.getMessage(),
                    run.getVersion());
            return this;

        }

        public Builder withEndTime(Instant time) {
            run = new RunImpl(run.getRunId(), run.getTaskId(), run.getEngineRequirements(), run.getQueuedTime(),
                    run.getHost(), run.getAcquiredTime(), run.getStartTime(), run.getPingTime(), time, run.getStatus(),
                    run.isRestartOnFail(), run.isRestartOnReboot(), run.getMessage(),
                    run.getVersion());
            return this;
        }

        public Builder withStatus(Status status) {
            run = new RunImpl(run.getRunId(), run.getTaskId(), run.getEngineRequirements(), run.getQueuedTime(),
                    run.getHost(), run.getAcquiredTime(), run.getStartTime(), run.getPingTime(), run.getEndTime(), status,
                    run.isRestartOnFail(), run.isRestartOnReboot(), run.getMessage(),
                    run.getVersion());
            return this;
        }

        public Builder withMessage(String message) {
            run = new RunImpl(run.getRunId(), run.getTaskId(), run.getEngineRequirements(), run.getQueuedTime(),
                    run.getHost(), run.getAcquiredTime(), run.getStartTime(), run.getPingTime(), run.getEndTime(), run.getStatus(),
                    run.isRestartOnFail(), run.isRestartOnReboot(), message,
                    run.getVersion());
            return this;
        }

        public Builder withVersion(int version) {
            run = new RunImpl(run.getRunId(), run.getTaskId(), run.getEngineRequirements(), run.getQueuedTime(),
                    run.getHost(), run.getAcquiredTime(), run.getStartTime(), run.getPingTime(), run.getEndTime(), run.getStatus(),
                    run.isRestartOnFail(), run.isRestartOnReboot(), run.getMessage(),
                    version);
            return this;
        }

        public Run build() {
            return run;
        }

    }
}
