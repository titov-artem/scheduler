package com.github.sc.scheduler.repo.jdbc;

import com.github.sc.scheduler.FlywayInit;
import com.github.sc.scheduler.model.TaskArgs;
import com.github.sc.scheduler.model.TaskArgsImpl;
import com.github.sc.scheduler.repo.TaskArgsRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public abstract class AbstractJdbcTaskArgsRepositoryTest {

    @Autowired
    TaskArgsRepository taskArgsRepository;

    @Autowired
    FlywayInit flywayInit;

    @After
    public void cleanup() {
        flywayInit.init();
    }

    @Test
    public void testGetAll() throws Exception {
        TaskArgs args1 = TaskArgsImpl.builder("task1")
                .append("p1", "v1")
                .build();
        TaskArgs args2 = TaskArgsImpl.builder("task2")
                .append("p2", "v2")
                .append("pp2", "vv2")
                .build();

        taskArgsRepository.save("task1", args1);
        taskArgsRepository.save("task2", args2);

        Map<String, TaskArgs> argsMap = taskArgsRepository.getAll().stream()
                .collect(toMap(TaskArgs::getTaskId, i -> i));

        assertThat(argsMap.get("task1"), is(args1));
        assertThat(argsMap.get("task2"), is(args2));
    }

    @Test
    public void testRemove() throws Exception {
        TaskArgs args1 = TaskArgsImpl.builder("task1")
                .append("p1", "v1")
                .build();
        TaskArgs args2 = TaskArgsImpl.builder("task2")
                .append("p2", "v2")
                .append("pp2", "vv2")
                .build();

        taskArgsRepository.save("task1", args1);
        taskArgsRepository.save("task2", args2);

        taskArgsRepository.remove("task1");

        Map<String, TaskArgs> argsMap = taskArgsRepository.getAll().stream()
                .collect(toMap(TaskArgs::getTaskId, i -> i));

        assertThat(argsMap.get("task1"), nullValue());
        assertThat(argsMap.get("task2"), is(args2));
    }

    @Test
    public void testGet() throws Exception {
        TaskArgs args = TaskArgsImpl.builder("task1")
                .append("param1", "value1")
                .put("param2", "value2")
                .build();

        taskArgsRepository.save("task1", args);

        Optional<TaskArgs> loaded = taskArgsRepository.get("task1");
        assertThat(loaded.isPresent(), is(true));
        assertThat(loaded.get(), is(args));
    }

    @Test
    public void testGetNotExists() throws Exception {
        Optional<TaskArgs> loaded = taskArgsRepository.get("task1");
        assertThat(loaded.isPresent(), is(false));
    }
}