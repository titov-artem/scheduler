package com.github.sc.cron;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface CronExpression {

    boolean match(ZonedDateTime dateTime);

    Optional<ZonedDateTime> nextFireAfter(ZonedDateTime afterTime);
}
