package com.github.sc.cron;

import java.time.ZonedDateTime;

interface CronPart {

    boolean match(ZonedDateTime dateTime);
}
