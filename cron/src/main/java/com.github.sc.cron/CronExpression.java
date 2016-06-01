package com.github.sc.cron;

import java.time.ZonedDateTime;

public interface CronExpression {

    boolean match(ZonedDateTime dateTime);

    ZonedDateTime nextTimeAfter(ZonedDateTime afterTime);
}
