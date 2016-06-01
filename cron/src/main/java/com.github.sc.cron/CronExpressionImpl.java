package com.github.sc.cron;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.sc.cron.CronFieldType.*;

public class CronExpressionImpl implements CronExpression, CronPart {

    private static final List<CronFieldType> ORDERED_FIELDS = ImmutableList.of(
            SECONDS, MINUTES, HOURS, DAY_OF_MONTH, MONTHS, DAY_OF_WEEK, YEARS
    );

    private Map<CronFieldType, CronField> cronFields;

    CronExpressionImpl(Map<CronFieldType, CronField> cronFields) {
        this.cronFields = cronFields;
    }

    @Override
    public ZonedDateTime nextTimeAfter(ZonedDateTime afterTime) {
        return null;
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
}
