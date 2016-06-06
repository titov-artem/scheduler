package com.github.sc.scheduler.jdbc.repo;

import com.github.sc.scheduler.core.model.EngineRequirementsImpl;
import com.github.sc.scheduler.core.model.Task;
import com.github.sc.scheduler.core.model.TaskImpl;
import com.github.sc.scheduler.core.repo.TaskRepository;
import com.github.sc.scheduler.core.utils.IdGenerator;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

public abstract class AbstractJdbcTaskRepository implements TaskRepository {

    public static final RowMapper<Task> ROW_MAPPER = (rs, i) ->
            new TaskImpl(
                    rs.getString(SQLSchema.TASK_ID.getName()),
                    Optional.ofNullable(rs.getString(SQLSchema.NAME.getName())),
                    new EngineRequirementsImpl(
                            rs.getInt(SQLSchema.WEIGHT.getName()),
                            rs.getString(SQLSchema.EXECUTOR.getName()),
                            rs.getString(SQLSchema.SERVICE.getName())
                    )
            );

    private JdbcOperations jdbcOperations;

    @Override
    public List<Task> getAll() {
        Query query = DSL().select().from(SQLSchema.TASK_TABLE);
        return jdbcOperations.query(query.getSQL(), ROW_MAPPER);
    }

    @Override
    public Optional<Task> get(String taskId) {
        Query query = DSL().select().from(SQLSchema.TASK_TABLE).where(SQLSchema.TASK_ID.eq(taskId));
        try {
            return Optional.of(jdbcOperations.queryForObject(query.getSQL(), ROW_MAPPER, query.getBindValues().toArray()));
        } catch (IncorrectResultSizeDataAccessException ignore) {
            return Optional.empty();
        }
    }

    @Override
    public Task create(Task task) {
        String taskId = IdGenerator.nextId();
        Query query = DSL().insertInto(SQLSchema.TASK_TABLE)
                .set(SQLSchema.TASK_ID, taskId)
                .set(SQLSchema.NAME, task.getName().orElse(null))
                .set(SQLSchema.WEIGHT, task.getEngineRequirements().getWeight())
                .set(SQLSchema.EXECUTOR, task.getEngineRequirements().getExecutor())
                .set(SQLSchema.SERVICE, task.getEngineRequirements().getService());
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
        return new TaskImpl(taskId, task.getName(), task.getEngineRequirements());
    }

    @Override
    public void remove(String taskId) {
        Query query = DSL().delete(SQLSchema.TASK_TABLE).where(SQLSchema.TASK_ID.eq(taskId));
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
    }

    protected abstract DSLContext DSL();

    /* Setters */
    @Required
    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }
}
