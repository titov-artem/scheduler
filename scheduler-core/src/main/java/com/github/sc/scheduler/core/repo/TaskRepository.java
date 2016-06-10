package com.github.sc.scheduler.core.repo;

import com.github.sc.scheduler.core.model.Task;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Store scheduler internal tasks
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public interface TaskRepository {

    List<Task> getAll();

    Optional<Task> get(String taskId);

    void save(Task task);

    void remove(String taskId);

    /**
     * Try update task. Update will be performed if and only if task version is
     * equals to repository version.
     *
     * @param task task to update
     * @return <ul>
     * <li>updated object if version matches</li>
     * <li>actual object if version not matches</li>
     * <li>{@code null} if there are no any scheduling params for such task</li>
     * </ul>
     */
    @Nullable
    Task tryUpdate(Task task);


    /**
     * Update last run time and not touch version if and only if repository last run time is less
     * then specified one
     *
     * @param taskId      task to update
     * @param lastRunTime new last run time
     */
    void tryUpdateLastRunTime(String taskId, Instant lastRunTime);

}
