package com.github.sc.cron;

import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CronExpressionImplNextFireTimeTest {

    @Test
    public void checkAll() throws Exception {
        assertThat(CronExpressionImpl.of("* * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:01.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:00:02.000Z")));
        assertThat(CronExpressionImpl.of("* * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:02:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:02:01.000Z")));
        assertThat(CronExpressionImpl.of("* * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:59:59.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T14:00:00.000Z")));
    }

    @Test
    public void checkSecondNumber() throws Exception {
        assertThat(CronExpressionImpl.of("3 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:01:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:01:03.000Z")));
        assertThat(CronExpressionImpl.of("3 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:01:03.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:02:03.000Z")));
        assertThat(CronExpressionImpl.of("3 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:59:03.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T14:00:03.000Z")));
        assertThat(CronExpressionImpl.of("3 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T23:59:03.000Z")).get(), is(ZonedDateTime.parse("2012-04-11T00:00:03.000Z")));
        assertThat(CronExpressionImpl.of("3 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-30T23:59:03.000Z")).get(), is(ZonedDateTime.parse("2012-05-01T00:00:03.000Z")));
    }

    @Test
    public void checkSecondIncrement() throws Exception {
        assertThat(CronExpressionImpl.of("5/15 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:00:05.000Z")));
        assertThat(CronExpressionImpl.of("5/15 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:05.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:00:20.000Z")));
        assertThat(CronExpressionImpl.of("5/15 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:20.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:00:35.000Z")));
        assertThat(CronExpressionImpl.of("5/15 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:35.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:00:50.000Z")));
        assertThat(CronExpressionImpl.of("5/15 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:50.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:01:05.000Z")));

        // if rolling over minute then reset second (cron rules - increment affects only values in own field)
