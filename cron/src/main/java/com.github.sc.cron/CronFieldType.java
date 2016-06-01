package com.github.sc.cron;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZonedDateTime;

enum CronFieldType {

    SECONDS(0, 59, true) {
        @Override
        public int getValue(ZonedDateTime dateTime) {
            return dateTime.getSecond();
        }

        @Override
        public CronPart parseFieldPart(String fieldPart) {
            return SimpleCronFieldPart.of(fieldPart, this);
        }
    },
    MINUTES(0, 59, true) {
        @Override
        public int getValue(ZonedDateTime dateTime) {
            return dateTime.getMinute();
        }

        @Override
        public CronPart parseFieldPart(String fieldPart) {
            return SimpleCronFieldPart.of(fieldPart, this);
        }
    },
    HOURS(0, 23, true) {
        @Override
        public int getValue(ZonedDateTime dateTime) {
            return dateTime.getHour();
        }

        @Override
        public CronPart parseFieldPart(String fieldPart) {
            return SimpleCronFieldPart.of(fieldPart, this);
        }
    },
    DAY_OF_MONTH(1, 31, true) {
        @Override
        public int getValue(ZonedDateTime dateTime) {
            return dateTime.getDayOfMonth();
        }

        @Override
        public CronPart parseFieldPart(String fieldPart) {
            return DayOfMonthCronFieldPart.of(fieldPart);
        }
    },
    MONTHS(1, 12, true) {
        @Override
        public int getValue(ZonedDateTime dateTime) {
            return dateTime.getMonthValue();
        }

        @Override
        public CronPart parseFieldPart(String fieldPart) {
            return SimpleCronFieldPart.of(fieldPart, this);
        }

        @Override
        public String normalize(String rawField) {
            String normalizedField = rawField.toUpperCase();
            for (Month month : Month.values()) {
                normalizedField = normalizedField.replace(month.name().substring(0, 3), Integer.toString(month.getValue()));
            }
            return normalizedField;

        }
    },
    /**
     * Day of week, Monday - 1 and Sunday - 7
     */
    DAY_OF_WEEK(1, 7, true) {
        @Override
        public int getValue(ZonedDateTime dateTime) {
            return dateTime.getDayOfWeek().getValue();
        }

        @Override
        public CronPart parseFieldPart(String fieldPart) {
            return DayOfWeekCronFieldPart.of(fieldPart);
        }

        @Override
        public String normalize(String rawField) {
            String normalizedField = rawField.toUpperCase();
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                normalizedField = normalizedField.replace(dayOfWeek.name().substring(0, 3), Integer.toString(dayOfWeek.getValue()));
            }
            return normalizedField;
        }
    },
    YEARS(LocalDate.MIN.getYear(), LocalDate.MAX.getYear(), false) {
        @Override
        public int getValue(ZonedDateTime dateTime) {
            return dateTime.getYear();
        }

        @Override
        public CronPart parseFieldPart(String fieldPart) {
            return SimpleCronFieldPart.of(fieldPart, this);
        }
    };

    private final int minValue;
    private final int maxValue;
    private boolean mandatory;

    CronFieldType(int minValue, int maxValue, boolean mandatory) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.mandatory = mandatory;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public abstract int getValue(ZonedDateTime dateTime);

    public final CronField parse(String rawField) {
        return CronFieldImpl.of(normalize(rawField), this, (p, t) -> this.parseFieldPart(p));
    }

    protected abstract CronPart parseFieldPart(String fieldPart);

    /**
     * Normalize field by replacing all symbolic synonyms to numbers
     *
     * @param rawField origin field
     * @return normalized field
     */
    public String normalize(String rawField) {
        return rawField;
    }
}
