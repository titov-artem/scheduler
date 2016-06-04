package com.github.sc.cron;

import java.time.ZonedDateTime;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
interface CronPart {

    boolean match(ZonedDateTime dateTime);
}
