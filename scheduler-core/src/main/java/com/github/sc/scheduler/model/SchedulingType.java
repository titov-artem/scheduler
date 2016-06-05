package com.github.sc.scheduler.model;

import com.github.sc.cron.CronExpression;
import com.github.sc.cron.CronExpressionImpl;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Optional;

public enum SchedulingType {
    /**
     * Run task based on cron expression
     */
    CRON {
        @Override
        public boolean canStart(String paramValue, Instant curTime, Instant lastRunTime, long tickPeriodSeconds, ZoneId zone) {
            CronExpression expression = CronExpressionImpl.of(paramValue);
            if (expression.match(ZonedDateTime.ofInstant(curTime, zone))) return true;

            Instant prevTime = curTime.minusSeconds(tickPeriodSeconds);
            Optional<ZonedDateTime> maybeNext = expression.nextFireAfter(
                    ZonedDateTime.ofInstant(prevTime, zone)
            );
            if (!maybeNext.isPresent()) return false;

            Instant next = maybeNext.get().toInstant();

            return prevTime.isBefore(next) && curTime.isAfter(next);
        }
    },
    /**
     * Run task once and remove it from timetable
     */
    ONCE {
        @Override
        public boolean canStart(String paramValue, Instant curTime, Instant lastRunTime, long tickPeriodSeconds, ZoneId zone) {
            return true;
        }
    },
    // todo maybe 1s will be hard to support...
    /**
     * Run task every k seconds
     */
    PERIOD {
        @Override
        public boolean canStart(String paramValue, Instant curTime, Instant lastRunTime, long tickPeriodSeconds, ZoneId zone) {
            if (lastRunTime == null) {
                return true;
            }
            long curSeconds = curTime.getLong(ChronoField.INSTANT_SECONDS);
            long lastRunSeconds = lastRunTime.getLong(ChronoField.INSTANT_SECONDS);
            return curSeconds - lastRunSeconds >= Long.parseLong(paramValue);
        }
    };

    public abstract boolean canStart(@Nullable String paramValue,
                                     Instant curTime,
                                     @Nullable Instant lastRunTime,
                                     long tickPeriodSeconds,
                                     ZoneId zone);
}
