package com.github.sc.scheduler.core.annotations;

import org.springframework.beans.factory.annotation.Required;

import javax.annotation.PostConstruct;

public class SchedulerTaskAnnotationProcessor {

    private String rootPackage;

    @PostConstruct
    public void init() {

    }

    /* Setters */
    @Required
    public void setRootPackage(String rootPackage) {
        this.rootPackage = rootPackage;
    }
}
