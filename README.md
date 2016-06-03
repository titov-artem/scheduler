#Cron

Added cron parsing submodule.
Cron support fields:
 * Seconds
 * Minutes
 * Hours
 * Day of month
 * Month
 * Day of week
 * Year (optional)

Supported expressions:

| A | B |
| - | - |
| c | c |


# Scheduler

Main idea is to run tasks on cluster by persistence timetable, which can be edited
in runtime without restarting.