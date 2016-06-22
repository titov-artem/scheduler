package com.github.sc.scheduler.core.engine;

import com.github.sc.scheduler.core.model.Run;
import com.github.sc.scheduler.core.model.TaskArgs;

/**
 * Contains task args and all inforamtion about task's run
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class RunContext {

    private static final ThreadLocal<RunContext> instance = new ThreadLocal<>();

    private final Run run;
    private final TaskArgs taskArgs;
    private String message;

    RunContext(Run run, TaskArgs taskArgs) {
        this.run = run;
        this.taskArgs = taskArgs;
    }

    public static RunContext get() {
        return instance.get();
    }

    static void set(RunContext context) {
        instance.set(context);
    }

    static void clear() {
        instance.remove();
    }

    public Run getRun() {
        return run;
    }

    public TaskArgs getTaskArgs() {
        return taskArgs;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Set output message, which will be stored in run history as comment to run result
     *
     * @param message message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
