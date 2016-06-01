package com.github.sc.cron;

/**
 * Field of the cron expression. One of
 * <ul>
 * <li>SECONDS</li>
 * <li>MINUTES</li>
 * <li>HOURS</li>
 * <li>DAY OF MONTH</li>
 * <li>MONTHS</li>
 * <li>DAY OF WEEK</li>
 * <li>YEARS</li>
 * </ul>
 */
interface CronField extends CronPart {

    CronFieldType getType();

}
