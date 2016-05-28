package com.github.scheduler.repo.jdbc.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class SqlDateUtils {

    private SqlDateUtils() {
    }

    public static LocalDateTime toTimestamp(Instant time) {
        return time == null ? null : LocalDateTime.ofInstant(time, ZoneId.systemDefault());
    }

    public static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }

}
