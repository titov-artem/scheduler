package com.github.sc.scheduler.repo.jdbc;

import com.github.sc.scheduler.FlywayInit;
import com.github.sc.scheduler.model.EngineRequirementsImpl;
import com.github.sc.scheduler.model.Run;
import com.github.sc.scheduler.model.RunImpl;
import com.github.sc.scheduler.repo.ActiveRunsRepository;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public abstract class AbstractJdbcActiveRunsRepositoryTest {

    @Autowired
    ActiveRunsRepository activeRunsRepository;

    @Autowired
    FlywayInit flywayInit;

    @After
    public void cleanup() {
        flywayInit.init();
    }

    @Test
    public void testGetRunsInOrder() throws Exception {
        Run run1 = RunImpl.builder(Run.FAKE_RUN_ID,
                "task1",
                new EngineRequirementsImpl(1, "Executor", "Service"),
                Run.Status.PENDING,
                Instant.now().truncatedTo(SECONDS)
        ).build();
        Run run2 = RunImpl.builder(Run.FAKE_RUN_ID,
                "task2",
                new EngineRequirementsImpl(1, "Executor", "Service"),
                Run.Status.PENDING,
                Instant.now().truncatedTo(SECONDS)
        ).build();
        Run run3 = RunImpl.builder(Run.FAKE_RUN_ID,
                "task3",
                new EngineRequirementsImpl(1, "Executor", "Service"),
                Run.Status.PENDING,
                Instant.now().truncatedTo(SECONDS)
        ).build();

        Run created1 = activeRunsRepository.create(run1);
        Run created2 = activeRunsRepository.create(run2);
        Run created3 = activeRunsRepository.create(run3);

        List<Run> runs = activeRunsRepository.getAll();
        assertThat(runs.size(), is(3));
        assertThat(runs.get(0), is(created1));
        assertThat(runs.get(1), is(created2));
        assertThat(runs.get(2), is(created3));
    }

    @Test
    public void testStoringFullRun() throws Exception {
        Run run = buildRun();
        Run created = activeRunsRepository.create(run);
        List<Run> runs = activeRunsRepository.getAll();

        assertThat(runs.size(), is(1));
        assertThat(runs.get(0), is(created));

        Optional<Run> loaded = activeRunsRepository.get(created.getRunId());

        assertThat(loaded.isPresent(), is(true));
        assertThat(loaded.get(), is(created));

        List<Run> byTask = activeRunsRepository.get("task1");

        assertThat(byTask.size(), is(1));
        assertThat(byTask.get(0), is(created));

        Collection<Run> byTask2 = activeRunsRepository.getRuns(Collections.singleton("task1")).get("task1");

        assertThat(byTask2.size(), is(1));
        assertThat(byTask2.iterator().next(), is(created));
    }

    @Test
    public void testStoringNewRun() throws Exception {
        Run run = RunImpl.builder(Run.FAKE_RUN_ID,
                "task1",
                new EngineRequirementsImpl(1, "Executor", "Service"),
                Run.Status.PENDING,
                Instant.now().truncatedTo(SECONDS)
        ).build();
        Run created = activeRunsRepository.create(run);
        List<Run> runs = activeRunsRepository.getAll();

        assertThat(runs.size(), is(1));
        assertThat(runs.get(0), is(created));

        Optional<Run> loaded = activeRunsRepository.get(created.getRunId());

        assertThat(loaded.isPresent(), is(true));
        assertThat(loaded.get(), is(created));

        List<Run> byTask = activeRunsRepository.get("task1");

        assertThat(byTask.size(), is(1));
        assertThat(byTask.get(0), is(created));

        Collection<Run> byTask2 = activeRunsRepository.getRuns(Collections.singleton("task1")).get("task1");

        assertThat(byTask2.size(), is(1));
        assertThat(byTask2.iterator().next(), is(created));
    }

    @Test
    public void testCreateIfNotExistsNotCreateWhenExists() throws Exception {
        Run run = RunImpl.builder(Run.FAKE_RUN_ID,
                "task1",
                new EngineRequirementsImpl(1, "Executor", "Service"),
                Run.Status.PENDING,
                Instant.now().truncatedTo(SECONDS)
        ).build();
        Run created = activeRunsRepository.create(run);
        activeRunsRepository.createIfNotExists(Collections.singleton(created));
        List<Run> runs = activeRunsRepository.getAll();

        assertThat(runs.size(), is(1));
        assertThat(runs.get(0), is(created));
    }

    @Test
    public void testCreateIfNotExistsCreateWhenNotExists() throws Exception {
        Run run = RunImpl.builder(1,
                "task1",
                new EngineRequirementsImpl(1, "Executor", "Service"),
                Run.Status.PENDING,
                Instant.now().truncatedTo(SECONDS)
        ).build();

        activeRunsRepository.createIfNotExists(Collections.singleton(run));
        List<Run> runs = activeRunsRepository.getAll();

        assertThat(runs.size(), is(1));
        assertThat(runs.get(0), is(run));
    }

    @Test
    public void testCreateIfNotExistsCreateMultiple() throws Exception {
        Run run1 = RunImpl.builder(1,
                "task1",
                new EngineRequirementsImpl(1, "Executor", "Service"),
                Run.Status.PENDING,
                Instant.now().truncatedTo(SECONDS)
        ).build();
        Run run2 = RunImpl.builder(2,
                "task1",
                new EngineRequirementsImpl(1, "Executor", "Service"),
                Run.Status.PENDING,
                Instant.now().truncatedTo(SECONDS)
        ).build();
        Run run3 = RunImpl.builder(3,
                "task1",
                new EngineRequirementsImpl(1, "Executor", "Service"),
                Run.Status.PENDING,
                Instant.now().truncatedTo(SECONDS)
        ).build();

        activeRunsRepository.create(run1);

        activeRunsRepository.createIfNotExists(ImmutableList.of(
                run1, run2, run3
        ));
        List<Run> runs = activeRunsRepository.getAll();

        assertThat(runs.size(), is(3));
        assertThat(runs.get(0), is(run1));
        assertThat(runs.get(1), is(run2));
        assertThat(runs.get(2), is(run3));
    }

    @Test
    public void testTryUpdateWithMatchedVersion() throws Exception {
        Run run = buildRun();
        Run created = activeRunsRepository.create(run);
        Run patched = RunImpl.builder(created).withStatus(Run.Status.COMPLETE).build();
        Run updated = activeRunsRepository.tryUpdate(patched);
        assertThat(updated, is(RunImpl.builder(patched).withVersion(patched.getVersion() + 1).build()));
    }

    @Test
    public void testTryUpdateWithMissedVersion() throws Exception {
        Run run = buildRun();
        Run created = activeRunsRepository.create(run);
        Run patched = RunImpl.builder(created).withStatus(Run.Status.COMPLETE).withVersion(3).build();
        Run updated = activeRunsRepository.tryUpdate(patched);
        assertThat(updated, is(created));
    }

    @Test
    public void testTryUpdateWithMissedRun() throws Exception {
        Run run = buildRun();
        Run updated = activeRunsRepository.tryUpdate(run);
        assertThat(updated, nullValue());
    }

    @Test
    public void testRemove() throws Exception {
        Run run = buildRun();
        Run created = activeRunsRepository.create(run);
        List<Run> before = activeRunsRepository.getAll();
        activeRunsRepository.remove(Collections.singleton(created));
        List<Run> after = activeRunsRepository.getAll();
        assertThat(before.size(), is(1));
        assertThat(after.size(), is(0));
    }

    @Test
    public void testPing() throws Exception {
        Run run = buildRun();
        Run created = activeRunsRepository.create(run);
        Instant pingTime = Instant.now().plusSeconds(1000).truncatedTo(SECONDS);
        activeRunsRepository.ping(created.getRunId(), pingTime);

        Optional<Run> pinged = activeRunsRepository.get(created.getRunId());
        assertThat(pinged.isPresent(), is(true));
        assertThat(pinged.get().getPingTime(), is(pingTime));
    }

    private Run buildRun() {
        return RunImpl.builder(Run.FAKE_RUN_ID,
                "task1",
                new EngineRequirementsImpl(1, "Executor", "Service"),
                Run.Status.PENDING,
                Instant.now().truncatedTo(SECONDS)
        )
                .withVersion(1)
                .withAcquiredTime(Instant.now().truncatedTo(SECONDS))
                .withStartTime(Instant.now().truncatedTo(SECONDS))
                .withPingTime(Instant.now().truncatedTo(SECONDS))
                .withEndTime(Instant.now().truncatedTo(SECONDS))
                .withHost("host1")
                .withMessage("Hello world")
                .withQueuedTime(Instant.now().truncatedTo(SECONDS))
                .build();
    }
}