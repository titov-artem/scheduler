package com.github.sc.scheduler.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
