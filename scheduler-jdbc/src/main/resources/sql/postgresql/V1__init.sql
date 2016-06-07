CREATE TABLE sch_task (
  task_id       VARCHAR(128) NOT NULL PRIMARY KEY,
  last_run_time TIMESTAMP             DEFAULT NULL,
  starting_host VARCHAR(128)          DEFAULT NULL,
  starting_time TIMESTAMP             DEFAULT NULL,
  version       INTEGER      NOT NULL DEFAULT 1
);

CREATE TABLE sch_task_args (
  task_id VARCHAR(128) NOT NULL,
  name    VARCHAR(128) NOT NULL,
  value   TEXT         NOT NULL
);
CREATE INDEX ON sch_task_args (task_id);

CREATE TABLE sch_timetable (
  task_id  VARCHAR(128) NOT NULL PRIMARY KEY,
  name     VARCHAR(128) DEFAULT NULL,
  service  VARCHAR(128) NOT NULL,
  executor VARCHAR(512) NOT NULL,
  weight   INTEGER      NOT NULL,
  type     VARCHAR(32)  NOT NULL,
  param    VARCHAR(128) DEFAULT NULL
);

CREATE TABLE sch_run (
  run_id        BIGSERIAL    NOT NULL PRIMARY KEY,
  task_id       VARCHAR(128) NOT NULL,
  service       VARCHAR(128) NOT NULL,
  executor      VARCHAR(512) NOT NULL,
  weight        INTEGER      NOT NULL,
  status        VARCHAR(32)  NOT NULL,
  queued_time   TIMESTAMP    NOT NULL,
  host          VARCHAR(128)                      DEFAULT NULL,
  acquired_time TIMESTAMP                         DEFAULT NULL,
  start_time    TIMESTAMP                         DEFAULT NULL,
  ping_time     TIMESTAMP                         DEFAULT NULL,
  end_time      TIMESTAMP                         DEFAULT NULL,
  message       VARCHAR(512)                      DEFAULT NULL,
  version       INTEGER      NOT NULL             DEFAULT 1
);
CREATE INDEX ON sch_run (task_id);

CREATE TABLE sch_history_run (
  run_id        BIGSERIAL    NOT NULL PRIMARY KEY,
  task_id       VARCHAR(128) NOT NULL,
  service       VARCHAR(128) NOT NULL,
  executor      VARCHAR(512) NOT NULL,
  weight        INTEGER      NOT NULL,
  status        VARCHAR(32)  NOT NULL,
  queued_time   TIMESTAMP    NOT NULL,
  host          VARCHAR(128)                      DEFAULT NULL,
  acquired_time TIMESTAMP                         DEFAULT NULL,
  start_time    TIMESTAMP                         DEFAULT NULL,
  ping_time     TIMESTAMP                         DEFAULT NULL,
  end_time      TIMESTAMP                         DEFAULT NULL,
  message       VARCHAR(512)                      DEFAULT NULL,
  version       INTEGER      NOT NULL             DEFAULT 1
);
CREATE INDEX ON sch_history_run (task_id);
