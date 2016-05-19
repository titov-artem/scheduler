package ru.yandex.qe.common.scheduler.repo.jdbc;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.qe.common.scheduler.utils.IdGenerator;
import ru.yandex.qe.common.scheduler.model.EngineRequirementsImpl;
import ru.yandex.qe.common.scheduler.model.Task;
import ru.yandex.qe.common.scheduler.model.TaskImpl;
import ru.yandex.qe.common.scheduler.repo.TaskRepository;

import java.util.Optional;

import static ru.yandex.qe.common.scheduler.repo.jdbc.SQLSchema.*;

public abstract class AbstractJdbcTaskRepository implements TaskRepository {

    public static final RowMapper<TaskImpl> ROW_MAPPER = (rs, i) ->
            new TaskImpl(
                    rs.getString(TASK_ID.getName()),
                    Optional.ofNullable(rs.getString(NAME.getName())),
                    new EngineRequirementsImpl(
                            rs.getInt(WEIGHT.getName()),
                            rs.getString(EXECUTOR.getName()),
                            rs.getString(SERVICE.getName())
                    )
            );

    private JdbcOperations jdbcOperations;

    @Override
    public Optional<Task> get(String taskId) {
        Query query = DSL().select().from(TASK_TABLE).where(TASK_ID.eq(taskId));
        try {
            return Optional.of(jdbcOperations.queryForObject(query.getSQL(), ROW_MAPPER, query.getBindValues().toArray()));
        } catch (IncorrectResultSizeDataAccessException ignore) {
            return Optional.empty();
        }
    }

    @Override
    public Task create(Task task) {
        String taskId = IdGenerator.nextId();
        Query query = DSL().insertInto(TASK_TABLE)
                .set(TASK_ID, taskId)
                .set(NAME, task.getName().orElse(null))
                .set(WEIGHT, task.getEngineRequirements().getWeight())
                .set(EXECUTOR, task.getEngineRequirements().getExecutor())
                .set(SERVICE, task.getEngineRequirements().getService());
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
        return new TaskImpl(taskId, task.getName(), task.getEngineRequirements());
    }

    protected abstract DSLContext DSL();

    /* Setters */
    @Required
    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }
}
