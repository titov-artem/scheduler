package com.github.sc.cron;

import com.google.common.base.Preconditions;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.toList;

class CronFieldImpl implements CronField {

    private final List<CronPart> cronParts;
    private final CronFieldType type;

    private CronFieldImpl(List<CronPart> cronParts, CronFieldType type) {
        this.cronParts = cronParts;
        this.type = type;
    }

    @Override
    public boolean match(ZonedDateTime dateTime) {
        for (CronPart part : cronParts) {
            if (part.match(dateTime)) return true;
        }
        return false;
    }

    @Override
    public CronFieldType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%s %s", type, cronParts);
    }

    public static CronFieldImpl of(String field,
                                   CronFieldType type,
                                   BiFunction<String, CronFieldType, CronPart> fieldsPartParser) {
        Preconditions.checkArgument(field != null && !field.trim().isEmpty(),
                "Empty cron field for field %s is forbidden", type.name());
        List<CronPart> parts = Arrays.asList(field.split(",")).stream()
                .map(p -> fieldsPartParser.apply(p, type))
                .collect(toList());
        return new CronFieldImpl(parts, type);
    }

}
