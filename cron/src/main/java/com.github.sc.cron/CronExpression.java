package com.github.sc.cron;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Cron expression
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */

public interface CronExpression {

    /**
     * @param dateTime time
     * @return true if time match to cron expression and false otherwise
     */
    boolean match(ZonedDateTime dateTime);

    /**
     * Try to find next time that will match to cron expression and will be strictly after
     * specified time
     *
     * @param afterTime time
     * @return empty if no any time found and next time otherwise
     */
    Optional<ZonedDateTime> nextFireAfter(ZonedDateTime afterTime);
}
