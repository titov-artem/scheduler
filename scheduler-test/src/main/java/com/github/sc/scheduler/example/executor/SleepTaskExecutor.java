package com.github.sc.scheduler.example.executor;

import com.github.sc.scheduler.core.engine.RunContext;
import com.github.sc.scheduler.core.engine.TaskExecutor;
import com.github.sc.scheduler.core.model.TaskArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple task executor, which sleep for time specified in task args {@code time} parameter
 * and fails if no such parameter presented
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class SleepTaskExecutor implements TaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(SleepTaskExecutor.class);

    @Override
    public void run(RunContext context) {
        String taskId = context.getRun().getTaskId();
        TaskArgs taskArgs = context.getTaskArgs();
        int sleepTime = Integer.parseInt(taskArgs.get("time"));
        log.info("Executing task {} for {} ms", taskId, sleepTime);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Executing task {} done", taskId);
    }
}
