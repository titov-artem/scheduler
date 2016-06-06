package com.github.sc.scheduler.core.engine;

import java.util.concurrent.Future;

final class RunFuture {
    final long runId;
    final Future<?> future;

    RunFuture(long runId, Future<?> future) {
        this.runId = runId;
        this.future = future;
    }
}