//        assertThat(CronExpressionImpl.of("10/100 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:50.000Z")).get(),
//                is(ZonedDateTime.parse("2012-04-10T13:01:10.000Z")));
//        assertThat(CronExpressionImpl.of("10/100 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:01:10.000Z")).get(),
//                is(ZonedDateTime.parse("2012-04-10T13:02:10.000Z")));
    }

    @Test
    public void checkSecondList() throws Exception {
        assertThat(CronExpressionImpl.of("7,19 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:00:07.000Z")));
        assertThat(CronExpressionImpl.of("7,19 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:07.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:00:19.000Z")));
        assertThat(CronExpressionImpl.of("7,19 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:19.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:01:07.000Z")));
    }

    @Test
    public void checkSecondRange() throws Exception {
        assertThat(CronExpressionImpl.of("42-45 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:00:42.000Z")));
        assertThat(CronExpressionImpl.of("42-45 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:42.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:00:43.000Z")));
        assertThat(CronExpressionImpl.of("42-45 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:43.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:00:44.000Z")));
        assertThat(CronExpressionImpl.of("42-45 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:44.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:00:45.000Z")));
        assertThat(CronExpressionImpl.of("42-45 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:45.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:01:42.000Z")));
    }

    @Test
    public void checkMinuteNumber() throws Exception {
        assertThat(CronExpressionImpl.of("0 3 * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:01:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:03:00.000Z")));
        assertThat(CronExpressionImpl.of("0 3 * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:03:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T14:03:00.000Z")));
    }

    @Test
    public void checkMinuteIncrement() throws Exception {
        assertThat(CronExpressionImpl.of("0 0/15 * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:15:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0/15 * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:15:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:30:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0/15 * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:30:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:45:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0/15 * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:45:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T14:00:00.000Z")));
    }

    @Test
    public void checkMinutesRange() throws Exception {
        assertThat(CronExpressionImpl.of("0 42-45 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:42:00.000Z")));
        assertThat(CronExpressionImpl.of("0 42-45 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:42:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:43:00.000Z")));
        assertThat(CronExpressionImpl.of("0 42-45 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:43:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:44:00.000Z")));
        assertThat(CronExpressionImpl.of("0 42-45 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:44:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:45:00.000Z")));
        assertThat(CronExpressionImpl.of("0 42-45 * * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:45:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T14:42:00.000Z")));
    }


    @Test
    public void checkMinuteList() throws Exception {
        assertThat(CronExpressionImpl.of("0 7,19 * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:07:00.000Z")));
        assertThat(CronExpressionImpl.of("0 7,19 * * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:07:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T13:19:00.000Z")));
    }

    @Test
    public void checkHourNumber() throws Exception {
        assertThat(CronExpressionImpl.of("0 * 3 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:01:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-11T03:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 * 3 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-11T03:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-11T03:01:00.000Z")));
        assertThat(CronExpressionImpl.of("0 * 3 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-11T03:59:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-12T03:00:00.000Z")));
    }

    @Test
    public void checkHourIncrement() throws Exception {
        assertThat(CronExpressionImpl.of("0 * 0/15 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T15:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 * 0/15 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T15:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T15:01:00.000Z")));
        assertThat(CronExpressionImpl.of("0 * 0/15 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T15:59:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-11T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 * 0/15 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-11T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-11T00:01:00.000Z")));
        assertThat(CronExpressionImpl.of("0 * 0/15 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-11T15:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-11T15:01:00.000Z")));
    }

    @Test
    public void checkHoursRange() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 7-9 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T07:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 7-9 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T07:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T08:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 7-9 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T08:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T09:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 7-9 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T09:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-11T07:00:00.000Z")));
    }


    @Test
    public void checkHourList() throws Exception {
        assertThat(CronExpressionImpl.of("0 * 7,19 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T19:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 * 7,19 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T19:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T19:01:00.000Z")));
        assertThat(CronExpressionImpl.of("0 * 7,19 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T19:59:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-11T07:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 * 7,19,23 * * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T22:59:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-10T23:00:00.000Z")));
    }

    @Test
    public void checkHourShallRun25TimesInDSTChangeToWintertime() throws Exception {
        CronExpressionImpl cron = CronExpressionImpl.of("0 1 * * * *");
        ZonedDateTime start = ZonedDateTime.of(2011, 10, 30, 00, 00, 00, 000, ZoneId.of("Europe/Oslo"));
        ZonedDateTime tomorrow = start.plusDays(1);
        assertTrue(ChronoUnit.HOURS.between(start, tomorrow) == 25L);
        ZonedDateTime cur = start;
        ZonedDateTime lastFireTime = start;
        int count = 0;
        while (cur.isBefore(tomorrow)) {
            Optional<ZonedDateTime> nextTime = cron.nextFireAfter(cur);
            assertTrue(nextTime.isPresent() && nextTime.get().isAfter(lastFireTime));
            lastFireTime = nextTime.get();
            cur = cur.plusHours(1);
            count++;
        }
        assertTrue(count == 25);
    }

    @Test
    public void checkHourShallRun23TimesInDSTChangeToSummertime() throws Exception {
        CronExpressionImpl cron = CronExpressionImpl.of("0 0 * * * *");
        ZonedDateTime start = ZonedDateTime.of(2011, 03, 27, 00, 00, 00, 000, ZoneId.of("Europe/Oslo"));
        ZonedDateTime tomorrow = start.plusDays(1);
        assertTrue(ChronoUnit.HOURS.between(start, tomorrow) == 23L);
        ZonedDateTime cur = start;
        ZonedDateTime lastFireTime = start;
        int count = 0;
        while (cur.isBefore(tomorrow)) {
            Optional<ZonedDateTime> nextTime = cron.nextFireAfter(cur);
            assertTrue(nextTime.isPresent() && nextTime.get().isAfter(lastFireTime));
            lastFireTime = nextTime.get();
            cur = cur.plusHours(1);
            count++;
        }
        assertTrue(count == 23);
    }

    @Test
    public void checkDayOfMonthNumber() throws Exception {
        assertThat(CronExpressionImpl.of("0 * * 3 * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-03T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 * * 3 * *", true).nextFireAfter(ZonedDateTime.parse("2012-05-03T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-03T00:01:00.000Z")));
        assertThat(CronExpressionImpl.of("0 * * 3 * *", true).nextFireAfter(ZonedDateTime.parse("2012-05-03T00:59:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-03T01:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 * * 3 * *", true).nextFireAfter(ZonedDateTime.parse("2012-05-03T23:59:00.000Z")).get(), is(ZonedDateTime.parse("2012-06-03T00:00:00.000Z")));
    }

    @Test
    public void checkDayOfMonthIncrement() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 1/15 * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-16T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 1/15 * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-16T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-01T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 1/15 * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-30T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-01T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 1/15 * *", true).nextFireAfter(ZonedDateTime.parse("2012-05-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-16T00:00:00.000Z")));
    }

    @Test
    public void checkDayOfMonthList() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 7,19 * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-19T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 7,19 * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-19T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-07T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 7,19 * *", true).nextFireAfter(ZonedDateTime.parse("2012-05-07T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-19T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 7,19 * *", true).nextFireAfter(ZonedDateTime.parse("2012-05-30T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-06-07T00:00:00.000Z")));
    }

    @Test
    public void checkDayOfMonthRange() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 7-9 * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-07T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 7-9 * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-07T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-08T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 7-9 * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-08T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-09T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 7-9 * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-09T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-07T00:00:00.000Z")));
    }

    @Test
    public void checkDayOfMonthLast() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 L * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-30T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 L * *", true).nextFireAfter(ZonedDateTime.parse("2012-02-12T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-02-29T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 L-2 * *", true).nextFireAfter(ZonedDateTime.parse("2012-02-12T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-02-27T00:00:00.000Z")));
    }

