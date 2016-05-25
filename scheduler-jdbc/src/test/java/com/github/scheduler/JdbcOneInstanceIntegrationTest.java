package com.github.scheduler;

import com.github.scheduler.engine.Context;
import com.github.scheduler.engine.Engine;
import com.github.scheduler.model.*;
import com.github.scheduler.repo.HistoryRunsRepository;
import com.github.scheduler.repo.TaskParamsRepository;
import com.github.scheduler.repo.TaskRepository;
import com.github.scheduler.repo.TimetableRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:mysql-jdbc-repositories-test.xml", "classpath*:scheduler-test.xml"})
@Transactional
public class JdbcOneInstanceIntegrationTest {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TaskParamsRepository taskParamsRepository;
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
            taskParamsRepository.save(task.getId(), TaskArgsImpl.builder()
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
            TaskArgs taskArgs = Context.get().getTaskArgs();
            Run run = Context.get().getRun();

            System.out.printf("Task params %s were executed in run %d with task %s%n",
                    taskArgs.get("name"), run.getRunId(), run.getTaskId());
        }
    }

}