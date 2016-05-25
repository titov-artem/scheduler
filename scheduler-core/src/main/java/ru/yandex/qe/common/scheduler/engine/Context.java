package ru.yandex.qe.common.scheduler.engine;

import ru.yandex.qe.common.scheduler.model.Run;
import ru.yandex.qe.common.scheduler.model.TaskArgs;

public class Context {

    private static final ThreadLocal<Context> instance = new ThreadLocal<>();

    private final Run run;
    private final TaskArgs taskArgs;
    private String message;

    Context(Run run, TaskArgs taskArgs) {
        this.run = run;
        this.taskArgs = taskArgs;
    }

    public static Context get() {
        return instance.get();
    }

    static void set(Context context) {
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
