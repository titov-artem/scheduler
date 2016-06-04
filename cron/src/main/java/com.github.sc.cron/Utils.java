package com.github.sc.cron;

import java.util.function.Supplier;

final class Utils {
    private Utils() {
    }

    static void validateValues(CronFieldType type, int... values) {
        for (int value : values) {
            checkArgument(value >= type.getMinValue(),
                    "Value %s must be greater than %s for type %s",
                    value, type.getMinValue(), type.name());
            checkArgument(value <= type.getMaxValue(),
                    "Value %s must be less than %s for type %s",
                    value, type.getMaxValue(), type.name());
        }
    }

    static void validateOffset(CronFieldType type, int offset) {
        checkArgument(offset >= 0 && offset < type.getMaxValue(),
                "Offset must be positive and less max field value in field %s",
                type.name());
    }

    static void checkArgument(boolean condition, String message, Object... args) {
        checkCondition(condition, () -> {
            throw new IllegalArgumentException(String.format(message, args));
        });
    }

    static void checkState(boolean condition, String message, Object... args) {
        checkCondition(condition, () -> {
            throw new IllegalStateException(String.format(message, args));
        });
    }

    private static void checkCondition(boolean condition,
                                       Supplier<RuntimeException> exceptionProvider) {
        if (!condition) {
            throw exceptionProvider.get();
        }
    }
}
