package com.github.sc.cron;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.sc.cron.CronFieldType.*;

public class CronExpressionImpl implements CronExpression, CronPart {

    private static final List<CronFieldType> ORDERED_FIELDS = ImmutableList.of(
            SECONDS, MINUTES, HOURS, DAY_OF_MONTH, MONTHS, DAY_OF_WEEK, YEARS
    );

    private Map<CronFieldType, CronField> cronFields;

    private CronExpressionImpl(Map<CronFieldType, CronField> cronFields) {
        this.cronFields = cronFields;
    }

    @Override
    public Optional<ZonedDateTime> nextFireAfter(ZonedDateTime afterTime) {
        // if no fire time found in next 4 years plus 1 day, we can consider cron
        // expression invalid. There are few cases that not covered by this logic:
        // each 100th year but not 400th year is not leap year, so run on 29th February
        // after such years can be missed
        return nextFireAfter(afterTime, afterTime.plusYears(4).plusDays(1));
    }

    private Optional<ZonedDateTime> nextFireAfter(ZonedDateTime afterTime, ZonedDateTime barrier) {
        NextFireTime nextFireTime = nextFireTimeAfter(afterTime);
        if (nextFireTime.thisDayFireTime == null && nextFireTime.nextDayFireTime == null)
            return Optional.empty();
        if (nextFireTime.thisDayFireTime != null) {
            ZonedDateTime time = withTime(afterTime, nextFireTime.thisDayFireTime);
            if (match(time)) {
                return Optional.of(time);
            }
        }
        ZonedDateTime curTime = withTime(afterTime.plusDays(1), nextFireTime.nextDayFireTime);
        while (barrier.isAfter(curTime)) {
            if (match(curTime)) {
                return Optional.of(curTime);
            }
            curTime = curTime.plusDays(1);
        }
        return Optional.empty();
    }

    private ZonedDateTime withTime(ZonedDateTime dateTime, ZonedDateTime time) {
        return dateTime.withHour(time.getHour())
                .withMinute(time.getMinute())
                .withSecond(time.getSecond());
    }

    /**
     * @param afterTime time strongly after which we have to find fire time
     * @return only time (hh:mm:ss) of next fire
     */
    private NextFireTime nextFireTimeAfter(ZonedDateTime afterTime) {
        ZonedDateTime prototype = ZonedDateTime.of(0, 0, 0, 0, 0, 0, 0, afterTime.getZone());
        // determine seconds
        int thisDaySeconds = -1;
        int nextDaySeconds = -1;
        for (int i = 0; i < 60; i++) {
            ZonedDateTime time = prototype.plusSeconds(i);
            if (cronFields.get(SECONDS).match(time)) {
                if (nextDaySeconds == -1) nextDaySeconds = i;
                //noinspection ConstantConditions
                if (i > afterTime.getSecond() && thisDaySeconds == -1) thisDaySeconds = i;
            }
            if (nextDaySeconds != -1 && thisDaySeconds != -1) break;
        }

        // determine minutes
        int thisDayMinutes = -1;
        int nextDayMinutes = -1;
        for (int i = 0; i < 60; i++) {
            ZonedDateTime time = prototype.plusMinutes(i);
            if (cronFields.get(MINUTES).match(time)) {
                if (nextDayMinutes == -1) nextDayMinutes = i;
                //noinspection ConstantConditions
                if (i > afterTime.getMinute() && thisDayMinutes == -1) thisDayMinutes = i;
            }
            if (nextDayMinutes != -1 && thisDayMinutes != -1) break;
        }

        // determine hours
        int thisDayHours = -1;
        int nextDayHours = -1;
        for (int i = 0; i < 23; i++) {
            ZonedDateTime time = prototype.plusHours(i);
            if (cronFields.get(HOURS).match(time)) {
                if (nextDayHours == -1) nextDayHours = i;
                //noinspection ConstantConditions
                if (i > afterTime.getHour() && thisDayHours == -1) thisDayHours = i;
            }
            if (nextDayHours != -1 && thisDayHours != -1) break;
        }

        ZonedDateTime thisDayFireTime =
                thisDayHours != -1 && thisDayMinutes != -1 && thisDaySeconds != -1
                        ? prototype.plusHours(thisDayHours).plusMinutes(thisDayMinutes).plusSeconds(thisDaySeconds)
                        : null;
        ZonedDateTime nextDayFireTime =
                nextDayHours != -1 && nextDayMinutes != -1 && nextDaySeconds != -1
                        ? prototype.plusHours(nextDayHours).plusMinutes(nextDayMinutes).plusSeconds(nextDaySeconds)
                        : null;

        if (thisDayFireTime != null) {
            Preconditions.checkState(afterTime.isBefore(withTime(afterTime, thisDayFireTime)),
                    "Internal error: invalid this day fire time!");
        }

        return new NextFireTime(thisDayFireTime, nextDayFireTime);
    }

