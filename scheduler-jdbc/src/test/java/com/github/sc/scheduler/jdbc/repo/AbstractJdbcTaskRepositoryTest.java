package com.github.sc.scheduler.jdbc.repo;

import com.github.sc.scheduler.core.model.Task;
import com.github.sc.scheduler.core.model.TaskImpl;
import com.github.sc.scheduler.core.repo.TaskRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static com.github.sc.scheduler.core.model.TaskImpl.builder;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public abstract class AbstractJdbcTaskRepositoryTest {

    @Autowired
    TaskRepository taskRepository;

    @Test
    public void testGetAll() throws Exception {
        Task task1 = TaskImpl.newTask("task1");
        Task task2 = TaskImpl.newTask("task2");

        taskRepository.save(task1);
        taskRepository.save(task2);

        Map<String, Task> argsMap = taskRepository.getAll().stream()
                .collect(toMap(Task::getId, i -> i));

        Assert.assertThat(argsMap.get(task1.getId()), is(task1));
        Assert.assertThat(argsMap.get(task2.getId()), is(task2));
    }

    @Test
    public void testRemove() throws Exception {
        Task task1 = TaskImpl.newTask("task1");
        Task task2 = TaskImpl.newTask("task2");

        taskRepository.save(task1);
        taskRepository.save(task2);

        taskRepository.remove(task1.getId());

        Map<String, Task> argsMap = taskRepository.getAll().stream()
                .collect(toMap(Task::getId, i -> i));

        Assert.assertThat(argsMap.get(task1.getId()), nullValue());
        Assert.assertThat(argsMap.get(task2.getId()), is(task2));
    }


    @Test
    public void testGet() throws Exception {
        Task task = builder("task1")
                .withLastRunTime(Instant.now().truncatedTo(SECONDS))
                .withStartingHost("host")
                .withStartingTime(Instant.now().truncatedTo(SECONDS))
                .withVersion(2)
                .build();
        taskRepository.save(task);

        Optional<Task> loaded = taskRepository.get(task.getId());
        assertThat(loaded.isPresent(), is(true));
        assertThat(loaded.get(), is(task));
    }

    @Test
    public void testGetNotExists() throws Exception {
        Optional<Task> loaded = taskRepository.get("task1");
        assertThat(loaded.isPresent(), is(false));
    }

    @Test
    public void testTryUpdateWithSameVersion() throws Exception {
        Task task = builder("task1")
                .withLastRunTime(Instant.now().truncatedTo(SECONDS))
                .withStartingHost("host")
                .withStartingTime(Instant.now().truncatedTo(SECONDS))
                .withVersion(2)
                .build();
        taskRepository.save(task);
        Task patched = builder(task)
                .withStartingHost(null)
                .build();
        Task updated = taskRepository.tryUpdate(patched);
        assertThat(updated, is(builder(patched).withVersion(patched.getVersion() + 1).build()));
    }

    @Test
    public void testTryUpdateWithWrongVersion() throws Exception {
        Task task = builder("task1")
                .withLastRunTime(Instant.now().truncatedTo(SECONDS))
                .withStartingHost("host")
                .withStartingTime(Instant.now().truncatedTo(SECONDS))
                .withVersion(2)
                .build();
        taskRepository.save(task);
        Task patched = builder(task)
                .withStartingHost(null)
                .withVersion(1)
                .build();
        Task updated = taskRepository.tryUpdate(patched);
        assertThat(updated, is(task));
    }

    @Test
    public void testTryUpdateMissed() throws Exception {
        Task task = builder("task1")
                .withLastRunTime(Instant.now().truncatedTo(SECONDS))
                .withStartingHost("host")
                .withStartingTime(Instant.now().truncatedTo(SECONDS))
                .withVersion(2)
                .build();
        Task updated = taskRepository.tryUpdate(task);
        assertThat(updated, nullValue());
    }

    @Test
    public void testTryUpdateLastRunTime() throws Exception {
        Task task = builder("task1")
                .withLastRunTime(Instant.now().truncatedTo(SECONDS))
                .withStartingHost("host")
                .withStartingTime(Instant.now().truncatedTo(SECONDS))
                .withVersion(2)
                .build();
        taskRepository.save(task);

        Instant newLastRunTime = Instant.now().truncatedTo(SECONDS);
        taskRepository.tryUpdateLastRunTime("task1", newLastRunTime);

        Optional<Task> updated = taskRepository.get("task1");
        assertThat(updated.get(), is(builder(task).withLastRunTime(newLastRunTime).build()));
    }
}