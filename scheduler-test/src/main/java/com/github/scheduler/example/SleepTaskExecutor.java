package com.github.scheduler.example;

import com.github.scheduler.engine.RunContext;
import com.github.scheduler.model.TaskArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SleepTaskExecutor implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SleepTaskExecutor.class);

    @Override
    public void run() {
        RunContext context = RunContext.get();
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
