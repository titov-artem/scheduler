package com.github.sc.cron;

import com.google.common.base.Preconditions;

class Utils {
    static void validateValues(CronFieldType type, int... values) {
        for (int value : values) {
            Preconditions.checkArgument(value >= type.getMinValue(),
                    "Value %s must be greater than %s for type %s",
                    value, type.getMinValue(), type.name());
            Preconditions.checkArgument(value <= type.getMaxValue(),
                    "Value %s must be less than %s for type %s",
                    value, type.getMaxValue(), type.name());
        }
    }

    static void validateOffset(CronFieldType type, int offset) {
        Preconditions.checkArgument(offset >= 0 && offset < type.getMaxValue(),
                "Offset must be positive and less max field value in field %s",
                type.name());
    }
}
