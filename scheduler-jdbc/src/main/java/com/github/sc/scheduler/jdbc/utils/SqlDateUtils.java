package com.github.sc.scheduler.jdbc.utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
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
