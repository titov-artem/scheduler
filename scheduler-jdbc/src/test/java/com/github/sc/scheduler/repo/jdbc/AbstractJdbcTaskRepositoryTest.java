package com.github.sc.scheduler.repo.jdbc;

import com.github.sc.scheduler.FlywayInit;
import com.github.sc.scheduler.model.EngineRequirementsImpl;
import com.github.sc.scheduler.model.Task;
import com.github.sc.scheduler.model.TaskImpl;
import com.github.sc.scheduler.repo.TaskRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public abstract class AbstractJdbcTaskRepositoryTest {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    FlywayInit flywayInit;

    @After
    public void cleanup() {
        flywayInit.init();
    }

    @Test
    public void testGetAll() throws Exception {
        Task task1 = TaskImpl.newTask("name1", new EngineRequirementsImpl(10, "Executor1", "Service"));
        Task task2 = TaskImpl.newTask(new EngineRequirementsImpl(11, "Executor2", "Service"));

        Task created1 = taskRepository.create(task1);
        Task created2 = taskRepository.create(task2);

        Map<String, Task> argsMap = taskRepository.getAll().stream()
                .collect(toMap(Task::getId, i -> i));

        Assert.assertThat(argsMap.get(created1.getId()), is(created1));
        Assert.assertThat(argsMap.get(created2.getId()), is(created2));
    }

    @Test
    public void testRemove() throws Exception {
        Task task1 = TaskImpl.newTask("name1", new EngineRequirementsImpl(10, "Executor1", "Service"));
        Task task2 = TaskImpl.newTask(new EngineRequirementsImpl(11, "Executor2", "Service"));

        Task created1 = taskRepository.create(task1);
        Task created2 = taskRepository.create(task2);

        taskRepository.remove(created1.getId());

        Map<String, Task> argsMap = taskRepository.getAll().stream()
                .collect(toMap(Task::getId, i -> i));

        Assert.assertThat(argsMap.get(created1.getId()), nullValue());
        Assert.assertThat(argsMap.get(created2.getId()), is(created2));
    }


    @Test
    public void testGet() throws Exception {
        Task task = TaskImpl.newTask("Name", new EngineRequirementsImpl(10, "Executor", "Service"));
        Task created = taskRepository.create(task);
        assertThat(created, notNullValue());
        assertThat(created.getName(), notNullValue());

        Optional<Task> loaded = taskRepository.get(created.getId());
        assertThat(loaded.isPresent(), is(true));
        assertThat(loaded.get(), is(created));
    }

    @Test
    public void testGetNotExists() throws Exception {
        Optional<Task> loaded = taskRepository.get("task1");
        assertThat(loaded.isPresent(), is(false));
    }
}