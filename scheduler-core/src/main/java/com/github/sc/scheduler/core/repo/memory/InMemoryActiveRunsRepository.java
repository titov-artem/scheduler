package com.github.sc.scheduler.core.repo.memory;

import com.github.sc.scheduler.core.model.Run;
import com.github.sc.scheduler.core.model.RunImpl;
import com.github.sc.scheduler.core.repo.ActiveRunsRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class InMemoryActiveRunsRepository extends InMemoryRunsRepository implements ActiveRunsRepository {

    @Override
    public List<Run> getAll() {
        List<Run> runs = super.getAll();
        Collections.sort(runs, (o1, o2) -> Long.compare(o1.getRunId(), o2.getRunId()));
        return runs;
    }

    @Override
    public void ping(long runId, Instant pingTime) {
        RunContainer container = data.get(runId);
        if (container == null) return;

        container.lock.lock();
        try {
            container.run.set(RunImpl.builder(container.run.get()).withPingTime(pingTime).build());
        } finally {
            container.lock.unlock();
        }
    }
}
