package com.github.sc.scheduler.core.repo;

import com.github.sc.scheduler.core.model.SchedulingParams;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TimetableRepository {

    List<SchedulingParams> getAll();

    List<SchedulingParams> getAll(Collection<String> taskIds);

    Optional<SchedulingParams> get(String taskId);

    SchedulingParams save(SchedulingParams params);

    void removeTask(String taskId);

    void removeTasks(Collection<String> taskIds);
}
