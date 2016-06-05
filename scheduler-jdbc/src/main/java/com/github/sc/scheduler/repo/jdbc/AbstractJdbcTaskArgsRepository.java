package com.github.sc.scheduler.repo.jdbc;

import com.github.sc.scheduler.model.TaskArgs;
import com.github.sc.scheduler.model.TaskArgsImpl;
import com.github.sc.scheduler.repo.TaskArgsRepository;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractJdbcTaskArgsRepository implements TaskArgsRepository {

    private JdbcOperations jdbcOperations;

    @Override
    public Optional<TaskArgs> get(String taskId) {
        Query query = DSL().select().from(TASK_ARGS_TABLE).where(TASK_ID.eq(taskId));
        TaskArgsImpl.Builder builder = TaskArgsImpl.builder();
        jdbcOperations.query(query.getSQL(),
                rs -> {
                    builder.append(rs.getString(NAME.getName()), rs.getString(VALUE.getName()));
                },
                query.getBindValues().toArray());
        return builder.isEmpty() ? Optional.empty() : Optional.of(builder.build());
    }

    @Override
    public void save(String taskId, TaskArgs taskArgs) {
        List<Object[]> params = new ArrayList<>();
        String sql = null;
        for (final String name : taskArgs.getNames()) {
            for (String value : taskArgs.getAll(name)) {
                Query query = DSL().insertInto(TASK_ARGS_TABLE)
                        .set(TASK_ID, taskId)
                        .set(NAME, name)
                        .set(VALUE, value);
                params.add(query.getBindValues().toArray());
                sql = query.getSQL();
            }
        }
        if (sql != null) {
            jdbcOperations.batchUpdate(sql, params);
        }
    }

    protected abstract DSLContext DSL();

    /* Setters */
    @Required
    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }
}