//    @Test
//    public void checkDayOfMonthNumberLastL() throws Exception {
//        assertThat(CronExpressionImpl.of("0 0 0 3L * *", true).nextFireAfter(ZonedDateTime.parse("2012-04-10T13:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-27T00:00:00.000Z")));
//        assertThat(CronExpressionImpl.of("0 0 0 3L * *", true).nextFireAfter(ZonedDateTime.parse("2012-02-12T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-02-36T00:00:00.000Z")));
//    }

    @Test
    public void checkDayOfMonthClosestWeekdayW() throws Exception {
        // 9 - is weekday in may
        assertThat(CronExpressionImpl.of("0 0 0 9W * *", true).nextFireAfter(ZonedDateTime.parse("2012-05-02T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-09T00:00:00.000Z")));

        // 9 - is weekday in may
        assertThat(CronExpressionImpl.of("0 0 0 9W * *", true).nextFireAfter(ZonedDateTime.parse("2012-05-08T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-09T00:00:00.000Z")));

        // 9 - saturday, friday closest weekday in june
        assertThat(CronExpressionImpl.of("0 0 0 9W * *", true).nextFireAfter(ZonedDateTime.parse("2012-05-09T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-06-08T00:00:00.000Z")));

        // 9 - sunday, monday closest weekday in september
        assertThat(CronExpressionImpl.of("0 0 0 9W * *", true).nextFireAfter(ZonedDateTime.parse("2012-09-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-09-10T00:00:00.000Z")));
    }

    @Test
    public void checkMonthNumber() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 1 5 *", true).nextFireAfter(ZonedDateTime.parse("2012-02-12T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-01T00:00:00.000Z")));
    }

    @Test
    public void checkMonthIncrement() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 1 5/2 *", true).nextFireAfter(ZonedDateTime.parse("2012-02-12T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-01T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 1 5/2 *", true).nextFireAfter(ZonedDateTime.parse("2012-05-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-07-01T00:00:00.000Z")));

        // if rolling over year then reset month field (cron rules - increments only affect own field)
        assertThat(CronExpressionImpl.of("0 0 0 1 5/10 *", true).nextFireAfter(ZonedDateTime.parse("2012-05-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2013-05-01T00:00:00.000Z")));
    }

    @Test
    public void checkMonthList() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 1 3,7,12 *", true).nextFireAfter(ZonedDateTime.parse("2012-02-12T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-03-01T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 1 3,7,12 *", true).nextFireAfter(ZonedDateTime.parse("2012-03-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-07-01T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 1 3,7,12 *", true).nextFireAfter(ZonedDateTime.parse("2012-07-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-12-01T00:00:00.000Z")));
    }

    @Test
    public void checkMonthRange() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 1 7-9 *", true).nextFireAfter(ZonedDateTime.parse("2012-01-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-07-01T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 1 7-9 *", true).nextFireAfter(ZonedDateTime.parse("2012-07-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-08-01T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 1 7-9 *", true).nextFireAfter(ZonedDateTime.parse("2012-08-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-09-01T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 1 7-9 *", true).nextFireAfter(ZonedDateTime.parse("2012-09-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2013-07-01T00:00:00.000Z")));
    }

    @Test
    public void checkMonthListByName() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 1 MAR,JUL,DEC *", true).nextFireAfter(ZonedDateTime.parse("2012-02-12T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-03-01T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 1 MAR,JUL,DEC *", true).nextFireAfter(ZonedDateTime.parse("2012-03-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-07-01T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 1 MAR,JUL,DEC *", true).nextFireAfter(ZonedDateTime.parse("2012-07-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-12-01T00:00:00.000Z")));
    }

    @Test
    public void checkDayOfWeekNumber() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 * * 3", true).nextFireAfter(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-04T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 3", true).nextFireAfter(ZonedDateTime.parse("2012-04-04T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-11T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 3", true).nextFireAfter(ZonedDateTime.parse("2012-04-12T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-18T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 3", true).nextFireAfter(ZonedDateTime.parse("2012-04-18T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-25T00:00:00.000Z")));
    }

    @Test
    public void checkDayOfWeekIncrement() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 * * 3/2", true).nextFireAfter(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-04T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 3/2", true).nextFireAfter(ZonedDateTime.parse("2012-04-04T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-06T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 3/2", true).nextFireAfter(ZonedDateTime.parse("2012-04-06T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-08T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 3/2", true).nextFireAfter(ZonedDateTime.parse("2012-04-08T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-11T00:00:00.000Z")));
    }

    @Test
    public void checkDayOfWeekList() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 * * 1,5,7", true).nextFireAfter(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-02T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 1,5,7", true).nextFireAfter(ZonedDateTime.parse("2012-04-02T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-06T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 1,5,7", true).nextFireAfter(ZonedDateTime.parse("2012-04-06T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-08T00:00:00.000Z")));
    }

    @Test
    public void checkDayOfWeekRange() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 * * 3-5", true).nextFireAfter(ZonedDateTime.parse("2016-05-30T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2016-06-01T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 3-5", true).nextFireAfter(ZonedDateTime.parse("2016-06-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2016-06-02T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 3-5", true).nextFireAfter(ZonedDateTime.parse("2016-06-02T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2016-06-03T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 3-5", true).nextFireAfter(ZonedDateTime.parse("2016-06-03T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2016-06-08T00:00:00.000Z")));
    }

    @Test
    public void checkDayOfWeekListByName() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 * * MON,FRI,SUN", true).nextFireAfter(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-02T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * MON,FRI,SUN", true).nextFireAfter(ZonedDateTime.parse("2012-04-02T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-06T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * MON,FRI,SUN", true).nextFireAfter(ZonedDateTime.parse("2012-04-06T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-08T00:00:00.000Z")));
    }

    @Test
    public void checkDayOfWeekLastFridayInMonth() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 * * 5L", true).nextFireAfter(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-27T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 5L", true).nextFireAfter(ZonedDateTime.parse("2012-04-27T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-25T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 5L", true).nextFireAfter(ZonedDateTime.parse("2012-02-06T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-02-24T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * FRIL", true).nextFireAfter(ZonedDateTime.parse("2012-02-06T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-02-24T00:00:00.000Z")));
    }

