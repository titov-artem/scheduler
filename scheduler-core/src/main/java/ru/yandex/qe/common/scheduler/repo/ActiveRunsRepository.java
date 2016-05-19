package ru.yandex.qe.common.scheduler.repo;

import ru.yandex.qe.common.scheduler.model.Run;

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
