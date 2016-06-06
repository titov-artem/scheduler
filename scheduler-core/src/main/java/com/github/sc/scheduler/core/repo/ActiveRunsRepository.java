package com.github.sc.scheduler.core.repo;

import com.github.sc.scheduler.core.model.Run;

import java.time.Instant;
import java.util.List;

public interface ActiveRunsRepository extends RunsRepository {

    /**
     * @return maybe empty list of runs in order of queuing
     */
    @Override
    List<Run> getAll();

    void ping(long runId, Instant pingTime);
}
