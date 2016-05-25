package com.github.scheduler.repo.jdbc.util;

import java.sql.Timestamp;
import java.time.Instant;

public final class SqlDateUtils {

    private SqlDateUtils() {
    }

    public static Timestamp toTimestamp(Instant time) {
        return time == null ? null : Timestamp.from(Instant.from(time));
    }

    public static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }

}
