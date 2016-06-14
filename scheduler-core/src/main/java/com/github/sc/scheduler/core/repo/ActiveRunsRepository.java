package com.github.sc.scheduler.core.repo;

import com.github.sc.scheduler.core.model.Run;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Store runs that are currently running or pending. When task is starting, new run will be
 * added here
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public interface ActiveRunsRepository extends RunsRepository {

    /**
     * @return maybe empty list of runs in order of queuing
     */
    @Override
    List<Run> getAll();

    void ping(long runId, Instant pingTime);

    List<Run> create(Run run, int count, int concurrencyLevel);

    /**
     * Recreate run. Try to update specified run to store it state to repository
     * and then create a new one, use specified one as prototype. Perform this operation
     * atomically. Don't create new run if
     * <ul>
     * <li>Not this thread update specified run</li>
     * <li>New run violate specified concurrency level</li>
     * </ul>
     *
     * @param prototype        run to update and to use as prototype for new one
     * @param concurrencyLevel concurrency level for task of the run
     * @return empty if no run was created on created run
     */
    Optional<Run> recreate(Run prototype, int concurrencyLevel);
}
