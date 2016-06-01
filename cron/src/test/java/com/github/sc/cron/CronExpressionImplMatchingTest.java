package com.github.sc.cron;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class CronExpressionImplMatchingTest {
    private static final Logger log = LoggerFactory.getLogger(CronExpressionImplMatchingTest.class);

    @Test
    public void of() throws Exception {
        assertTrue(CronExpressionImpl.of("* 0/5 1,3-5,7/3 ? jan,3,5 mon 2005-3000").match(ZonedDateTime.parse("2016-01-04T10:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* 0/5 1,3-5,7/3 ? 1,3,5 MON 2005-3000").match(ZonedDateTime.parse("2016-01-01T10:00:00.000Z")));
    }

    @Test
    public void testSecondsMatch() throws Exception {
        assertTrue(CronExpressionImpl.of("* * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("0 * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("0/5 * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("0/5 * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:55.000Z")));
        assertTrue(CronExpressionImpl.of("/5 * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("/5 * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:40.000Z")));
        assertTrue(CronExpressionImpl.of("0-3 * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("0,1 * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("1-4,/5 * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:10.000Z")));
        assertTrue(CronExpressionImpl.of("1-4,/5,11-14 * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:12.000Z")));

        assertFalse(CronExpressionImpl.of("0 * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:01.000Z")));
        assertFalse(CronExpressionImpl.of("0/5 * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:01.000Z")));
        assertFalse(CronExpressionImpl.of("/5 * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:01.000Z")));
        assertFalse(CronExpressionImpl.of("0,3,6 * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:01.000Z")));
        assertFalse(CronExpressionImpl.of("2-3 * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:01.000Z")));
    }

    @Test
    public void testMinutesMatch() throws Exception {
        assertTrue(CronExpressionImpl.of("* * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* 0 * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* 0/5 * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* 0/5 * * * ?").match(ZonedDateTime.parse("2016-01-01T12:55:00.000Z")));
        assertTrue(CronExpressionImpl.of("* /5 * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* /5 * * * ?").match(ZonedDateTime.parse("2016-01-01T12:40:00.000Z")));
        assertTrue(CronExpressionImpl.of("* 0-3 * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* 0,1 * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* 1-4,/5 * * * ?").match(ZonedDateTime.parse("2016-01-01T12:10:00.000Z")));
        assertTrue(CronExpressionImpl.of("* 1-4,/5,11-14 * * * ?").match(ZonedDateTime.parse("2016-01-01T12:12:00.000Z")));

        assertFalse(CronExpressionImpl.of("* 0 * * * ?").match(ZonedDateTime.parse("2016-01-01T12:01:00.000Z")));
        assertFalse(CronExpressionImpl.of("* 0/5 * * * ?").match(ZonedDateTime.parse("2016-01-01T12:01:00.000Z")));
        assertFalse(CronExpressionImpl.of("* /5 * * * ?").match(ZonedDateTime.parse("2016-01-01T12:01:00.000Z")));
        assertFalse(CronExpressionImpl.of("* 0,3,6 * * * ?").match(ZonedDateTime.parse("2016-01-01T12:01:00.000Z")));
        assertFalse(CronExpressionImpl.of("* 2-3 * * * ?").match(ZonedDateTime.parse("2016-01-01T12:01:00.000Z")));
    }

    @Test
    public void testHoursMatch() throws Exception {
        assertTrue(CronExpressionImpl.of("* * * * * ?").match(ZonedDateTime.parse("2016-01-01T00:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * 0 * * ?").match(ZonedDateTime.parse("2016-01-01T00:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * 0/5 * * ?").match(ZonedDateTime.parse("2016-01-01T00:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * 0/5 * * ?").match(ZonedDateTime.parse("2016-01-01T10:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * /5 * * ?").match(ZonedDateTime.parse("2016-01-01T00:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * /5 * * ?").match(ZonedDateTime.parse("2016-01-01T05:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * 0-3 * * ?").match(ZonedDateTime.parse("2016-01-01T01:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * 0,1 * * ?").match(ZonedDateTime.parse("2016-01-01T01:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * 1-4,/5 * * ?").match(ZonedDateTime.parse("2016-01-01T10:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * 1-4,/5,11-14 * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));

        assertFalse(CronExpressionImpl.of("* * 0 * * ?").match(ZonedDateTime.parse("2016-01-01T01:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * 0/5 * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * /5 * * ?").match(ZonedDateTime.parse("2016-01-01T21:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * 0,3,6 * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * 2-3 * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
    }

    @Test
    public void testDayOfMonthMatch() throws Exception {
        assertTrue(CronExpressionImpl.of("* * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * 1 * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * 0/5 * ?").match(ZonedDateTime.parse("2016-01-05T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * 0/5 * ?").match(ZonedDateTime.parse("2016-01-30T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * /5 * ?").match(ZonedDateTime.parse("2016-01-05T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * /5 * ?").match(ZonedDateTime.parse("2016-01-15T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * 1-3 * ?").match(ZonedDateTime.parse("2016-01-02T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * 4,1 * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * 1-4,/5 * ?").match(ZonedDateTime.parse("2016-01-05T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * 1-4,/5,11-14 * ?").match(ZonedDateTime.parse("2016-01-12T12:00:00.000Z")));

        assertTrue(CronExpressionImpl.of("* * * l * ?").match(ZonedDateTime.parse("2016-01-31T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * LW * ?").match(ZonedDateTime.parse("2016-01-29T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * L-3 * ?").match(ZonedDateTime.parse("2016-01-28T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * 1W * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * 2w * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * 3W * ?").match(ZonedDateTime.parse("2016-01-04T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * 31W * ?").match(ZonedDateTime.parse("2016-01-29T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * 1W * ?").match(ZonedDateTime.parse("2016-05-02T12:00:00.000Z")));

        assertFalse(CronExpressionImpl.of("* * * 1 * ?").match(ZonedDateTime.parse("2016-01-02T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 0/5 * ?").match(ZonedDateTime.parse("2016-01-03T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * /5 * ?").match(ZonedDateTime.parse("2016-01-31T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 1-3 * ?").match(ZonedDateTime.parse("2016-01-04T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 4,1 * ?").match(ZonedDateTime.parse("2016-01-02T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 1-4,/5 * ?").match(ZonedDateTime.parse("2016-01-07T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 1-4,/5,11-14 * ?").match(ZonedDateTime.parse("2016-01-09T12:00:00.000Z")));

        assertFalse(CronExpressionImpl.of("* * * L * ?").match(ZonedDateTime.parse("2016-01-30T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * L-0 * ?").match(ZonedDateTime.parse("2016-01-30T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * LW * ?").match(ZonedDateTime.parse("2016-01-28T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * LW * ?").match(ZonedDateTime.parse("2016-01-30T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * L-3 * ?").match(ZonedDateTime.parse("2016-01-29T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * L-3 * ?").match(ZonedDateTime.parse("2016-01-27T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 1W * ?").match(ZonedDateTime.parse("2015-12-31T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 1W * ?").match(ZonedDateTime.parse("2016-01-02T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 1W * ?").match(ZonedDateTime.parse("2016-01-04T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 2W * ?").match(ZonedDateTime.parse("2016-01-02T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 2W * ?").match(ZonedDateTime.parse("2016-01-04T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 3W * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 3W * ?").match(ZonedDateTime.parse("2016-01-03T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 31W * ?").match(ZonedDateTime.parse("2016-01-31T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 31W * ?").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 1W * ?").match(ZonedDateTime.parse("2016-05-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * 1W * ?").match(ZonedDateTime.parse("2016-04-29T12:00:00.000Z")));
    }

    @Test
    public void testMonthsMatch() throws Exception {
        assertTrue(CronExpressionImpl.of("* * * * * ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * * 1 ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * * JAN ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * * 1/5 ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * * 1/5 ?").match(ZonedDateTime.parse("2016-11-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * * /5 ?").match(ZonedDateTime.parse("2016-05-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * * /5 ?").match(ZonedDateTime.parse("2016-10-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * * 1-3 ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * * JAN-mar ?").match(ZonedDateTime.parse("2016-03-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * * 4,1 ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * * 4,1,FEB ?").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * * 1-4,/5 ?").match(ZonedDateTime.parse("2016-05-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * * 1-4,/5,11-12 ?").match(ZonedDateTime.parse("2016-12-01T12:00:00.000Z")));

        assertFalse(CronExpressionImpl.of("* * * * 2 ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * * 0/5 ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * * /5 ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * * 1,3,6 ?").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * * 2-3 ?").match(ZonedDateTime.parse("2016-01-01T12:00:00.000Z")));
    }

    @Test
    public void testDayOfWeekMatch() throws Exception {
        // "2016-02-01T12:00:00.000Z" - MONDAY     1
        // "2016-02-02T12:00:00.000Z" - TUESDAY    2
        // "2016-02-03T12:00:00.000Z" - WEDNESDAY  3
        // "2016-02-04T12:00:00.000Z" - THURSDAY   4
        // "2016-02-05T12:00:00.000Z" - FRIDAY     5
        // "2016-02-06T12:00:00.000Z" - SATURDAY   6
        // "2016-02-07T12:00:00.000Z" - SUNDAY     7
        assertTrue(CronExpressionImpl.of("* * * ? * *").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * 1").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * /3").match(ZonedDateTime.parse("2016-02-03T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * /3").match(ZonedDateTime.parse("2016-02-06T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * 1/3").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * 1/3").match(ZonedDateTime.parse("2016-02-07T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * 1-6").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * 1,4").match(ZonedDateTime.parse("2016-02-04T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * MON").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * THU-fri").match(ZonedDateTime.parse("2016-02-05T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * WED/2").match(ZonedDateTime.parse("2016-02-05T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * 1,4-7").match(ZonedDateTime.parse("2016-02-05T12:00:00.000Z")));

        assertTrue(CronExpressionImpl.of("* * * ? * L").match(ZonedDateTime.parse("2016-02-07T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * L-1").match(ZonedDateTime.parse("2016-02-06T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * 2L").match(ZonedDateTime.parse("2016-02-23T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * 1l-1").match(ZonedDateTime.parse("2016-02-28T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * 3#4").match(ZonedDateTime.parse("2016-02-24T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * 1#1").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * ? * 1#5").match(ZonedDateTime.parse("2016-02-29T12:00:00.000Z")));

        assertFalse(CronExpressionImpl.of("* * * ? * 2").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * FRI").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * /3").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * 2/3").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * 2-3").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * 2,3").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * 2,5-7").match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));

        assertFalse(CronExpressionImpl.of("* * * ? * L").match(ZonedDateTime.parse("2016-02-06T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * L").match(ZonedDateTime.parse("2016-02-08T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * L-1").match(ZonedDateTime.parse("2016-02-05T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * 2L").match(ZonedDateTime.parse("2016-02-16T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * 2L").match(ZonedDateTime.parse("2016-02-22T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * 2L").match(ZonedDateTime.parse("2016-02-24T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * 2L").match(ZonedDateTime.parse("2016-03-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * 1L-1").match(ZonedDateTime.parse("2016-02-27T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * 1L-1").match(ZonedDateTime.parse("2016-02-29T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * 3#4").match(ZonedDateTime.parse("2016-02-16T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * 3#4").match(ZonedDateTime.parse("2016-03-01T12:00:00.000Z")));
        assertFalse(CronExpressionImpl.of("* * * ? * 3#5").match(ZonedDateTime.parse("2016-03-01T12:00:00.000Z")));
    }

    @Test
    public void testDayOfWeekWithDayOfMonthMatch() throws Exception {
        // "2016-02-01T12:00:00.000Z" - MONDAY     1
        // "2016-02-02T12:00:00.000Z" - TUESDAY    2
        // "2016-02-03T12:00:00.000Z" - WEDNESDAY  3
        // "2016-02-04T12:00:00.000Z" - THURSDAY   4
        // "2016-02-05T12:00:00.000Z" - FRIDAY     5
        // "2016-02-06T12:00:00.000Z" - SATURDAY   6
        // "2016-02-07T12:00:00.000Z" - SUNDAY     7
        assertTrue(CronExpressionImpl.of("* * * * * *", true).match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * 1 * 1", true).match(ZonedDateTime.parse("2016-02-01T12:00:00.000Z")));
        assertTrue(CronExpressionImpl.of("* * * /3 * /3", true).match(ZonedDateTime.parse("2016-02-03T12:00:00.000Z")));
    }

    @Test
    public void wrongSeconds() throws Exception {
        assertThrows(() -> CronExpressionImpl.of("MON * * * * ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("JAN * * * * ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("W * * * * ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("L * * * * ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("3#3 * * * * ?"), IllegalArgumentException.class);

        testWrongSimpleValues(0);
    }

    @Test
    public void wrongMinutes() throws Exception {
        assertThrows(() -> CronExpressionImpl.of("* MON * * * ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* JAN * * * ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* W * * * ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* L * * * ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* 3#3 * * * ?"), IllegalArgumentException.class);

        testWrongSimpleValues(1);
    }

    @Test
    public void wrongHours() throws Exception {
        assertThrows(() -> CronExpressionImpl.of("* * MON * * ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* * JAN * * ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* * W * * ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* * L * * ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* * 3#3 * * ?"), IllegalArgumentException.class);

        testWrongSimpleValues(2);
    }

    @Test
    public void wrongDayOfMonth() throws Exception {
        assertThrows(() -> CronExpressionImpl.of("* * * MON * ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* * * JAN * ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* * * 3#3 * ?"), IllegalArgumentException.class);

        testWrongSimpleValues(3);
    }

    @Test
    public void wrongMonths() throws Exception {
        assertThrows(() -> CronExpressionImpl.of("* * * * MON ? "), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* * * * W ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* * * * L ?"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* * * * 3#3 ?"), IllegalArgumentException.class);

        testWrongSimpleValues(4);
    }

    @Test
    public void wrongDayOfWeek() throws Exception {
        assertThrows(() -> CronExpressionImpl.of("* * * ? * JAN *"), IllegalArgumentException.class);

        testWrongSimpleValues(5);
    }

    @Test
    public void wrongDayOfMonthWithDayOfWeek() throws Exception {
        assertThrows(() -> CronExpressionImpl.of("* * * 5 * MON *"), IllegalArgumentException.class);
    }

    @Test
    public void wrong() throws Exception {
        assertThrows(() -> CronExpressionImpl.of("* 0/5 1,3-5,7/3 MON JAN,3,5 * 2005-3000"), IllegalArgumentException.class);
    }

    private static void testWrongSimpleValues(int fieldPos) {
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("a", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("-1", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("1--1", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("60", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("1-60", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("1/60", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("60/6", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("6/0", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("6/-1", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("-1/6", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("1,,5", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField(",1,5", fieldPos)), IllegalArgumentException.class);
    }

    private static String buildExpressionForField(String fieldSpec, int fieldPos) {
        StringBuilder expression = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            expression.append(" ").append(i == fieldPos ? fieldSpec : "*");
        }
        expression.append(" ").append(5 == fieldPos ? fieldSpec : "?");
        return expression.toString().trim();
    }

    private static void assertThrows(Runnable action, Class<? extends Exception> exClass) {
        try {
            action.run();
            fail("No exception of class " + exClass + " was thrown");
        } catch (Exception e) {
            assertThat(e.getClass(), equalTo(exClass));
            log.debug("Expected exception was thrown: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

}