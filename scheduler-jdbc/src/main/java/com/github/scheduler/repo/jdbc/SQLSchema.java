package com.github.scheduler.repo.jdbc;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class SQLSchema {
    static final Table<Record> TASK_TABLE = table("sch_task");
    static final Table<Record> TASK_PARAMS_TABLE = table("sch_task_params");
    static final Table<Record> TIMETABLE_TABLE = table("sch_timetable");
    static final Table<Record> RUNS_TABLE = table("sch_run");
    static final Table<Record> HISTORY_RUNS_TABLE = table("sch_history_run");

    /* Run fields */
    static final Field<Object> RUN_ID = field("run_id");
    static final Field<Object> STATUS = field("status");
    static final Field<Object> QUEUED_TIME = field("queued_time");
    static final Field<Object> HOST = field("host");
    static final Field<Object> ACQUIRED_TIME = field("acquired_time");
    static final Field<Object> START_TIME = field("start_time");
    static final Field<Object> PING_TIME = field("ping_time");
    static final Field<Object> END_TIME = field("end_time");
    static final Field<Object> MESSAGE = field("message");

    /* Engine requirements */
    static final Field<Object> SERVICE = field("service");
    static final Field<Object> EXECUTOR = field("executor");
    static final Field<Object> WEIGHT = field("weight");

    /* Task params */
    static final Field<Object> VALUE = field("value");

    /* Scheduling params */
    static final Field<Object> TYPE = field("type");
    static final Field<Object> PARAM = field("param");
    static final Field<Object> LAST_RUN_TIME = field("last_run_time");
    static final Field<Object> STARTING_HOST = field("starting_host");
    static final Field<Object> STARTING_TIME = field("starting_time");

    /* Common fields */
    // task, run, scheduling params, task params
    static final Field<Object> TASK_ID = field("task_id");
    // Task, task params
    static final Field<Object> NAME = field("name");
    // Run, scheduling params
    static final Field<Object> VERSION = field("version");
}
