package ru.yandex.qe.common.scheduler.repo.jdbc.mysql;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.qe.common.scheduler.model.EngineRequirementsImpl;
import ru.yandex.qe.common.scheduler.model.Task;
import ru.yandex.qe.common.scheduler.model.TaskImpl;
import ru.yandex.qe.common.scheduler.repo.TaskRepository;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:mysql-jdbc-repositories-test.xml")
@Transactional
public class MySqlJdbcTaskRepositoryTest {

    @Autowired
    TaskRepository taskRepository;

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