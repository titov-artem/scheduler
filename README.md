#Cron

Cron parsing submodule implemented on pure java 8 with Java Time API.

Support cron fields:
 * Seconds (0-59)
 * Minutes (0-59)
 * Hours (0-23)
 * Day of month (1-31)
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

Current version support only small clusters because each instance need at
least one connection to scheduler database. So with big cluster there will be problems
with db access.

Scheduler maintains persistence timetable with all scheduled `Task`'s. When task's time to
start comes, scheduler create `Run` for this task and put it in the run's queue, from which
engines gets it and perform.

###Main interface entities

#####SchedulingParams

This class represent task in scheduler for API user. Scheduling params contains
all information about how and when task should be started. Also it has task name:

* `name` - task name
* `engineRequirements` - requirements for engine, that will execute this task. Full description of this object will be
provided below
* `type` - scheduling type, one of `CRON`, `ONCE` or `PERIOD`. `CRON` means, that task should be started by cron
expression, `ONCE` - task should be started only once right now, `PERIOD` - task should be started right now and
then each time after specified period. Cron expression and period must be specified in `param`
* `param` - parameter for scheduling type. For `CRON` must contains cron expression, for `PERIOD` must contains period
in seconds
* `concurrencyLevel` - how much runs of this task can be executed concurrently on the cluster
* `isRestartOnFail` - restart task if it fails
* `isRestartOnReboot` - restart task if engine, that run it was rebooted

Last two parameters are coordinated with concurrency level

#####EngineRequirements

This class describe which criteria must met the engine, that will execute task. You can specify
this requirements:

* `weight` - each task in scheduler has it's own weight, and each engine has capacity, which determine
how "big" tasks they can executes. When engine starts new run, it checks that it's free capacity is greater, than run's
weight. If so, then engine allocates part of it's capacity for selected run, if not, run won't be started.
* `service` - engines in cluster can be divided in different services, to support working of multiple
subclusters on same database. So you can specify which service will be able to execute your task.
* `executor` - executor is class, that perform task operations. The main difference of this requirement from
others is that engine checks it after run selection, and if engine doesn't met it, task will fail.

#####Run

`Run` is the single execution of one task in scheduler. After you create a task and it's start time comes, scheduler
determines it and create a `Run` object and put it into special execution queue. Scheduler will copy all params from
`SchedulingParams` to `Run` while creation for better debugging, history and independence from task modifications.
Then scheduler engine will peek `Run` and acquire it for special host, where it will be executed. After execution
`Run`'s status will be stored and `Run` will be moved to history.

So `Run` has the biggest subset of fields from `SchedulingParams` and also has it's own status,
and different event times: queued time, acquire time, start time, ping time, end time. Here is one interesting time:
ping time. During execution, engine, that acquire run, update it's ping time every k seconds to show for other cluster,
that it doesn't hanged. So if ping time became idle for too long, `Run` will be restarted, basing on `isRestartOnReboot`
property. During restart old run will be marked as `HANGED` and new one will be created.

#####TaskArgs

Some tasks need additional args during execution. You can specify them with `TaskArgs`.

###Usage

There are different ways, how you can schedule the task. Scheduler has programmatic API and HTTP API
(optional separate module). Also it is possible to schedule task via database directly. But this way has no
compatibility guarantees between different versions!

#####Programmatic API

To schedule task programmatically you need `TaskService` instance. It provides only
two methods:

* `SchedulingParams createTask(SchedulingParams params, Optional<TaskArgs> args)` - allow to schedule task. You can use
`SchedulingParamsImp.Builder` and `TaskArgsImpl.Builder` to create corresponding necessary
objects.

*  `void removeTask(String taskId)` - allow to remove task from scheduler

Also you can get different information about currently existing tasks and runs via repositories:
* `TimetableRepository` - store existing `SchedulingParams`
* `TaskArgsRepository` - store `TaskArgs`
* `ActiveRunsRepository` - store `Run`s, that are waiting/running or just finished their executions
* `HistoryRunsRepository` - store `Run`'s history

#####HTTP API

Documentation under construction
See `com.github.sc.scheduler.http.TaskController`

###Configuration

Scheduler can be configurated programmatically or by Spring.

For spring configuration `context/scheduler-core-ctx.xml` can be used. To configure repositories you should select one,
that is acceptable for your database:
* `context/scheduler-mysql-jdbc-repositories-ctx.xml`
* `context/scheduler-postgresql-jdbc-repositories-ctx.xml`

Database scheme can be found in:
* `mysql/V1__init.sql`
* `postgresql/V1__init.sql`

With such configurations one `RunMaster`, that schedule tasks and create `Run`s, and one `Engine`, that
execute `Run`s, will be started

Also you need specify these properties:
* `com.github.sc.scheduler.host` - this scheduler instance host
* `com.github.sc.scheduler.master.periodSeconds` - `RunMaster` schedule period. `RunMaster` will perform its operations
basing on this period
* `com.github.sc.scheduler.engine.periodSeconds` - engine operations period
* `com.github.sc.scheduler.engine.capacity` - engine capacity
* `com.github.sc.scheduler.engine.threads` - number of threads on engine
* `com.github.sc.scheduler.engine.service` - engine's service