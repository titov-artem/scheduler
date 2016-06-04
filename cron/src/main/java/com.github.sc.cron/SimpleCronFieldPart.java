package com.github.sc.cron;

import java.time.ZonedDateTime;
import java.util.regex.Pattern;

import static com.github.sc.cron.Utils.checkArgument;
import static com.github.sc.cron.Utils.validateValues;

/**
 * Storing data about cron field part. Applicable only to simple fields
 */
final class SimpleCronFieldPart implements CronPart {

    private static final String INC_MARK = "/";
    private static final Pattern SIMPLE_RANGE = Pattern.compile("[0-9]+\\-[0-9]+");
    private static final Pattern SINGLE_VALUE = Pattern.compile("[0-9]+");

    private final boolean any;
    private final Range range;
    private final Inc inc;
    private final CronFieldType type;

    private SimpleCronFieldPart(boolean any,
                                Range range,
                                Inc inc,
                                CronFieldType type) {
        this.any = any;
        this.range = range;
        this.inc = inc;
        this.type = type;
    }

    @Override
    public boolean match(ZonedDateTime dateTime) {
        if (any) return true;
        int value = type.getValue(dateTime);
        if (range != null) {
            return value >= range.from && value <= range.to;
        }
        if (inc != null) {
            if (inc.start > value) return false;
            return (value - inc.start) % inc.inc == 0;
        }
        return false;
    }

    private static SimpleCronFieldPart any(CronFieldType type) {
        return new SimpleCronFieldPart(true, null, null, type);
    }

    private static SimpleCronFieldPart range(Range range, CronFieldType type) {
        return new SimpleCronFieldPart(false, range, null, type);
    }

    private static SimpleCronFieldPart inc(Inc inc, CronFieldType type) {
        return new SimpleCronFieldPart(false, null, inc, type);
    }

    @Override
    public String toString() {
        return String.format("%s%s%s",
                any ? "*" : "",
                range != null ? range.toString() : "",
                inc != null ? inc.toString() : ""
        );
    }

    static SimpleCronFieldPart of(String part, CronFieldType type) {
        checkArgument(part != null && !part.trim().isEmpty(),
                "Empty cron field parts for field %s is forbidden", type.name());
        // * or ?
        if ("*".equals(part) || "?".equals(part)) {
            return any(type);
        }
        // 0/5 or /5
        if (part.contains(INC_MARK)) {
            String[] split = part.split(INC_MARK);
            final int start;
            final int inc;
            if (split[0].isEmpty()) {
                start = 0;
                inc = Integer.parseInt(split[1]);
            } else {
                start = Integer.parseInt(split[0]);
                inc = Integer.parseInt(split[1]);
            }
            if (start != 0)
                validateValues(type, start);
            checkArgument(inc > 0 && inc <= type.getMaxValue(), "Increment must be greater than 0 and less than or equal to %s in field %s", type.getMaxValue(), type);
            return inc(new Inc(start, inc), type);
        }
        // 1-12
        if (SIMPLE_RANGE.matcher(part).matches()) {
            String[] split = part.split("-");
            final int from = Integer.parseInt(split[0]);
            final int to = Integer.parseInt(split[1]);
            validateValues(type, from, to);
            checkArgument(from <= to, "From must be less than to in range in field %s", type.name());
            return range(new Range(from, to), type);
        }
        // 10
        if (SINGLE_VALUE.matcher(part).matches()) {
            int value = Integer.parseInt(part);
            validateValues(type, value);
            return range(new Range(value, value), type);
        }
        throw new IllegalArgumentException(String.format("Wrong format for part %s in field %s",
                part, type.name()));
    }

    private static final class Range {
        final int from;
        final int to;

        private Range(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return String.format("%d-%d", from, to);
        }
    }

    private static final class Inc {
        final int start;
        final int inc;

        private Inc(int start, int inc) {
            this.start = start;
            this.inc = inc;
        }

        @Override
        public String toString() {
            return String.format("%d/%d", start, inc);
        }
    }
}
