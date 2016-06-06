package com.github.sc.scheduler.jdbc.repo;

import com.github.sc.scheduler.core.model.TaskArgs;
import com.github.sc.scheduler.core.model.TaskArgsImpl;
import com.github.sc.scheduler.core.repo.TaskArgsRepository;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractJdbcTaskArgsRepository implements TaskArgsRepository {

    private JdbcOperations jdbcOperations;

    @Override
    public List<TaskArgs> getAll() {
        Query query = DSL().select().from(SQLSchema.TASK_ARGS_TABLE).orderBy(SQLSchema.TASK_ID);
        List<TaskArgs> out = new ArrayList<>();
        AtomicReference<TaskArgsImpl.Builder> curBuilder = new AtomicReference<>();
        jdbcOperations.query(query.getSQL(),
                rs -> {
                    String taskId = rs.getString(SQLSchema.TASK_ID.getName());
                    if (curBuilder.get() == null || !curBuilder.get().getTaskId().equals(taskId)) {
                        if (curBuilder.get() != null) {
                            out.add(curBuilder.get().build());
                        }
                        curBuilder.set(TaskArgsImpl.builder(taskId));
                    }
                    curBuilder.get().append(rs.getString(SQLSchema.NAME.getName()), rs.getString(SQLSchema.VALUE.getName()));
                });
        if (curBuilder.get() != null && !curBuilder.get().isEmpty()) {
            out.add(curBuilder.get().build());
        }
        return out;
    }

    @Override
    public Optional<TaskArgs> get(String taskId) {
        Query query = DSL().select().from(SQLSchema.TASK_ARGS_TABLE).where(SQLSchema.TASK_ID.eq(taskId));
        AtomicReference<TaskArgsImpl.Builder> builder = new AtomicReference<>();
        jdbcOperations.query(query.getSQL(),
                rs -> {
                    if (builder.get() == null) {
                        builder.set(TaskArgsImpl.builder(rs.getString(SQLSchema.TASK_ID.getName())));
                    }
                    builder.get().append(rs.getString(SQLSchema.NAME.getName()), rs.getString(SQLSchema.VALUE.getName()));
                },
                query.getBindValues().toArray());
        return builder.get() == null ? Optional.empty() : Optional.of(builder.get().build());
    }

    @Override
    public void save(String taskId, TaskArgs taskArgs) {
        List<Object[]> params = new ArrayList<>();
        String sql = null;
        for (final String name : taskArgs.getNames()) {
            for (String value : taskArgs.getAll(name)) {
                Query query = DSL().insertInto(SQLSchema.TASK_ARGS_TABLE)
                        .set(SQLSchema.TASK_ID, taskId)
                        .set(SQLSchema.NAME, name)
                        .set(SQLSchema.VALUE, value);
                params.add(query.getBindValues().toArray());
                sql = query.getSQL();
            }
        }
        if (sql != null) {
            jdbcOperations.batchUpdate(sql, params);
        }
    }

    @Override
    public void remove(String taskId) {
        Query query = DSL().delete(SQLSchema.TASK_ARGS_TABLE).where(SQLSchema.TASK_ID.eq(taskId));
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
    }

    protected abstract DSLContext DSL();

    /* Setters */
    @Required
    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }
}
