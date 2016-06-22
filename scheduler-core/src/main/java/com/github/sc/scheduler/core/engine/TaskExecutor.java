package com.github.sc.scheduler.core.engine;

/**
 * Executor for task in scheduler
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public interface TaskExecutor {

    void run(RunContext context);
}
