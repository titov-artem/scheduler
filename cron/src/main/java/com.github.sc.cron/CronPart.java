package com.github.sc.cron;

import java.time.ZonedDateTime;
import java.util.regex.Pattern;

interface CronPart {

    String INC_MARK = "/";
    Pattern SIMPLE_RANGE = Pattern.compile("[0-9]+\\-[0-9]+");
    Pattern SINGLE_VALUE = Pattern.compile("[0-9]+");

    boolean match(ZonedDateTime dateTime);
}
