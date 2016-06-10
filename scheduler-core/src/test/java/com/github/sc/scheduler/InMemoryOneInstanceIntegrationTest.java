package com.github.sc.scheduler;

import com.github.sc.scheduler.core.RunMaster;
import com.github.sc.scheduler.core.engine.Engine;
import com.github.sc.scheduler.core.engine.RunContext;
import com.github.sc.scheduler.core.engine.SimpleInOrderTaskPicker;
import com.github.sc.scheduler.core.engine.TaskPicker;
import com.github.sc.scheduler.core.engine.executor.lookup.ByClassNameExecutorLookupService;
import com.github.sc.scheduler.core.engine.executor.lookup.ExecutorLookupService;
import com.github.sc.scheduler.core.model.*;
import com.github.sc.scheduler.core.repo.memory.*;
import com.github.sc.scheduler.core.utils.FromPropertySchedulerHostProvider;
import org.junit.Test;

import java.time.Clock;
import java.util.Collections;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class InMemoryOneInstanceIntegrationTest {

    @Test
    public void testRun() throws Exception {
        Clock clock = Clock.systemDefaultZone();

        InMemoryActiveRunsRepository activeRunsRepository = new InMemoryActiveRunsRepository();
        InMemoryHistoryRunsRepository historyRunsRepository = new InMemoryHistoryRunsRepository();
        InMemoryTaskArgsRepository taskArgsRepository = new InMemoryTaskArgsRepository();
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryTimetableRepository timetableRepository = new InMemoryTimetableRepository();
        FromPropertySchedulerHostProvider hostProvider = new FromPropertySchedulerHostProvider("localhost");

        RunMaster runMaster = new RunMaster();
        runMaster.setActiveRunsRepository(activeRunsRepository);
        runMaster.setHistoryRunsRepository(historyRunsRepository);
        runMaster.setTaskRepository(taskRepository);
        runMaster.setTimetableRepository(timetableRepository);
        runMaster.setClock(clock);
        runMaster.setHostProvider(hostProvider);
        runMaster.setPeriodSeconds(1);

        TaskPicker taskPicker = new SimpleInOrderTaskPicker();
        ExecutorLookupService executorLookupService = new ByClassNameExecutorLookupService();

        Engine engine = new Engine();
        engine.setActiveRunsRepository(activeRunsRepository);
        engine.setTaskArgsRepository(taskArgsRepository);
        engine.setExecutorLookupService(executorLookupService);
        engine.setTaskPicker(taskPicker);
        engine.setClock(clock);
        engine.setHostProvider(hostProvider);
        engine.setCapacity(100);
        engine.setThreadsCount(2);
        engine.setService("main");
        engine.setPickPeriodSeconds(1);

        runMaster.setEngines(Collections.singleton(engine));
        runMaster.start();
        engine.start();

        for (int i = 0; i < 1000; i++) {
            SchedulingParams params = timetableRepository.save(SchedulingParamsImpl.newTask(
                    new EngineRequirementsImpl(i % 100, SimpleExecutor.class.getName(), "main"),
                    SchedulingType.ONCE,
                    null
            ).build());
            taskRepository.save(TaskImpl.newTask(params.getTaskId()));
            taskArgsRepository.save(params.getTaskId(), TaskArgsImpl.builder(params.getTaskId())
                    .put("name", Integer.toString(i))
                    .build());
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