package com.github.sc.scheduler.core.repo;

import com.github.sc.scheduler.core.model.Run;

import java.time.Instant;
import java.util.List;

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
}
