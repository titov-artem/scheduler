package com.github.sc.scheduler.core.repo;

import com.github.sc.scheduler.core.model.SchedulingParams;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Store scheduler params, that are main entities in scheduler
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public interface TimetableRepository {

    List<SchedulingParams> getAll();

    List<SchedulingParams> getAll(Collection<String> taskIds);

    Optional<SchedulingParams> get(String taskId);

    SchedulingParams save(SchedulingParams params);

    void removeTask(String taskId);

    void removeTasks(Collection<String> taskIds);
}
