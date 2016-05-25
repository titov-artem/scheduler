package com.github.scheduler.repo;

import com.github.scheduler.model.Run;

import java.time.Instant;
import java.util.List;

public interface ActiveRunsRepository extends RunsRepository {

    /**
     * @return maybe empty list of runs in order of queuing
     */
    @Override
    List<Run> getRuns();

    void ping(long runId, Instant pingTime);
}
