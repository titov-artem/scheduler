package com.github.sc.scheduler;

import com.github.sc.scheduler.core.RunMaster;
import com.github.sc.scheduler.core.engine.Engine;
import com.github.sc.scheduler.core.engine.RunContext;
import com.github.sc.scheduler.core.engine.TaskExecutor;
import com.github.sc.scheduler.core.model.*;
import com.github.sc.scheduler.core.repo.HistoryRunsRepository;
import com.github.sc.scheduler.core.repo.TaskArgsRepository;
import com.github.sc.scheduler.core.repo.TaskRepository;
import com.github.sc.scheduler.core.repo.TimetableRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:mysql-database-ctx.xml",
        "classpath*:context/scheduler-mysql-jdbc-repositories-ctx.xml",
        "classpath*:scheduler-test.xml"
})
//@Transactional
public class JdbcOneInstanceIntegrationTest {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TaskArgsRepository taskArgsRepository;
    @Autowired
    TimetableRepository timetableRepository;
    @Autowired
    HistoryRunsRepository historyRunsRepository;
    @Autowired
    RunMaster runMaster;
    @Autowired
    Engine engine;

    @Test
    public void testRun() throws Exception {
        for (int i = 0; i < 100; i++) {
            SchedulingParams params = timetableRepository.save(
                    SchedulingParamsImpl.newTask(
                            Integer.toString(i),
                            new EngineRequirementsImpl(i % 100, SimpleExecutor.class.getName(), "main"),
                            SchedulingType.ONCE,
                            null
                    )
                            .build()
            );
            taskRepository.save(TaskImpl.newTask(params.getTaskId()));
            taskArgsRepository.save(params.getTaskId(), TaskArgsImpl.builder(params.getTaskId())
                    .put("name", Integer.toString(i))
                    .build());
        }

        while (historyRunsRepository.getAll().size() != 100) {
            Thread.sleep(100);
        }
    }

    public static final class SimpleExecutor implements TaskExecutor {

        @Override
        public void run(RunContext context) {
            TaskArgs taskArgs = context.getTaskArgs();
            Run run = context.getRun();

            System.out.printf("Task params %s were executed in run %d with task %s%n",
                    taskArgs.get("name"), run.getRunId(), run.getTaskId());
        }
    }

}