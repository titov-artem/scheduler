package com.github.sc.scheduler.jdbc.repo;

import com.github.sc.scheduler.core.model.Task;
import com.github.sc.scheduler.core.model.TaskImpl;
import com.github.sc.scheduler.core.repo.TaskRepository;
import com.github.sc.scheduler.jdbc.utils.SqlDateUtils;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
public abstract class AbstractJdbcTaskRepository implements TaskRepository {

    public static final RowMapper<Task> ROW_MAPPER = (rs, i) ->
            TaskImpl.builder(rs.getString(SQLSchema.TASK_ID.getName()))
                    .withLastRunTime(SqlDateUtils.toInstant(rs.getTimestamp(SQLSchema.LAST_RUN_TIME.getName())))
                    .withStartingHost(rs.getString(SQLSchema.STARTING_HOST.getName()))
                    .withStartingTime(SqlDateUtils.toInstant(rs.getTimestamp(SQLSchema.STARTING_TIME.getName())))
                    .withVersion(rs.getInt(SQLSchema.VERSION.getName()))
                    .build();

    private JdbcOperations jdbcOperations;

    @Override
    public List<Task> getAll() {
        Query query = DSL().select().from(SQLSchema.TASK_TABLE);
        return jdbcOperations.query(query.getSQL(), ROW_MAPPER);
    }

    @Override
    public Optional<Task> get(String taskId) {
        return Optional.ofNullable(getInternal(taskId));
    }

    @Override
    public void save(Task task) {
        Query query = DSL().insertInto(SQLSchema.TASK_TABLE)
                .set(SQLSchema.TASK_ID, task.getId())
                .set(SQLSchema.LAST_RUN_TIME, SqlDateUtils.toTimestamp(task.getLastRunTime()))
                .set(SQLSchema.STARTING_HOST, task.getStartingHost())
                .set(SQLSchema.STARTING_TIME, SqlDateUtils.toTimestamp(task.getStartingTime()))
                .set(SQLSchema.VERSION, task.getVersion());
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
    }

    @Override
    public void remove(String taskId) {
        Query query = DSL().delete(SQLSchema.TASK_TABLE).where(SQLSchema.TASK_ID.eq(taskId));
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
    }

    @Nullable
    @Override
    public Task tryUpdate(Task task) {
        Query query = DSL().update(SQLSchema.TASK_TABLE)
                .set(SQLSchema.LAST_RUN_TIME, SqlDateUtils.toTimestamp(task.getLastRunTime()))
                .set(SQLSchema.STARTING_HOST, task.getStartingHost())
                .set(SQLSchema.STARTING_TIME, SqlDateUtils.toTimestamp(task.getStartingTime()))
                .set(SQLSchema.VERSION, task.getVersion() + 1)
                .where(SQLSchema.TASK_ID.eq(task.getId())).and(SQLSchema.VERSION.eq(task.getVersion()));
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
        return getInternal(task.getId());
    }

    @Override
    public void tryUpdateLastRunTime(String taskId, Instant lastRunTime) {
        Query query = DSL().update(SQLSchema.TASK_TABLE).set(SQLSchema.LAST_RUN_TIME, SqlDateUtils.toTimestamp(lastRunTime))
                .where(SQLSchema.TASK_ID.eq(taskId)).and(SQLSchema.LAST_RUN_TIME.lessThan(SqlDateUtils.toTimestamp(lastRunTime)));
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
    }

    @Nullable
    private Task getInternal(String taskId) {
        try {
            Query query = DSL().select().from(SQLSchema.TASK_TABLE).where(SQLSchema.TASK_ID.eq(taskId));
            return jdbcOperations.queryForObject(query.getSQL(), ROW_MAPPER, query.getBindValues().toArray());
        } catch (IncorrectResultSizeDataAccessException ignore) {
            return null;
        }
    }

    protected abstract DSLContext DSL();

    /* Setters */
    @Required
    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }
}
