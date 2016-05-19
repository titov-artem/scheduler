package ru.yandex.qe.common.scheduler;

import org.junit.Test;
import ru.yandex.qe.common.scheduler.engine.Context;
import ru.yandex.qe.common.scheduler.engine.Engine;
import ru.yandex.qe.common.scheduler.engine.SimpleInOrderTaskPicker;
import ru.yandex.qe.common.scheduler.engine.TaskPicker;
import ru.yandex.qe.common.scheduler.engine.executor.lookup.ByClassNameExecutorLookupService;
import ru.yandex.qe.common.scheduler.engine.executor.lookup.ExecutorLookupService;
import ru.yandex.qe.common.scheduler.model.*;
import ru.yandex.qe.common.scheduler.repo.memory.*;

import java.time.Clock;
import java.util.Collections;

public class InMemoryOneInstanceIntegrationTest {

    @Test
    public void testRun() throws Exception {
        Clock clock = Clock.systemDefaultZone();

        InMemoryActiveRunsRepository activeRunsRepository = new InMemoryActiveRunsRepository();
        InMemoryHistoryRunsRepository historyRunsRepository = new InMemoryHistoryRunsRepository();
        InMemoryTaskParamsRepository taskParamsRepository = new InMemoryTaskParamsRepository();
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryTimetableRepository timetableRepository = new InMemoryTimetableRepository();

        RunMaster runMaster = new RunMaster();
        runMaster.setActiveRunsRepository(activeRunsRepository);
        runMaster.setHistoryRunsRepository(historyRunsRepository);
        runMaster.setTaskRepository(taskRepository);
        runMaster.setTimetableRepository(timetableRepository);
        runMaster.setClock(clock);
        runMaster.setHost("localhost");
        runMaster.setPeriod(1);

        TaskPicker taskPicker = new SimpleInOrderTaskPicker();
        ExecutorLookupService executorLookupService = new ByClassNameExecutorLookupService();

        Engine engine = new Engine();
        engine.setActiveRunsRepository(activeRunsRepository);
        engine.setTaskParamsRepository(taskParamsRepository);
        engine.setExecutorLookupService(executorLookupService);
        engine.setTaskPicker(taskPicker);
        engine.setClock(clock);
        engine.setHost("localhost");
        engine.setCapacity(100);
        engine.setThreadCount(2);
        engine.setService("main");
        engine.setPickPeriod(1);

        runMaster.setEngines(Collections.singleton(engine));
        runMaster.start();
        engine.start();

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
        runMaster.destroy();
        engine.destroy();
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