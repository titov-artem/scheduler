package com.github.sc.scheduler.repo.jdbc;

import com.github.sc.scheduler.FlywayInit;
import com.github.sc.scheduler.model.EngineRequirementsImpl;
import com.github.sc.scheduler.model.Task;
import com.github.sc.scheduler.model.TaskImpl;
import com.github.sc.scheduler.repo.TaskRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
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
    public void testGet() throws Exception {
        Task task = TaskImpl.newTask("Name", new EngineRequirementsImpl(10, "Executor", "Service"));
        Task created = taskRepository.create(task);
        assertThat(created, notNullValue());

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