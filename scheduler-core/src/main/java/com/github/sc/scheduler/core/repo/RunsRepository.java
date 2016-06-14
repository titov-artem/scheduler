package com.github.sc.scheduler.core.repo;

import com.github.sc.scheduler.core.model.Run;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Abstract repository for storing runs
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public interface RunsRepository {

    Optional<Run> get(long runId);

    /**
     * @return maybe empty list of all runs without any ordering guaranties
     */
    List<Run> getAll();

    /**
     * Return all runs for specified task without any ordering guaranties
     *
     * @param taskId task id
     * @return maybe empty list of runs
     */
    List<Run> get(String taskId);

    /**
     * Return multi map from task id to it's runs without any ordering guaranties
     *
     * @param taskIds task ids
     * @return maybe empty multi map from task id to its runs
     */
    Multimap<String, Run> getRuns(Collection<String> taskIds);

    /**
     * Create specified run and return run with {@code runId} setted
     *
     * @param run run to create
     * @return created run with run id
     */
    Run create(Run run);

    /**
     * Create each specified run if it is not exist in the repo. For existence checking run id is used.
     * If any run in specified list has {@link Run#FAKE_RUN_ID} then it will be skipped
     *
     * @param runs runs to create
     */
    void createIfNotExists(Collection<Run> runs);

    /**
     * Trying to update run into repository. If mod token match than update will be successful and
     * updated entity will be returned, otherwise nothing will be returned
     *
     * @param run run to updated
     * @return updated entry or empty
     */
    Optional<Run> tryUpdate(Run run);

    /**
     * Remove all specified runs basing on run id. If any run willn't be found, nothing will happened
     *
     * @param runs runs to remove
     */
    void remove(Collection<Run> runs);
}
