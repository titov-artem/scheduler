package ru.yandex.qe.common.scheduler.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TaskArgsImpl implements TaskArgs {

    private final ListMultimap<String, String> params;

    private TaskArgsImpl(ListMultimap<String, String> params) {
        this.params = params;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Collection<String> getNames() {
        return params.keySet();
    }

    @Nullable
    @Override
    public String get(String name) {
        List<String> values = params.get(name);
        return values.isEmpty() ? null : values.get(0);
    }

    @Nonnull
    @Override
    public List<String> getAll(String name) {
        return params.get(name);
    }

    @Nullable
    @Override
    public <T> T get(String name, Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskArgsImpl that = (TaskArgsImpl) o;
        return Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params);
    }

    public static class Builder {
        private ListMultimap<String, String> params = ArrayListMultimap.create();

        public Builder put(String name, String value) {
            params.replaceValues(name, Collections.singleton(value));
            return this;
        }

        public Builder append(String name, String value) {
            params.put(name, value);
            return this;
        }

        public boolean isEmpty() {
            return params.isEmpty();
        }

        public TaskArgs build() {
            return new TaskArgsImpl(params);
        }
    }
}
