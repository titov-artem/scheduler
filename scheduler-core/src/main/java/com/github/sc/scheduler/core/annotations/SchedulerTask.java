package com.github.sc.scheduler.core.annotations;

import com.github.sc.scheduler.core.model.SchedulingType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SchedulerTask {

    /**
     * Unique job name, that will be used as id
     */
    String name();

    /**
     * Declare scheduling type for this task
     */
    SchedulingType type();

    /**
     * Declare params for scheduling type
     */
    String params() default "";

    String service();

    int weight();

    String executor();
}
