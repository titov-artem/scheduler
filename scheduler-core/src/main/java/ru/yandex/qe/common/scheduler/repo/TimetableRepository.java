package ru.yandex.qe.common.scheduler.repo;

import ru.yandex.qe.common.scheduler.model.SchedulingParams;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TimetableRepository {

    List<SchedulingParams> getTasks();

    Optional<SchedulingParams> getTask(String taskId);

    void save(SchedulingParams params);

    /**
     * Try update params. Update will be performed if and only if task version is equals to repository version.
     * Invocation will return updated object if version matches, actual object otherwise, and null if there are
     * no any scheduling params for such task in the repository
     *
     * @param task task to update
     * @return
     */
    @Nullable
    SchedulingParams tryUpdate(SchedulingParams task);

    /**
     * Update last run time and not touch version if and only if repository last run time is less
     * then specified one
     *
     * @param taskId      task to update
     * @param lastRunTime new last run time
     */
    void tryUpdateLastRunTime(String taskId, Instant lastRunTime);

    void removeTask(String taskId);

    void removeTasks(Collection<String> taskIds);

}
