package com.github.sc.cron;

import java.util.regex.Pattern;

abstract class AbstractComplicatedCronFieldPart {

    protected static final String LAST_MARK = "L";
    protected static final String LEGAL_MARK = "#";
    protected static final String WEEKDAYS_MARK = "W";

    protected static final Pattern K_BEFORE_LAST = Pattern.compile("L\\-[0-9]+");

}
