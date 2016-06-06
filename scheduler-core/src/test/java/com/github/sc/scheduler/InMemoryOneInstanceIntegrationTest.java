package com.github.sc.scheduler;

import com.github.sc.scheduler.engine.Engine;
import com.github.sc.scheduler.engine.RunContext;
import com.github.sc.scheduler.engine.SimpleInOrderTaskPicker;
import com.github.sc.scheduler.engine.TaskPicker;
import com.github.sc.scheduler.engine.executor.lookup.ByClassNameExecutorLookupService;
import com.github.sc.scheduler.engine.executor.lookup.ExecutorLookupService;
import com.github.sc.scheduler.model.*;
import com.github.sc.scheduler.repo.memory.*;
import com.github.sc.scheduler.utils.FromPropertySchedulerHostProvider;
import org.junit.Test;

import java.time.Clock;
import java.util.Collections;

public class InMemoryOneInstanceIntegrationTest {

    @Test
    public void testRun() throws Exception {
        Clock clock = Clock.systemDefaultZone();

        InMemoryActiveRunsRepository activeRunsRepository = new InMemoryActiveRunsRepository();
        InMemoryHistoryRunsRepository historyRunsRepository = new InMemoryHistoryRunsRepository();
        InMemoryTaskArgsRepository taskParamsRepository = new InMemoryTaskArgsRepository();
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryTimetableRepository timetableRepository = new InMemoryTimetableRepository();

        RunMaster runMaster = new RunMaster();
        runMaster.setActiveRunsRepository(activeRunsRepository);
        runMaster.setHistoryRunsRepository(historyRunsRepository);
        runMaster.setTaskRepository(taskRepository);
        runMaster.setTimetableRepository(timetableRepository);
        runMaster.setClock(clock);
        runMaster.setHost("localhost");
        runMaster.setPeriodSeconds(1);

        TaskPicker taskPicker = new SimpleInOrderTaskPicker();
        ExecutorLookupService executorLookupService = new ByClassNameExecutorLookupService();

        Engine engine = new Engine();
        engine.setActiveRunsRepository(activeRunsRepository);
        engine.setTaskArgsRepository(taskParamsRepository);
        engine.setExecutorLookupService(executorLookupService);
        engine.setTaskPicker(taskPicker);
        engine.setClock(clock);
        engine.setHostProvider(new FromPropertySchedulerHostProvider("localhost"));
        engine.setCapacity(100);
        engine.setThreadsCount(2);
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
            taskParamsRepository.save(task.getId(), TaskArgsImpl.builder(task.getId())
                    .put("name", Integer.toString(i))
                    .build());
            timetableRepository.save(SchedulingParamsImpl.builder(task.getId(), SchedulingType.ONCE, null).build());
        }

        while (historyRunsRepository.getAll().size() != 1000) {
            Thread.sleep(100);
        }
        runMaster.destroy();
        engine.destroy();
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