    @Override
    public boolean match(ZonedDateTime dateTime) {
        boolean matches = true;
        for (CronFieldType type : ORDERED_FIELDS) {
            if (cronFields.containsKey(type)) {
                CronField field = cronFields.get(type);
                matches = matches && field.match(dateTime);
            }
        }
        return matches;
    }

    /**
     * Create cron expression with default behavior (only day of month or day of week
     * can be specified, not both of them)
     *
     * @param expression expression string
     * @return parsed expression
     */
    public static CronExpression of(String expression) {
        return of(expression, false);
    }

    /**
     * Create cron expression with specified behavior
     *
     * @param expression             expression string
     * @param dayOfMonthAndDayOfWeek if {@code true} - both day of month and day of week can be
     *                               specified. They will be joined by and with each other
     *                               and other parts of expression. If {@code false} - only one
     *                               of day of month and day of week can be specified, not both.
     *                               The second one MUST be set to "?". Specified one will be
     *                               joined by "and" with other expression parts
     * @return parsed expression
     */
    public static CronExpression of(String expression, boolean dayOfMonthAndDayOfWeek) {
        String[] rawFields = expression.toUpperCase().split(" ");
        Preconditions.checkArgument(rawFields.length <= ORDERED_FIELDS.size(),
                "Invalid expression. Expected format: <SECONDS> <MINUTES> <HOURS> <DAY OF MONTH> <MONTHS> <DAY OF WEEK>[ <YEARS>]");
        Map<CronFieldType, CronField> fields = new HashMap<>();
        for (int i = 0; i < ORDERED_FIELDS.size(); i++) {
            CronFieldType fieldType = ORDERED_FIELDS.get(i);
            if (i >= rawFields.length) {
                Preconditions.checkArgument(!fieldType.isMandatory(),
                        "Missed mandatory field in expression: %s",
                        fieldType.name());
                break;
            }
            CronField field = fieldType.parse(rawFields[i]);
            fields.put(fieldType, field);
        }

        if (!dayOfMonthAndDayOfWeek) {
            String rawDayOfMonth = rawFields[ORDERED_FIELDS.indexOf(DAY_OF_MONTH)];
            String rawDayOfWeek = rawFields[ORDERED_FIELDS.indexOf(DAY_OF_WEEK)];

            if ("?".equals(rawDayOfMonth)) {
                fields.remove(DAY_OF_MONTH);
            }
            if ("?".equals(rawDayOfWeek)) {
                fields.remove(DAY_OF_WEEK);
            }
            Preconditions.checkArgument(fields.containsKey(DAY_OF_MONTH) || fields.containsKey(DAY_OF_WEEK),
                    "One of the day of month or day of week must be specified to not '?'");
            Preconditions.checkArgument(!fields.containsKey(DAY_OF_MONTH) || !fields.containsKey(DAY_OF_WEEK),
                    "Only one of the day of month or day of week can be specified, other must be '?'");
        }


        return new CronExpressionImpl(fields);
    }

    private static final class NextFireTime {
        @Nullable
        final ZonedDateTime thisDayFireTime;
        @Nullable
        final ZonedDateTime nextDayFireTime;

        private NextFireTime(@Nullable ZonedDateTime thisDayFireTime,
                             @Nullable ZonedDateTime nextDayFireTime) {
            this.thisDayFireTime = thisDayFireTime;
            this.nextDayFireTime = nextDayFireTime;
        }
    }
}
