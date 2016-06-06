package com.github.sc.scheduler.example;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("classpath:context/application-ctx.xml").start();
    }

    /*
    start transaction;
    insert into sch_task (task_id, name, service, executor, weight) values ('1','Simple periodic task', 'main', 'com.github.sc.scheduler.example.SleepTaskExecutor', 1);
    insert into sch_task_args (task_id, name, value) values ('1', 'time', '1000');
    insert into sch_timetable (task_id, type, param) values ('1', 'PERIOD', '1');
    commit;
     */
}
