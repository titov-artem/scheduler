package com.github.scheduler.repo.jdbc;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import java.time.LocalDateTime;

import static com.github.scheduler.repo.jdbc.jooq.JooqTable.table;
import static org.jooq.impl.DSL.field;

public class SQLSchema {
    static final Table<Record> TASK_TABLE = table("sch_task", field("task_id", String.class));
    static final Table<Record> TASK_ARGS_TABLE = table("sch_task_args");
    static final Table<Record> TIMETABLE_TABLE = table("sch_timetable", field("task_id", String.class));
    static final Table<Record> RUNS_TABLE = table("sch_run", field("run_id", Long.class));
    static final Table<Record> HISTORY_RUNS_TABLE = table("sch_history_run", field("run_id", Long.class));

    /* Run fields */
    static final Field<Long> RUN_ID = field("run_id", Long.class);
    static final Field<String> STATUS = field("status", String.class);
    static final Field<LocalDateTime> QUEUED_TIME = field("queued_time", LocalDateTime.class);
    static final Field<String> HOST = field("host", String.class);
    static final Field<LocalDateTime> ACQUIRED_TIME = field("acquired_time", LocalDateTime.class);
    static final Field<LocalDateTime> START_TIME = field("start_time", LocalDateTime.class);
    static final Field<LocalDateTime> PING_TIME = field("ping_time", LocalDateTime.class);
    static final Field<LocalDateTime> END_TIME = field("end_time", LocalDateTime.class);
    static final Field<String> MESSAGE = field("message", String.class);

    /* Engine requirements */
    static final Field<String> SERVICE = field("service", String.class);
    static final Field<String> EXECUTOR = field("executor", String.class);
    static final Field<Integer> WEIGHT = field("weight", Integer.class);

    /* Task params */
    static final Field<String> VALUE = field("value", String.class);

    /* Scheduling params */
    static final Field<String> TYPE = field("type", String.class);
    static final Field<String> PARAM = field("param", String.class);
    static final Field<LocalDateTime> LAST_RUN_TIME = field("last_run_time", LocalDateTime.class);
    static final Field<String> STARTING_HOST = field("starting_host", String.class);
    static final Field<LocalDateTime> STARTING_TIME = field("starting_time", LocalDateTime.class);

    /* Common fields */
    // task, run, scheduling params, task params
    static final Field<String> TASK_ID = field("task_id", String.class);
    // Task, task params
    static final Field<String> NAME = field("name", String.class);
    // Run, scheduling params
    static final Field<Integer> VERSION = field("version", Integer.class);
}
