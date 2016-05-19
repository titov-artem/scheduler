package ru.yandex.qe.common.scheduler.model;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.temporal.ChronoField;

public enum SchedulingType {
    /**
     * Run task based on cron expression
     */
    CRON {
        @Override
        public boolean canStart(String paramValue, Instant curTime, Instant lastRunTime) {
            return lastRunTime == null;
        }
    },
    /**
     * Run task once and remove it from timetable
     */
    ONCE {
        @Override
        public boolean canStart(String paramValue, Instant curTime, Instant lastRunTime) {
            return true;
        }
    },
    // todo maybe 1s will be hard to support...
    /**
     * Run task every k seconds
     */
    PERIOD {
        @Override
        public boolean canStart(String paramValue, Instant curTime, Instant lastRunTime) {
            if (lastRunTime == null) {
                return true;
            }
            return curTime.getLong(ChronoField.INSTANT_SECONDS) - lastRunTime.getLong(ChronoField.INSTANT_SECONDS) >= Long.parseLong(paramValue);
        }
    };

    public abstract boolean canStart(@Nullable String paramValue,
                                     Instant curTime,
                                     @Nullable Instant lastRunTime);
}
