package com.github.sc.scheduler.example;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;

public class SchedulerObjectMapper extends ObjectMapper {

    public SchedulerObjectMapper() {
        setup();
    }

    public SchedulerObjectMapper(JsonFactory jf) {
        super(jf);
        setup();
    }

    public SchedulerObjectMapper(ObjectMapper src) {
        super(src);
        setup();
    }

    public SchedulerObjectMapper(JsonFactory jf, DefaultSerializerProvider sp, DefaultDeserializationContext dc) {
        super(jf, sp, dc);
        setup();
    }

    private void setup() {
        this
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .findAndRegisterModules();
    }
}
