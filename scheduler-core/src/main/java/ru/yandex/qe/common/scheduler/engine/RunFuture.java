package ru.yandex.qe.common.scheduler.engine;

import java.util.concurrent.Future;

final class RunFuture {
    final long runId;
    final Future<?> future;

    RunFuture(long runId, Future<?> future) {
        this.runId = runId;
        this.future = future;
    }
}
