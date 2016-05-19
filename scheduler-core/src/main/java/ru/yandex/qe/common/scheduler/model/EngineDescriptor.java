package ru.yandex.qe.common.scheduler.model;

import javax.annotation.Nonnull;

/**
 * Describe engine host
 */
public interface EngineDescriptor {

    int getMaxCapacity();

    int getFreeCapacity();

    @Nonnull
    String getService();

    @Nonnull
    String getHost();
}
