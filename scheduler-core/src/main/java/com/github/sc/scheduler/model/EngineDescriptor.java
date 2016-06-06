package com.github.sc.scheduler.model;

import javax.annotation.Nonnull;

/**
 * Describe engine host
 */
public interface EngineDescriptor {

    int getMaxCapacity();

    int getFreeCapacity();

    int getMaxThreads();

    int getFreeThreads();

    @Nonnull
    String getService();

    @Nonnull
    String getHost();
}
