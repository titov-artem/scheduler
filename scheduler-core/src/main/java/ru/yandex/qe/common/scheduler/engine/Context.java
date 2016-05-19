package ru.yandex.qe.common.scheduler.engine;

import ru.yandex.qe.common.scheduler.model.Run;
import ru.yandex.qe.common.scheduler.model.TaskParams;

public class Context {

    private static final ThreadLocal<Context> instance = new ThreadLocal<>();

    private final Run run;
    private final TaskParams taskParams;
    private String message;

    Context(Run run, TaskParams taskParams) {
        this.run = run;
        this.taskParams = taskParams;
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

    public TaskParams getTaskParams() {
        return taskParams;
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
