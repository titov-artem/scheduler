package com.github.sc.scheduler.utils;

import org.springframework.beans.factory.annotation.Required;

public final class FromPropertySchedulerHostProvider implements SchedulerHostProvider {

    private String host;

    public FromPropertySchedulerHostProvider() {
    }

    public FromPropertySchedulerHostProvider(String host) {
        this.host = host;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Required
    public void setHost(String host) {
        this.host = host;
    }
}
