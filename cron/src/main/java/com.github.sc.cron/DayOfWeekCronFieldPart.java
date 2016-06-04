package com.github.sc.cron;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;

import static com.github.sc.cron.Utils.checkArgument;

class DayOfWeekCronFieldPart extends AbstractComplicatedCronFieldPart implements CronPart {

    private static final Pattern LAST_XXX_DAY = Pattern.compile("[0-9]+L(\\-[0-9]+)?");

    private static final CronFieldType TYPE = CronFieldType.DAY_OF_WEEK;

    /**
     * For parts without L, W or # and for parts with this marks, that can be translated to prats without them
     */
    private final CronPart simple;
    /**
     * For parts with L
     */
    private final Last last;
    /**
     * For parts with #
     */
    private final NthXDayOfMonth nthXDay;

    private DayOfWeekCronFieldPart(CronPart simple, Last last, NthXDayOfMonth nthXDay) {
        this.simple = simple;
        this.last = last;
        this.nthXDay = nthXDay;
    }

    private static DayOfWeekCronFieldPart simple(SimpleCronFieldPart simple) {
        return new DayOfWeekCronFieldPart(simple, null, null);
    }

    private static DayOfWeekCronFieldPart last(Last last) {
        return new DayOfWeekCronFieldPart(null, last, null);
    }

    private static DayOfWeekCronFieldPart nthXDayOfMonth(NthXDayOfMonth nthXDay) {
        return new DayOfWeekCronFieldPart(null, null, nthXDay);
    }

    @Override
    public boolean match(ZonedDateTime dateTime) {
        if (simple != null) {
            return simple.match(dateTime);
        }
        if (last != null) {
            ZonedDateTime future = dateTime.plusDays(last.offset);
            if (future.getDayOfWeek().getValue() != last.dayOfWeek) {
                return false;
            }
            int month = future.getMonthValue();
            int nextWeekMonth = future.plusWeeks(1).getMonthValue();
            return month != nextWeekMonth;
        }
        if (nthXDay != null) {
            int dayOfWeek = dateTime.getDayOfWeek().getValue();
            if (dayOfWeek != nthXDay.dayOfWeek) return false;
            int curMonth = dateTime.getMonthValue();
            int nthPrevMonth = dateTime.minusWeeks(nthXDay.number - 1).getMonthValue();
            int nP1ThPrevMonth = dateTime.minusWeeks(nthXDay.number).getMonthValue();
            return curMonth == nthPrevMonth && curMonth != nP1ThPrevMonth;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s",
                simple != null ? simple.toString() : "",
                last != null ? last.toString() : "",
                nthXDay != null ? nthXDay.toString() : ""
        );
    }

    static DayOfWeekCronFieldPart of(String part) {
        checkArgument(part != null && !part.trim().isEmpty(),
                "Empty cron field parts for field %s is forbidden", TYPE.name());
        if (!part.contains(LAST_MARK) && !part.contains(WEEKDAYS_MARK) && !part.contains(LEGAL_MARK)) {
            return simple(SimpleCronFieldPart.of(part, TYPE));
        }
        // 3#4 - 4th WEDNESDAY of the month
        if (part.contains(LEGAL_MARK)) {
            String[] split = part.split(LEGAL_MARK);
            int dayOfWeek = Integer.parseInt(split[0]);
            int number = Integer.parseInt(split[1]);
            return nthXDayOfMonth(new NthXDayOfMonth(dayOfWeek, number));
        }
        if (part.contains(LAST_MARK)) {
            // L - SUNDAY
            if (LAST_MARK.equals(part)) {
                return simple(SimpleCronFieldPart.of(
                        part.replace("L", Integer.toString(DayOfWeek.SUNDAY.getValue())),
                        TYPE
                ));
            }
            // L-3 before 3 days to SUNDAY
            if (K_BEFORE_LAST.matcher(part).matches()) {
                int offset = Integer.parseInt(part.split("-")[1]);
                Utils.validateOffset(TYPE, offset);
                return simple(SimpleCronFieldPart.of(
                        Integer.toString(DayOfWeek.SUNDAY.getValue() - offset),
                        TYPE
                ));
            }
            // 3L-1 - 1 day before last WEDNESDAY of the month
            if (LAST_XXX_DAY.matcher(part).matches()) {
                String[] split = part.split("-");
                int offset = 0;
                if (split.length == 2) {
                    offset = Integer.parseInt(part.split("-")[1]);
                }
                int dayNumber = Integer.parseInt(split[0].replace("L", ""));
                Utils.validateValues(TYPE, dayNumber);
                Utils.validateOffset(TYPE, offset);

                return last(new Last(dayNumber, offset));
            }
        }
        // W - week days from MONDAY to FRIDAY
        if (part.equals(WEEKDAYS_MARK)) {
            return simple(SimpleCronFieldPart.of(
                    String.format("%d-%d", DayOfWeek.MONDAY.getValue(), DayOfWeek.FRIDAY.getValue()),
                    TYPE
            ));
        }
        throw new IllegalArgumentException(String.format("Wrong format for part %s in field %s",
                part, TYPE.name()));
    }

    /**
     * Descriptor for k days before last XXX day of the week in the month.
     * For example 1 day before last SATURDAY in the month (6L-1)
     */
    private static final class Last {
        /**
         * Day of the week
         */
        final int dayOfWeek;
        /**
         * Offset to the past from the {@code day}
         */
        final int offset;

        Last(int dayOfWeek, int offset) {
            this.dayOfWeek = dayOfWeek;
            this.offset = offset;
        }

        @Override
        public String toString() {
            return String.format("%dL-%d", dayOfWeek, offset);
        }
    }

    /**
     * Descriptor for nth XXX day of the month. For example 3rd FRIDAY of the month (5#3)
     */
    private static final class NthXDayOfMonth {
        /**
         * Day of week in the month
         */
        final int dayOfWeek;
        /**
         * Number of such day of week in the month
         */
        final int number;

        NthXDayOfMonth(int dayOfWeek, int number) {
            this.dayOfWeek = dayOfWeek;
            this.number = number;
        }

        @Override
        public String toString() {
            return String.format("%d#%d", dayOfWeek, number);
        }
    }
}
