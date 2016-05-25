package com.github.scheduler.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Params that will be passed to task when it will be executed
 */
public interface TaskArgs {

    Collection<String> getNames();

    @Nullable
    String get(String name);

    @Nonnull
    List<String> getAll(String name);

    @Nullable
    <T> T get(String name, Class<T> clazz);

}
