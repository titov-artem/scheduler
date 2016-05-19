package ru.yandex.qe.common.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.qe.common.scheduler.engine.Context;
import ru.yandex.qe.common.scheduler.engine.Engine;
import ru.yandex.qe.common.scheduler.model.*;
import ru.yandex.qe.common.scheduler.repo.HistoryRunsRepository;
import ru.yandex.qe.common.scheduler.repo.TaskParamsRepository;
import ru.yandex.qe.common.scheduler.repo.TaskRepository;
import ru.yandex.qe.common.scheduler.repo.TimetableRepository;

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
        for (int i = 0; i < 1000; i++) {
            Task task = taskRepository.create(TaskImpl.newTask(
                    Integer.toString(i),
                    new EngineRequirementsImpl(i % 100, SimpleExecutor.class.getName(), "main")
            ));
            taskParamsRepository.save(task.getId(), TaskParamsImpl.builder()
                    .put("name", Integer.toString(i))
                    .build());
            timetableRepository.save(SchedulingParamsImpl.builder(task.getId(), SchedulingType.ONCE, null).build());
        }

        while (historyRunsRepository.getRuns().size() != 1000) {
            Thread.sleep(100);
        }
    }

    public static final class SimpleExecutor implements Runnable {

        @Override
        public void run() {
            TaskParams taskParams = Context.get().getTaskParams();
            Run run = Context.get().getRun();

            System.out.printf("Task params %s were executed in run %d with task %s%n",
                    taskParams.get("name"), run.getRunId(), run.getTaskId());
        }
    }

}