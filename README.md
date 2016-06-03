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
| Field type | Expression | Meaning |
| -----------|------------|---------|
| Seconds    |      1     | Equal to specified value


# Scheduler

Main idea is to run tasks on cluster by persistence timetable, which can be edited
in runtime without restarting.