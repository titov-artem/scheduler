package com.github.scheduler.repo.jdbc;

import com.github.scheduler.model.SchedulingParams;
import com.github.scheduler.model.SchedulingParamsImpl;
import com.github.scheduler.model.SchedulingType;
import com.github.scheduler.repo.TimetableRepository;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.github.scheduler.repo.jdbc.SQLSchema.*;
import static com.github.scheduler.repo.jdbc.util.SqlDateUtils.toInstant;
import static com.github.scheduler.repo.jdbc.util.SqlDateUtils.toTimestamp;

public abstract class AbstractJdbcTimetableRepository implements TimetableRepository {

    private static final RowMapper<SchedulingParams> ROW_MAPPER = (rs, i) ->
            SchedulingParamsImpl.builder(
                    rs.getString(TASK_ID.getName()),
                    SchedulingType.valueOf(rs.getString(TYPE.getName())),
                    rs.getString(PARAM.getName())
            )
                    .withLastRunTime(toInstant(rs.getTimestamp(LAST_RUN_TIME.getName())))
                    .withStartingHost(rs.getString(STARTING_HOST.getName()))
                    .withStartingTime(toInstant(rs.getTimestamp(STARTING_TIME.getName())))
                    .withVersion(rs.getInt(VERSION.getName()))
                    .build();

    private JdbcOperations jdbcOperations;

    @Override
    public List<SchedulingParams> getTasks() {
        Query query = DSL().select().from(TIMETABLE_TABLE);
        return jdbcOperations.query(query.getSQL(), ROW_MAPPER);
    }

    @Override
    public Optional<SchedulingParams> getTask(String taskId) {
        return Optional.ofNullable(get(taskId));
    }

    @Override
    public void save(SchedulingParams params) {
        Query query = DSL().insertInto(TIMETABLE_TABLE)
                .set(TASK_ID, params.getTaskId())
                .set(TYPE, params.getType().name())
                .set(PARAM, params.getParam())
                .set(LAST_RUN_TIME, toTimestamp(params.getLastRunTime()))
                .set(STARTING_HOST, params.getStartingHost())
                .set(STARTING_TIME, toTimestamp(params.getStartingTime()))
                .set(VERSION, params.getVersion());
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
    }

    @Nullable
    @Override
    public SchedulingParams tryUpdate(SchedulingParams task) {
        Query query = DSL().update(TIMETABLE_TABLE)
                .set(TYPE, task.getType().name())
                .set(PARAM, task.getParam())
                .set(LAST_RUN_TIME, toTimestamp(task.getLastRunTime()))
                .set(STARTING_HOST, task.getStartingHost())
                .set(STARTING_TIME, toTimestamp(task.getStartingTime()))
                .set(VERSION, task.getVersion() + 1)
                .where(TASK_ID.eq(task.getTaskId())).and(VERSION.eq(task.getVersion()));
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
        return get(task.getTaskId());
    }

    @Nullable
    private SchedulingParams get(String taskId) {
        try {
            Query query = DSL().select().from(TIMETABLE_TABLE).where(TASK_ID.eq(taskId));
            return jdbcOperations.queryForObject(query.getSQL(), ROW_MAPPER, query.getBindValues().toArray());
        } catch (IncorrectResultSizeDataAccessException ignore) {
            return null;
        }
    }

    @Override
    public void tryUpdateLastRunTime(String taskId, Instant lastRunTime) {
        Query query = DSL().update(TIMETABLE_TABLE).set(LAST_RUN_TIME, toTimestamp(lastRunTime))
                .where(TASK_ID.eq(taskId)).and(LAST_RUN_TIME.lessThan(toTimestamp(lastRunTime)));
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
    }

    @Override
    public void removeTask(String taskId) {
        Query query = DSL().deleteFrom(TIMETABLE_TABLE).where(TASK_ID.eq(taskId));
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
    }

    @Override
    public void removeTasks(Collection<String> taskIds) {
        Iterator<String> iterator = taskIds.iterator();
        Query query = DSL().deleteFrom(TIMETABLE_TABLE).where(TASK_ID.eq((String) null));
        jdbcOperations.batchUpdate(query.getSQL(), new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, iterator.next());
            }

            @Override
            public int getBatchSize() {
                return taskIds.size();
            }
        });
    }

    protected abstract DSLContext DSL();

    /* Setters */
    @Required
    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }
}