//    @Test
//    public void checkDayOfWeekShallInterpret_0AsSunday() throws Exception {
//        assertThat(CronExpressionImpl.of("0 0 0 * * 0", true).nextFireAfter(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-08T00:00:00.000Z")));
//        assertThat(CronExpressionImpl.of("0 0 0 * * 0L", true).nextFireAfter(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-29T00:00:00.000Z")));
//        assertThat(CronExpressionImpl.of("0 0 0 * * 0#2", true).nextFireAfter(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-08T00:00:00.000Z")));
//    }

    @Test
    public void checkDayOfWeekShallInterpret7AsSunday() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 * * 7", true).nextFireAfter(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-08T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 7L", true).nextFireAfter(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-29T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 7#2", true).nextFireAfter(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-08T00:00:00.000Z")));
    }

    @Test
    public void checkDayOfWeekNthDayInMonth() throws Exception {
        assertThat(CronExpressionImpl.of("0 0 0 * * 5#3", true).nextFireAfter(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-20T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 5#3", true).nextFireAfter(ZonedDateTime.parse("2012-04-20T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-18T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 7#1", true).nextFireAfter(ZonedDateTime.parse("2012-03-30T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 7#1", true).nextFireAfter(ZonedDateTime.parse("2012-04-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-05-06T00:00:00.000Z")));
        assertThat(CronExpressionImpl.of("0 0 0 * * 3#5", true).nextFireAfter(ZonedDateTime.parse("2012-02-06T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-02-29T00:00:00.000Z"))); // leapday
        assertThat(CronExpressionImpl.of("0 0 0 * * WED#5", true).nextFireAfter(ZonedDateTime.parse("2012-02-06T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2012-02-29T00:00:00.000Z"))); // leapday
    }

    @Test
    public void nonExistingDateReturnEmpty() throws Exception {
        assertFalse(CronExpressionImpl.of("* * * 30 2 *", true).nextFireAfter(ZonedDateTime.parse("2016-01-01T00:00:00.000Z")).isPresent());
    }

    @Test
    public void testLeapYear() throws Exception {
        assertThat(CronExpressionImpl.of("* * * 29 2 *", true).nextFireAfter(ZonedDateTime.parse("2012-03-01T00:00:00.000Z")).get(), is(ZonedDateTime.parse("2016-02-29T00:00:00.000Z")));
    }

    @Test
    public void testEdgeValues() throws Exception {
        assertEquals(CronExpressionImpl.of("59 59 23 31 12 ?").nextFireAfter(ZonedDateTime.parse("2012-01-01T00:00:00.000Z")).get(), ZonedDateTime.parse("2012-12-31T23:59:59.000Z"));
        assertEquals(CronExpressionImpl.of("0 0 0 1 1 ?").nextFireAfter(ZonedDateTime.parse("2012-01-01T00:00:00.000Z")).get(), ZonedDateTime.parse("2013-01-01T00:00:00.000Z"));
    }

}