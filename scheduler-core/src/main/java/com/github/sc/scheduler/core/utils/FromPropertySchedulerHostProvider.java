package com.github.sc.scheduler.core.utils;

import org.springframework.beans.factory.annotation.Required;

/**
 * Return host name, that was passed via property.
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
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
