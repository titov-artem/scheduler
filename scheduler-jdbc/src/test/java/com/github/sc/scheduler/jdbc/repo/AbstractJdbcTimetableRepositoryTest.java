package com.github.sc.scheduler.jdbc.repo;

import com.github.sc.scheduler.core.model.EngineRequirementsImpl;
import com.github.sc.scheduler.core.model.SchedulingParams;
import com.github.sc.scheduler.core.model.SchedulingParamsImpl;
import com.github.sc.scheduler.core.model.SchedulingType;
import com.github.sc.scheduler.core.repo.TimetableRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public abstract class AbstractJdbcTimetableRepositoryTest {

    @Autowired
    TimetableRepository timetableRepository;

    @Test
    public void testGetAll() throws Exception {
        SchedulingParams run = SchedulingParamsImpl.newTask("name", new EngineRequirementsImpl(1, "executor", "service"), SchedulingType.ONCE, null)
                .build();
        SchedulingParams created = timetableRepository.save(run);

        Optional<SchedulingParams> task = timetableRepository.get(created.getTaskId());
        assertThat(task.isPresent(), is(true));
        assertThat(task.get(), is(created));

        List<SchedulingParams> tasks = timetableRepository.getAll();
        assertThat(tasks.size(), is(1));
        assertThat(tasks.get(0), is(created));
    }

    @Test
    public void testRemoveTask() throws Exception {
        SchedulingParams run = SchedulingParamsImpl.newTask("name", new EngineRequirementsImpl(1, "executor", "service"), SchedulingType.ONCE, null)
                .build();
        SchedulingParams created = timetableRepository.save(run);


        List<SchedulingParams> before = timetableRepository.getAll();
        timetableRepository.removeTask(created.getTaskId());
        List<SchedulingParams> after = timetableRepository.getAll();
        assertThat(before.size(), is(1));
        assertThat(after.size(), is(0));
    }

    @Test
    public void testRemoveTasks() throws Exception {
        SchedulingParams run = SchedulingParamsImpl.newTask("name", new EngineRequirementsImpl(1, "executor", "service"), SchedulingType.ONCE, null)
                .build();
        SchedulingParams created = timetableRepository.save(run);


        List<SchedulingParams> before = timetableRepository.getAll();
        timetableRepository.removeTasks(Collections.singleton(created.getTaskId()));
        List<SchedulingParams> after = timetableRepository.getAll();
        assertThat(before.size(), is(1));
        assertThat(after.size(), is(0));
    }
}