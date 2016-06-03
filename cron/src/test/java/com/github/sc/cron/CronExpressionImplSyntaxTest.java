package com.github.sc.cron;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class CronExpressionImplSyntaxTest {
    private static final Logger log = LoggerFactory.getLogger(CronExpressionImplMatchingTest.class);

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

    @Test
    public void wrongPeriod() throws Exception {
        assertThrows(() -> CronExpressionImpl.of("3-1 * * * * * *"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* 2-1 * * * * *"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* * 5-1 * * * *"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* * * 5-1 * * *"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* * * * 5-1 * *"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* * * * * 5-1 *"), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of("* * * * * * 5-1"), IllegalArgumentException.class);
    }


    private static void testWrongSimpleValues(int fieldPos) {
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("a", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("-1", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("1--1", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("60", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("1-60", fieldPos)), IllegalArgumentException.class);
        assertThrows(() -> CronExpressionImpl.of(buildExpressionForField("1/-60", fieldPos)), IllegalArgumentException.class);
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