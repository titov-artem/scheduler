package com.github.sc.cron;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;

class DayOfMonthCronFieldPart extends AbstractComplicatedCronFieldPart implements CronPart {

    private static final int NONE = -1;

    private static final CronFieldType TYPE = CronFieldType.DAY_OF_MONTH;

    private final CronPart simple;
    private boolean lastWeekDay;
    private int nearestWeekDayToDay;
    private int kBeforeLastDay;

    private DayOfMonthCronFieldPart(CronPart simple, boolean lastWeekDay, int nearestWeekDayToDay, int kBeforeLastDay) {
        this.simple = simple;
        this.lastWeekDay = lastWeekDay;
        this.nearestWeekDayToDay = nearestWeekDayToDay;
        this.kBeforeLastDay = kBeforeLastDay;
    }

    private static DayOfMonthCronFieldPart simple(SimpleCronFieldPart simple) {
        return new DayOfMonthCronFieldPart(simple, false, NONE, NONE);
    }

    private static DayOfMonthCronFieldPart lastWeekDay() {
        return new DayOfMonthCronFieldPart(null, true, NONE, NONE);
    }

    private static DayOfMonthCronFieldPart nearestWeekDayTo(int dayOfMonth) {
        return new DayOfMonthCronFieldPart(null, false, dayOfMonth, NONE);
    }

    private static DayOfMonthCronFieldPart kBeforeLastDay(int k) {
        return new DayOfMonthCronFieldPart(null, false, NONE, k);
    }

    @Override
    public boolean match(ZonedDateTime dateTime) {
        if (simple != null) {
            return simple.match(dateTime);
        }
        if (lastWeekDay) {
            int dayOfWeek = dateTime.getDayOfWeek().getValue();
            if (dayOfWeek >= DayOfWeek.SATURDAY.getValue()) return false;

            int month = dateTime.getMonthValue();
            int nextDayMonth = dateTime.plusDays(1).getMonthValue();
            if (month != nextDayMonth) return true;

            int nextDayOfWeek = dateTime.plusDays(1).getDayOfWeek().getValue();
            if (nextDayOfWeek <= DayOfWeek.FRIDAY.getValue()) return false;

            int nextSundayMonth = dateTime.plusDays(3).getMonthValue();
            return month != nextSundayMonth;
        }
        if (nearestWeekDayToDay != NONE) {
            int dayOfMonth = dateTime.getDayOfMonth();
            int dayOfWeek = dateTime.getDayOfWeek().getValue();
            // if today not week day - false
            if (dayOfWeek >= DayOfWeek.SATURDAY.getValue()) return false;

            // if today is that day - return true
            if (dayOfMonth == nearestWeekDayToDay) return true;

            int month = dateTime.getMonthValue();
            int leftDays = nearestWeekDayToDay - dayOfMonth; // maybe negative
            ZonedDateTime thatDay = dateTime.plusDays(leftDays);
            int thatMonth = thatDay.getMonthValue();
            int thatDayOfWeek = thatDay.getDayOfWeek().getValue();

            // if today and that day have different months - return false
            if (month != thatMonth) return false;

            // if that day will be, or was week day - return false
            if (thatDayOfWeek <= DayOfWeek.FRIDAY.getValue()) return false;

            // here we are if that day is SATURDAY or SUNDAY and it will be the same month

            // if that day is SATURDAY, then return "Is today FRIDAY?"
            if (thatDayOfWeek == DayOfWeek.SATURDAY.getValue()) {
                if (dayOfWeek == DayOfWeek.FRIDAY.getValue()) return true;
                if (dayOfWeek == DayOfWeek.MONDAY.getValue()) {
                    return thatDay.minusDays(1).getMonthValue() != thatMonth;
                }
            }

            // if that day is SUNDAY, then return "Is today MONDAY?"
            if (thatDayOfWeek == DayOfWeek.SUNDAY.getValue()) {
                if (dayOfWeek == DayOfWeek.MONDAY.getValue()) return true;
                if (dayOfWeek == DayOfWeek.FRIDAY.getValue()) {
                    return thatDay.plusDays(1).getMonthValue() != thatMonth;
                }
            }

            return false;
        }
        if (kBeforeLastDay != NONE) {
            int month = dateTime.getMonthValue();
            int afterKDaysMonth = dateTime.plusDays(kBeforeLastDay).getMonthValue();
            if (month != afterKDaysMonth) return false;

            int afterKP1DayMonth = dateTime.plusDays(kBeforeLastDay + 1).getMonthValue();
            return month != afterKP1DayMonth;
        }

        return false;
    }

    static DayOfMonthCronFieldPart of(String part) {
        if (!part.contains(LAST_MARK) && !part.contains(WEEKDAYS_MARK) && !part.contains(LEGAL_MARK)) {
            return simple(SimpleCronFieldPart.of(part, TYPE));
        }

        // LW last weekday of the month
        if (part.contains(LAST_MARK) && part.contains(WEEKDAYS_MARK)) {
            return lastWeekDay();
        }

        // L or L-3
        if (part.contains(LAST_MARK)) {
            if (LAST_MARK.equals(part)) {
                return kBeforeLastDay(0);
            }
            // L-3 before 3 days to SUNDAY
            if (K_BEFORE_LAST.matcher(part).matches()) {
                int offset = Integer.parseInt(part.split("-")[1]);
                Utils.validateOffset(TYPE, offset);
                return kBeforeLastDay(offset);
            }
        }

        // 15W
        if (part.contains(WEEKDAYS_MARK)) {
            int day = Integer.parseInt(part.substring(0, part.length() - 1));
            Utils.validateValues(TYPE, day);
            return nearestWeekDayTo(day);
        }

        throw new IllegalArgumentException(String.format("Wrong format for part %s in field %s",
                part, TYPE.name()));
    }
}
