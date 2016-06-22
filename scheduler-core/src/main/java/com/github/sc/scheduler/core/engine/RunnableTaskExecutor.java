package com.github.sc.scheduler.core.engine;

/**
 * Executes passed {@link Runnable}
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class RunnableTaskExecutor implements TaskExecutor {

    private final Runnable action;

    public RunnableTaskExecutor(Runnable action) {
        this.action = action;
    }

    @Override
    public void run(RunContext context) {
        action.run();
    }
}
