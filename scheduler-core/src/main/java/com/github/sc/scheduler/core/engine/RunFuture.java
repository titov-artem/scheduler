package com.github.sc.scheduler.core.engine;

import java.util.concurrent.Future;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
final class RunFuture {
    final long runId;
    final Future<?> future;

    RunFuture(long runId, Future<?> future) {
        this.runId = runId;
        this.future = future;
    }
}
