package com.github.sc.scheduler.http.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.sc.scheduler.model.EngineDescriptor;

import javax.annotation.concurrent.Immutable;

@Immutable
public class EngineDescriptorView {

    private final String host;
    private final String service;
    private final int maxCapacity;
    private final int freeCapacity;
    private final int maxThreads;
    private final int freeThreads;

    public EngineDescriptorView(EngineDescriptor engineDescriptor) {
        host = engineDescriptor.getHost();
        service = engineDescriptor.getService();
        maxCapacity = engineDescriptor.getMaxCapacity();
        freeCapacity = engineDescriptor.getFreeCapacity();
        maxThreads = engineDescriptor.getMaxThreads();
        freeThreads = engineDescriptor.getFreeThreads();
    }

    @JsonGetter
    public String getHost() {
        return host;
    }

    @JsonGetter
    public String getService() {
        return service;
    }

    @JsonGetter
    public int getMaxCapacity() {
        return maxCapacity;
    }

    @JsonGetter
    public int getFreeCapacity() {
        return freeCapacity;
    }

    @JsonGetter
    public int getMaxThreads() {
        return maxThreads;
    }

    @JsonGetter
    public int getFreeThreads() {
        return freeThreads;
    }
}
