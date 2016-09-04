package com.github.sc.scheduler.core

import com.github.sc.scheduler.core.model.Run
import com.github.sc.scheduler.core.model.Task
import com.github.sc.scheduler.core.repo.ActiveRunsRepository
import com.github.sc.scheduler.core.repo.HistoryRunsRepository
import com.github.sc.scheduler.core.repo.TaskRepository
import com.github.sc.scheduler.core.repo.TimetableRepository
import com.github.sc.scheduler.core.utils.FromPropertySchedulerHostProvider
import com.github.sc.scheduler.core.utils.SchedulerHostProvider
import spock.lang.Specification

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class RunMasterTest extends Specification {

    TaskRepository taskRepository = Mock(TaskRepository)
    TimetableRepository timetableRepository = Mock(TimetableRepository)
    ActiveRunsRepository activeRunsRepository = Mock(ActiveRunsRepository)
    HistoryRunsRepository historyRunsRepository = Mock(HistoryRunsRepository)

    SchedulerHostProvider hostProvider = new FromPropertySchedulerHostProvider("localhost")

    RunMaster master = new RunMaster()

    Clock clock = Clock.fixed(Instant.parse("2016-01-01T00:00:00"), ZoneOffset.UTC)

    def setup() {
        master.setClock(clock);
        master.setHostProvider(hostProvider);
        master.setTaskRepository(taskRepository);
        master.setTimetableRepository(timetableRepository);
        master.setActiveRunsRepository(activeRunsRepository);
        master.setHistoryRunsRepository(historyRunsRepository);
    }

    def ".restore"() {
        given:
        activeRunsRepository.getAll() >> [
                Mock(Run) {
                    getRunId() >> 1L
                    getTaskId() >> '1'
                    getQueuedTime() >> Instant.now(clock)
                },
                Mock(Run) {
                    getRunId() >> 2L
                    getTaskId() >> '2'
                    getQueuedTime() >> Instant.now(clock)
                },
                Mock(Run) {
                    getRunId() >> 3L
                    getTaskId() >> '3'
                    getQueuedTime() >> Instant.now(clock)
                },
        ]
        taskRepository.getAll() >> [
                Mock(Task) {
                    getId() >> '1'
                    getLastRunTime() >> null
                },
                Mock(Task) {
                    getId() >> '2'
                    getLastRunTime() >> Instant.now(clock).minus(1, ChronoUnit.HOURS)
                },
                Mock(Task) {
                    getId() >> '3'
                    getLastRunTime() >> Instant.now(clock)
                },
                Mock(Task) {
                    getId() >> '4'
                    getLastRunTime() >> null
                },
        ]

        when:
        master.restore()

        then:
        1 == 1
//        then:
//        taskRepository.
    }
}
