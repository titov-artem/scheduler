package com.github.sc.scheduler.repo.jdbc;

import com.github.sc.scheduler.FlywayInit;
import com.github.sc.scheduler.model.SchedulingParams;
import com.github.sc.scheduler.model.SchedulingType;
import com.github.sc.scheduler.repo.TimetableRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.github.sc.scheduler.model.SchedulingParamsImpl.builder;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public abstract class AbstractJdbcTimetableRepositoryTest {

    @Autowired
    TimetableRepository timetableRepository;

    @Autowired
    FlywayInit flywayInit;

    @After
    public void cleanup() {
        flywayInit.init();
    }

    @Test
    public void testGetAll() throws Exception {
        SchedulingParams run = builder("task1", SchedulingType.ONCE, null)
                .withLastRunTime(Instant.now().truncatedTo(SECONDS))
                .withStartingHost("host")
                .withStartingTime(Instant.now().truncatedTo(SECONDS))
                .withVersion(2)
                .build();
        timetableRepository.save(run);

        Optional<SchedulingParams> task = timetableRepository.getTask("task1");
        assertThat(task.isPresent(), is(true));
        assertThat(task.get(), is(run));

        List<SchedulingParams> tasks = timetableRepository.getAll();
        assertThat(tasks.size(), is(1));
        assertThat(tasks.get(0), is(run));
    }


    @Test
    public void testTryUpdateWithSameVersion() throws Exception {
        SchedulingParams run = builder("task1", SchedulingType.ONCE, null)
                .withLastRunTime(Instant.now().truncatedTo(SECONDS))
                .withStartingHost("host")
                .withStartingTime(Instant.now().truncatedTo(SECONDS))
                .withVersion(2)
                .build();
        timetableRepository.save(run);
        SchedulingParams patched = builder(run)
                .withStartingHost(null)
                .build();
        SchedulingParams updated = timetableRepository.tryUpdate(patched);
        assertThat(updated, is(builder(patched).withVersion(patched.getVersion() + 1).build()));
    }

    @Test
    public void testTryUpdateWithWrongVersion() throws Exception {
        SchedulingParams run = builder("task1", SchedulingType.ONCE, null)
                .withLastRunTime(Instant.now().truncatedTo(SECONDS))
                .withStartingHost("host")
                .withStartingTime(Instant.now().truncatedTo(SECONDS))
                .withVersion(2)
                .build();
        timetableRepository.save(run);
        SchedulingParams patched = builder(run)
                .withStartingHost(null)
                .withVersion(1)
                .build();
        SchedulingParams updated = timetableRepository.tryUpdate(patched);
        assertThat(updated, is(run));
    }

    @Test
    public void testTryUpdateMissed() throws Exception {
        SchedulingParams run = builder("task1", SchedulingType.ONCE, null)
                .withLastRunTime(Instant.now().truncatedTo(SECONDS))
                .withStartingHost("host")
                .withStartingTime(Instant.now().truncatedTo(SECONDS))
                .withVersion(2)
                .build();
        SchedulingParams updated = timetableRepository.tryUpdate(run);
        assertThat(updated, nullValue());
    }

    @Test
    public void testTryUpdateLastRunTime() throws Exception {
        SchedulingParams run = builder("task1", SchedulingType.ONCE, null)
                .withLastRunTime(Instant.now().truncatedTo(SECONDS))
                .withStartingHost("host")
                .withStartingTime(Instant.now().truncatedTo(SECONDS))
                .withVersion(2)
                .build();
        timetableRepository.save(run);

        Instant newLastRunTime = Instant.now().truncatedTo(SECONDS);
        timetableRepository.tryUpdateLastRunTime("task1", newLastRunTime);

        Optional<SchedulingParams> updated = timetableRepository.getTask("task1");
        assertThat(updated.get(), is(builder(run).withLastRunTime(newLastRunTime).build()));
    }

    @Test
    public void testRemoveTask() throws Exception {
        SchedulingParams run = builder("task1", SchedulingType.ONCE, null)
                .withLastRunTime(Instant.now().truncatedTo(SECONDS))
                .withStartingHost("host")
                .withStartingTime(Instant.now().truncatedTo(SECONDS))
                .withVersion(2)
                .build();
        timetableRepository.save(run);


        List<SchedulingParams> before = timetableRepository.getAll();
        timetableRepository.removeTask("task1");
        List<SchedulingParams> after = timetableRepository.getAll();
        assertThat(before.size(), is(1));
        assertThat(after.size(), is(0));
    }

    @Test
    public void testRemoveTasks() throws Exception {
        SchedulingParams run = builder("task1", SchedulingType.ONCE, null)
                .withLastRunTime(Instant.now().truncatedTo(SECONDS))
                .withStartingHost("host")
                .withStartingTime(Instant.now().truncatedTo(SECONDS))
                .withVersion(2)
                .build();
        timetableRepository.save(run);


        List<SchedulingParams> before = timetableRepository.getAll();
        timetableRepository.removeTasks(Collections.singleton("task1"));
        List<SchedulingParams> after = timetableRepository.getAll();
        assertThat(before.size(), is(1));
        assertThat(after.size(), is(0));
    }
}