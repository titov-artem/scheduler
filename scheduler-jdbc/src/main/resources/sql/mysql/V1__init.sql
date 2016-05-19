CREATE TABLE sch_task (
  task_id  VARCHAR(128) NOT NULL PRIMARY KEY,
  name     VARCHAR(128) DEFAULT NULL,
  service  VARCHAR(128) NOT NULL,
  executor VARCHAR(512) NOT NULL,
  weight   INTEGER      NOT NULL
)
  ENGINE = INNODB;

CREATE TABLE sch_task_params (
  task_id VARCHAR(128) NOT NULL,
  name    VARCHAR(128) NOT NULL,
  value   TEXT         NOT NULL,
  INDEX (task_id)
)
  ENGINE = INNODB;

CREATE TABLE sch_timetable (
  task_id       VARCHAR(128) NOT NULL PRIMARY KEY,
  type          VARCHAR(32)  NOT NULL,
  param         VARCHAR(128)          DEFAULT NULL,
  last_run_time DATETIME              DEFAULT NULL,
  starting_host VARCHAR(128)          DEFAULT NULL,
  starting_time DATETIME              DEFAULT NULL,
  version       INTEGER      NOT NULL DEFAULT 0
)
  ENGINE = INNODB;

CREATE TABLE sch_run (
  run_id        BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
  task_id       VARCHAR(128) NOT NULL,
  service       VARCHAR(128) NOT NULL,
  executor      VARCHAR(512) NOT NULL,
  weight        INTEGER      NOT NULL,
  status        VARCHAR(32)  NOT NULL,
  queued_time   DATETIME     NOT NULL,
  host          VARCHAR(128)                      DEFAULT NULL,
  acquired_time DATETIME                          DEFAULT NULL,
  start_time    DATETIME                          DEFAULT NULL,
  ping_time     DATETIME                          DEFAULT NULL,
  end_time      DATETIME                          DEFAULT NULL,
  message       VARCHAR(512)                      DEFAULT NULL,
  version       INTEGER      NOT NULL             DEFAULT 0,
  INDEX (task_id)
)
  ENGINE = INNODB;

CREATE TABLE sch_history_run (
  run_id        BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
  task_id       VARCHAR(128) NOT NULL,
  service       VARCHAR(128) NOT NULL,
  executor      VARCHAR(512) NOT NULL,
  weight        INTEGER      NOT NULL,
  status        VARCHAR(32)  NOT NULL,
  queued_time   DATETIME     NOT NULL,
  host          VARCHAR(128)                      DEFAULT NULL,
  acquired_time DATETIME                          DEFAULT NULL,
  start_time    DATETIME                          DEFAULT NULL,
  end_time      DATETIME                          DEFAULT NULL,
  message       VARCHAR(512)                      DEFAULT NULL,
  version       INTEGER      NOT NULL             DEFAULT 0,
  INDEX (task_id)
)
  ENGINE = INNODB;
