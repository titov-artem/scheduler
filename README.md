#Cron

Cron parsing submodule implemented on pure java 8 with Java Time API.

Support cron fields:
 * Seconds (0-59)
 * Minutes (0-59)
 * Hours (0-23)
 * Day of month (0-31)
 * Month (1-12, JAN-DEC)
 * Day of week (1-7, MON-SUN)
 * Year (optional)

###Supported expressions:

Supported expression were made maximum compatible with quartz cron expressions.
**Important** difference is in the day of week field: 1 is Monday and 7 is Sunday.

Here is symbols supported for each field with their meanings

####1. Seconds
 * 17 - field equals to 17
 * 1,3,7 - field equals to one of 1, 3 or 7 (**IMPORTANT** no spaces after comma)
 * 10-29 - field greater or equals to 10 and less than or equals to 29
 * 3/5 - field equals to 3, 8, 13, 18, 23 etc 
 * /7 - field equals to 0, 7, 14, 21 etc 

####2. Minutes
 * 17 - field equals to 17
 * 1,3,7 - field equals to one of 1, 3 or 7 (**IMPORTANT** no spaces after comma)
 * 10-29 - field greater or equals to 10 and less than or equals to 29
 * 3/5 - field equals to 3, 8, 13, 18, 23 etc 
 * /7 - field equals to 0, 7, 14, 21 etc 

####3. Hours
 * 17 - field equals to 17
 * 1,3,7 - field equals to one of 1, 3 or 7 (**IMPORTANT** no spaces after comma)
 * 10-23 - field greater or equals to 10 and less than or equals to 23
 * 3/5 - field equals to 3, 8, 13, 18, 23 etc 
 * /7 - field equals to 0, 7, 14, 21 etc 

####4. Day of month
 * 17 - field equals to 17
 * 1,3,7 - field equals to one of 1, 3 or 7 (**IMPORTANT** no spaces after comma)
 * 10-29 - field greater or equals to 10 and less than or equals to 29
 * 3/5 - field equals to 3, 8, 13, 18, 23 etc 
 * /7 - field equals to 7, 14, 21 28 etc
 * L - field equals to last day of the month
 * L-3 - field equals to 3rd day before last day of the month
 * LW - field equals last week day of the month
 * 15W - field equals to nearest week day to 15th day of the month

####5. Month
 * 12 - field equals to 12
 * 1,3,7 - field equals to one of 1, 3 or 7 (**IMPORTANT** no spaces after comma)
 * 10-12 - field greater or equals to 10 and less than or equals to 12
 * 1/3 - field equals to 1, 4, 7, etc 
 * /3 - field equals to 3, 6, 9 etc
 * JAN - field equals to number of specified month name (1)

####6. Day of week
 * 2 - field equals to 2
 * 1,3,7 - field equals to one of 1, 3 or 7 (**IMPORTANT** no spaces after comma)
 * 2-5 - field greater or equals to 2 and less than or equals to 5
 * 1/2 - field equals to 1, 3, 6
 * /2 - field equals to 2, 4, 6
 * MON - field equals to number of specified week day name (1)
 * L - field equals to last day of the week (Sunday)
 * L-3 - field equals to 3rd day before last day of the week
 * 3L-1 - field equals to 1st day before last Wednesday (3 means Wednesday) of the month
 * W - field is week day
 * 2#5 - field equals to 2nd Friday (5 means Friday) of the month

####7. Years
 * 17 - field equals to 17
 * 1,3,7 - field equals to one of 1, 3 or 7 (**IMPORTANT** no spaces after comma)
 * 10-29 - field greater or equals to 10 and less than or equals to 29
 * 3/5 - field equals to 3, 8, 13, 18, 23 etc
 * /7 - field equals to **0**, 7, 14, 21 etc

Main interface `CronExpression` provides two methods:
 * `boolean match(ZonedDateTime)` - return does specified time match expression or not
 * `Optional<ZonedDateTime> nextFireAfter(ZonedDateTime)` - return next time that strictly greater specified one that match cron expression. This method trying to find next time in interval from specified time plus 4 years. So it can miss fire on 29 February in each 100th years, but not 400th years, because of leap year rule.

Library provides implementation of this interface `CronExpressionImpl` with two factory methods:
 * `CronExpressionImpl.of(String)` - build cron expression from string representation. Only one from day of month and day of week permitted.
 It was made for imitating quartz cron expressions
 * `CronExpressionImpl.of(String, boolean)` - build cron expression from string representation. Boolean parameter permit to turn on support of both day of week and day of month fields

# Scheduler

Main idea is to run tasks on cluster by persistence timetable, which can be edited
in runtime without restarting.