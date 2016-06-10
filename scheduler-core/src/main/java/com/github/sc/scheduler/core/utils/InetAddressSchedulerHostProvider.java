package com.github.sc.scheduler.core.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Provider, which get local address and resolve it
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public final class InetAddressSchedulerHostProvider implements SchedulerHostProvider {
    @Override
    public String getHost() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to determine local host name", e);
        }
    }
}
