package com.github.sc.scheduler;

import com.github.sc.scheduler.engine.Engine;
import com.github.sc.scheduler.engine.RunContext;
import com.github.sc.scheduler.model.*;
import com.github.sc.scheduler.repo.HistoryRunsRepository;
import com.github.sc.scheduler.repo.TaskArgsRepository;
import com.github.sc.scheduler.repo.TaskRepository;
import com.github.sc.scheduler.repo.TimetableRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
            Task task = taskRepository.create(TaskImpl.newTask(
                    Integer.toString(i),
                    new EngineRequirementsImpl(i % 100, SimpleExecutor.class.getName(), "main")
            ));
            taskArgsRepository.save(task.getId(), TaskArgsImpl.builder()
                    .put("name", Integer.toString(i))
                    .build());
            timetableRepository.save(SchedulingParamsImpl.builder(task.getId(), SchedulingType.ONCE, null).build());
        }

        while (historyRunsRepository.getRuns().size() != 100) {
            Thread.sleep(100);
        }
    }

    public static final class SimpleExecutor implements Runnable {

        @Override
        public void run() {
            TaskArgs taskArgs = RunContext.get().getTaskArgs();
            Run run = RunContext.get().getRun();

            System.out.printf("Task params %s were executed in run %d with task %s%n",
                    taskArgs.get("name"), run.getRunId(), run.getTaskId());
        }
    }

}