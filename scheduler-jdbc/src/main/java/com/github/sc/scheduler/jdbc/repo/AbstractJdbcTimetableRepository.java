package com.github.sc.scheduler.jdbc.repo;

import com.github.sc.scheduler.core.model.EngineRequirementsImpl;
import com.github.sc.scheduler.core.model.SchedulingParams;
import com.github.sc.scheduler.core.model.SchedulingParamsImpl;
import com.github.sc.scheduler.core.model.SchedulingType;
import com.github.sc.scheduler.core.repo.TimetableRepository;
import com.github.sc.scheduler.core.utils.IdGenerator;
import com.google.common.collect.Iterables;
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
import java.util.*;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
public abstract class AbstractJdbcTimetableRepository implements TimetableRepository {

    private static final RowMapper<SchedulingParams> ROW_MAPPER = (rs, i) ->
            new SchedulingParamsImpl.Builder(
                    rs.getString(SQLSchema.TASK_ID.getName()),
                    rs.getString(SQLSchema.NAME.getName()),
                    new EngineRequirementsImpl(
                            rs.getInt(SQLSchema.WEIGHT.getName()),
                            rs.getString(SQLSchema.EXECUTOR.getName()),
                            rs.getString(SQLSchema.SERVICE.getName())
                    ),
                    SchedulingType.valueOf(rs.getString(SQLSchema.TYPE.getName())),
                    rs.getString(SQLSchema.PARAM.getName()),
                    rs.getInt(SQLSchema.CONCURRENCY_LEVEL.getName()),
                    rs.getBoolean(SQLSchema.RESTART_ON_FAIL.getName()),
                    rs.getBoolean(SQLSchema.RESTART_ON_REBOOT.getName())
            ).build();


    private JdbcOperations jdbcOperations;

    @Override
    public List<SchedulingParams> getAll() {
        Query query = DSL().select().from(SQLSchema.TIMETABLE_TABLE);
        return jdbcOperations.query(query.getSQL(), ROW_MAPPER);
    }

    @Override
    public List<SchedulingParams> getAll(Collection<String> taskIds) {
        List<SchedulingParams> out = new ArrayList<>();
        for (List<String> chunk : Iterables.partition(new HashSet<>(taskIds), getMaxInSize())) {
            Query query = DSL().select().from(SQLSchema.TIMETABLE_TABLE).where(SQLSchema.TASK_ID.in(chunk));
            out.addAll(jdbcOperations.query(query.getSQL(), query.getBindValues().toArray(), ROW_MAPPER));
        }
        return out;
    }

    @Override
    public Optional<SchedulingParams> get(String taskId) {
        return Optional.ofNullable(getInternal(taskId));
    }

    @Override
    public SchedulingParams save(SchedulingParams params) {
        String taskId = IdGenerator.nextId();
        Query query = DSL().insertInto(SQLSchema.TIMETABLE_TABLE)
                .set(SQLSchema.TASK_ID, taskId)
                .set(SQLSchema.NAME, params.getName().orElse(null))
                .set(SQLSchema.WEIGHT, params.getEngineRequirements().getWeight())
                .set(SQLSchema.EXECUTOR, params.getEngineRequirements().getExecutor())
                .set(SQLSchema.SERVICE, params.getEngineRequirements().getService())
                .set(SQLSchema.TYPE, params.getType().name())
                .set(SQLSchema.PARAM, params.getParam())
                .set(SQLSchema.CONCURRENCY_LEVEL, params.getConcurrencyLevel())
                .set(SQLSchema.RESTART_ON_FAIL, params.isRestartOnFail())
                .set(SQLSchema.RESTART_ON_REBOOT, params.isRestartOnReboot());
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
        return SchedulingParamsImpl.builder(params).withTaskId(taskId).build();
    }

    @Nullable
    private SchedulingParams getInternal(String taskId) {
        try {
            Query query = DSL().select().from(SQLSchema.TIMETABLE_TABLE).where(SQLSchema.TASK_ID.eq(taskId));
            return jdbcOperations.queryForObject(query.getSQL(), ROW_MAPPER, query.getBindValues().toArray());
        } catch (IncorrectResultSizeDataAccessException ignore) {
            return null;
        }
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

    protected abstract int getMaxInSize();

    /* Setters */
    @Required
    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }
}
