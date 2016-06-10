package com.github.sc.scheduler.core.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Args that will be passed to task when it will be executed
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public interface TaskArgs {

    String getTaskId();

    Collection<String> getNames();

    @Nullable
    String get(String name);

    @Nonnull
    List<String> getAll(String name);

    @Nullable
    <T> T get(String name, Class<T> clazz);

}
