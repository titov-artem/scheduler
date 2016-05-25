package com.github.scheduler.repo.jdbc.mysql;

import com.github.scheduler.model.TaskArgs;
import com.github.scheduler.model.TaskArgsImpl;
import com.github.scheduler.repo.TaskParamsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:mysql-jdbc-repositories-test.xml")
@Transactional
public class MySqlJdbcTaskArgsRepositoryTest {

    @Autowired
    TaskParamsRepository taskParamsRepository;

    @Test
    public void testGet() throws Exception {
        TaskArgs params = TaskArgsImpl.builder()
                .append("param1", "value1")
                .put("param2", "value2")
                .build();

        taskParamsRepository.save("task1", params);

        Optional<TaskArgs> loaded = taskParamsRepository.get("task1");
        assertThat(loaded.isPresent(), is(true));
        assertThat(loaded.get(), is(params));
    }

    @Test
    public void testGetNotExists() throws Exception {
        Optional<TaskArgs> loaded = taskParamsRepository.get("task1");
        assertThat(loaded.isPresent(), is(false));
    }
}