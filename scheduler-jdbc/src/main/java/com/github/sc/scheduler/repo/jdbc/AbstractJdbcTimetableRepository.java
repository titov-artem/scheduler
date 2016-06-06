package com.github.sc.scheduler.repo.jdbc;

import com.github.sc.scheduler.model.SchedulingParams;
import com.github.sc.scheduler.model.SchedulingParamsImpl;
import com.github.sc.scheduler.model.SchedulingType;
import com.github.sc.scheduler.repo.TimetableRepository;
import com.github.sc.scheduler.repo.jdbc.util.SqlDateUtils;
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

public abstract class AbstractJdbcTimetableRepository implements TimetableRepository {

    private static final RowMapper<SchedulingParams> ROW_MAPPER = (rs, i) ->
            SchedulingParamsImpl.builder(
                    rs.getString(SQLSchema.TASK_ID.getName()),
                    SchedulingType.valueOf(rs.getString(SQLSchema.TYPE.getName())),
                    rs.getString(SQLSchema.PARAM.getName())
            )
                    .withLastRunTime(SqlDateUtils.toInstant(rs.getTimestamp(SQLSchema.LAST_RUN_TIME.getName())))
                    .withStartingHost(rs.getString(SQLSchema.STARTING_HOST.getName()))
                    .withStartingTime(SqlDateUtils.toInstant(rs.getTimestamp(SQLSchema.STARTING_TIME.getName())))
                    .withVersion(rs.getInt(SQLSchema.VERSION.getName()))
                    .build();

    private JdbcOperations jdbcOperations;

    @Override
    public List<SchedulingParams> getAll() {
        Query query = DSL().select().from(SQLSchema.TIMETABLE_TABLE);
        return jdbcOperations.query(query.getSQL(), ROW_MAPPER);
    }

    @Override
    public Optional<SchedulingParams> getTask(String taskId) {
        return Optional.ofNullable(get(taskId));
    }

    @Override
    public void save(SchedulingParams params) {
        Query query = DSL().insertInto(SQLSchema.TIMETABLE_TABLE)
                .set(SQLSchema.TASK_ID, params.getTaskId())
                .set(SQLSchema.TYPE, params.getType().name())
                .set(SQLSchema.PARAM, params.getParam())
                .set(SQLSchema.LAST_RUN_TIME, SqlDateUtils.toTimestamp(params.getLastRunTime()))
                .set(SQLSchema.STARTING_HOST, params.getStartingHost())
                .set(SQLSchema.STARTING_TIME, SqlDateUtils.toTimestamp(params.getStartingTime()))
                .set(SQLSchema.VERSION, params.getVersion());
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
    }

    @Nullable
    @Override
    public SchedulingParams tryUpdate(SchedulingParams task) {
        Query query = DSL().update(SQLSchema.TIMETABLE_TABLE)
                .set(SQLSchema.TYPE, task.getType().name())
                .set(SQLSchema.PARAM, task.getParam())
                .set(SQLSchema.LAST_RUN_TIME, SqlDateUtils.toTimestamp(task.getLastRunTime()))
                .set(SQLSchema.STARTING_HOST, task.getStartingHost())
                .set(SQLSchema.STARTING_TIME, SqlDateUtils.toTimestamp(task.getStartingTime()))
                .set(SQLSchema.VERSION, task.getVersion() + 1)
                .where(SQLSchema.TASK_ID.eq(task.getTaskId())).and(SQLSchema.VERSION.eq(task.getVersion()));
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
        return get(task.getTaskId());
    }

    @Nullable
    private SchedulingParams get(String taskId) {
        try {
            Query query = DSL().select().from(SQLSchema.TIMETABLE_TABLE).where(SQLSchema.TASK_ID.eq(taskId));
            return jdbcOperations.queryForObject(query.getSQL(), ROW_MAPPER, query.getBindValues().toArray());
        } catch (IncorrectResultSizeDataAccessException ignore) {
            return null;
        }
    }

    @Override
    public void tryUpdateLastRunTime(String taskId, Instant lastRunTime) {
        Query query = DSL().update(SQLSchema.TIMETABLE_TABLE).set(SQLSchema.LAST_RUN_TIME, SqlDateUtils.toTimestamp(lastRunTime))
                .where(SQLSchema.TASK_ID.eq(taskId)).and(SQLSchema.LAST_RUN_TIME.lessThan(SqlDateUtils.toTimestamp(lastRunTime)));
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
    }

    @Override
    public void removeTask(String taskId) {
        Query query = DSL().deleteFrom(SQLSchema.TIMETABLE_TABLE).where(SQLSchema.TASK_ID.eq(taskId));
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
    }

    @Override
    public void removeTasks(Collection<String> taskIds) {
        Iterator<String> iterator = taskIds.iterator();
        Query query = DSL().deleteFrom(SQLSchema.TIMETABLE_TABLE).where(SQLSchema.TASK_ID.eq((String) null));
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
