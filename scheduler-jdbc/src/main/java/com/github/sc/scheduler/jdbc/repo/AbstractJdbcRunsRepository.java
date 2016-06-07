package com.github.sc.scheduler.jdbc.repo;

import com.github.sc.scheduler.core.model.EngineRequirementsImpl;
import com.github.sc.scheduler.core.model.Run;
import com.github.sc.scheduler.core.model.RunImpl;
import com.github.sc.scheduler.core.repo.RunsRepository;
import com.github.sc.scheduler.jdbc.utils.SqlDateUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nullable;
import java.sql.Types;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @author titov.artem.u@yandex.com on 02.11.15.
 */
public abstract class AbstractJdbcRunsRepository implements RunsRepository {

    protected static final RowMapper<Run> ROW_MAPPER = (rs, i) ->
            RunImpl.builder(
                    rs.getLong(SQLSchema.RUN_ID.getName()),
                    rs.getString(SQLSchema.TASK_ID.getName()),
                    new EngineRequirementsImpl(
                            rs.getInt(SQLSchema.WEIGHT.getName()),
                            rs.getString(SQLSchema.EXECUTOR.getName()),
                            rs.getString(SQLSchema.SERVICE.getName())
                    ),
                    rs.getBoolean(SQLSchema.RESTART_ON_FAIL.getName()),
                    rs.getBoolean(SQLSchema.RESTART_ON_REBOOT.getName()),
                    Run.Status.valueOf(rs.getString(SQLSchema.STATUS.getName())),
                    SqlDateUtils.toInstant(rs.getTimestamp(SQLSchema.QUEUED_TIME.getName()))
            )
                    .withAcquiredTime(SqlDateUtils.toInstant(rs.getTimestamp(SQLSchema.ACQUIRED_TIME.getName())))
                    .withHost(rs.getString(SQLSchema.HOST.getName()))
                    .withStartTime(SqlDateUtils.toInstant(rs.getTimestamp(SQLSchema.START_TIME.getName())))
                    .withPingTime(SqlDateUtils.toInstant(rs.getTimestamp(SQLSchema.PING_TIME.getName())))
                    .withEndTime(SqlDateUtils.toInstant(rs.getTimestamp(SQLSchema.END_TIME.getName())))
                    .withMessage(rs.getString(SQLSchema.MESSAGE.getName()))
                    .withVersion(rs.getInt(SQLSchema.VERSION.getName()))
                    .build();


    private JdbcOperations jdbcOperations;

    protected final JdbcOperations getJdbcOperations() {
        return jdbcOperations;
    }

    /* Setters */
    @Required
    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public Optional<Run> get(long runId) {
        return Optional.ofNullable(getInternal(runId));
    }

    @Nullable
    private Run getInternal(long runId) {
        try {
            Query query = DSL().select().from(getRunsTable()).where(SQLSchema.RUN_ID.eq(runId));
            return jdbcOperations.queryForObject(query.getSQL(), ROW_MAPPER, query.getBindValues().toArray());
        } catch (IncorrectResultSizeDataAccessException ignore) {
            return null;
        }
    }

    @Override
    public List<Run> getAll() {
        return jdbcOperations.query(DSL().select().from(getRunsTable()).getSQL(), ROW_MAPPER);
    }

    @Override
    public List<Run> get(String taskId) {
        Query query = DSL().select()
                .from(getRunsTable())
                .where(SQLSchema.TASK_ID.eq(taskId));
        return jdbcOperations.query(query.getSQL(), ROW_MAPPER, query.getBindValues().toArray());
    }

    @Override
    public Multimap<String, Run> getRuns(Collection<String> taskIds) {
        ListMultimap<String, Run> out = ArrayListMultimap.create();
        for (List<String> chunk : Iterables.partition(taskIds, getMaxInSize())) {
            Query query = DSL().select().from(getRunsTable()).where(SQLSchema.TASK_ID.in(chunk));
            jdbcOperations.query(query.getSQL(),
                    rs -> {
                        Run run = ROW_MAPPER.mapRow(rs, 1);
                        out.put(run.getTaskId(), run);
                    },
                    query.getBindValues().toArray());
        }
        return out;
    }

    @Override
    public Run create(Run run) {
        Query query = DSL().insertInto(getRunsTable())
                .set(SQLSchema.TASK_ID, run.getTaskId())
                .set(SQLSchema.WEIGHT, run.getEngineRequirements().getWeight())
                .set(SQLSchema.EXECUTOR, run.getEngineRequirements().getExecutor())
                .set(SQLSchema.SERVICE, run.getEngineRequirements().getService())
                .set(SQLSchema.STATUS, run.getStatus().name())
                .set(SQLSchema.QUEUED_TIME, SqlDateUtils.toTimestamp(run.getQueuedTime()))
                .set(SQLSchema.ACQUIRED_TIME, SqlDateUtils.toTimestamp(run.getAcquiredTime()))
                .set(SQLSchema.HOST, run.getHost())
                .set(SQLSchema.START_TIME, SqlDateUtils.toTimestamp(run.getStartTime()))
                .set(SQLSchema.PING_TIME, SqlDateUtils.toTimestamp(run.getPingTime()))
                .set(SQLSchema.END_TIME, SqlDateUtils.toTimestamp(run.getEndTime()))
                .set(SQLSchema.RESTART_ON_FAIL, run.isRestartOnFail())
                .set(SQLSchema.RESTART_ON_REBOOT, run.isRestartOnReboot())
                .set(SQLSchema.MESSAGE, run.getMessage())
                .set(SQLSchema.VERSION, run.getVersion());
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(query.getSQL(),
                Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.TIMESTAMP, Types.TIMESTAMP, Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP,
                Types.BOOLEAN, Types.BOOLEAN,
                Types.VARCHAR, Types.INTEGER);
        pscf.setReturnGeneratedKeys(true);
        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(query.getBindValues());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(psc, keyHolder);
        long runId = getKey(keyHolder, SQLSchema.RUN_ID.getName()).longValue();
        return RunImpl.builder(run).withRunId(runId).build();
    }

    private Number getKey(KeyHolder keyHolder, String keyName) {
        Map<String, Object> keys = keyHolder.getKeys();
        if (keys.size() == 1) return keyHolder.getKey();
        return (Number) keys.get(keyName);
    }

    @Override
    public void createIfNotExists(Collection<Run> runs) {
        List<Object[]> params = new ArrayList<>(runs.size());
        String sql = null;
        for (final Run run : runs) {
            Query query = DSL().insertInto(getRunsTable())
                    .set(SQLSchema.RUN_ID, run.getRunId())
                    .set(SQLSchema.TASK_ID, run.getTaskId())
                    .set(SQLSchema.WEIGHT, run.getEngineRequirements().getWeight())
                    .set(SQLSchema.EXECUTOR, run.getEngineRequirements().getExecutor())
                    .set(SQLSchema.SERVICE, run.getEngineRequirements().getService())
                    .set(SQLSchema.STATUS, run.getStatus().name())
                    .set(SQLSchema.QUEUED_TIME, SqlDateUtils.toTimestamp(run.getQueuedTime()))
                    .set(SQLSchema.ACQUIRED_TIME, SqlDateUtils.toTimestamp(run.getAcquiredTime()))
                    .set(SQLSchema.HOST, run.getHost())
                    .set(SQLSchema.START_TIME, SqlDateUtils.toTimestamp(run.getStartTime()))
                    .set(SQLSchema.PING_TIME, SqlDateUtils.toTimestamp(run.getPingTime()))
                    .set(SQLSchema.END_TIME, SqlDateUtils.toTimestamp(run.getEndTime()))
                    .set(SQLSchema.RESTART_ON_FAIL, run.isRestartOnFail())
                    .set(SQLSchema.RESTART_ON_REBOOT, run.isRestartOnReboot())
                    .set(SQLSchema.MESSAGE, run.getMessage())
                    .set(SQLSchema.VERSION, run.getVersion())
                    .onDuplicateKeyIgnore();

            params.add(query.getBindValues().toArray());
            sql = query.getSQL();
        }
        if (sql != null) {
            jdbcOperations.batchUpdate(sql, params);
        }
    }

    @Nullable
    @Override
    public Run tryUpdate(Run run) {
        Query query = DSL().update(getRunsTable())
                .set(SQLSchema.TASK_ID, run.getTaskId())
                .set(SQLSchema.WEIGHT, run.getEngineRequirements().getWeight())
                .set(SQLSchema.EXECUTOR, run.getEngineRequirements().getExecutor())
                .set(SQLSchema.SERVICE, run.getEngineRequirements().getService())
                .set(SQLSchema.STATUS, run.getStatus().name())
                .set(SQLSchema.QUEUED_TIME, SqlDateUtils.toTimestamp(run.getQueuedTime()))
                .set(SQLSchema.ACQUIRED_TIME, SqlDateUtils.toTimestamp(run.getAcquiredTime()))
                .set(SQLSchema.HOST, run.getHost())
                .set(SQLSchema.START_TIME, SqlDateUtils.toTimestamp(run.getStartTime()))
                .set(SQLSchema.PING_TIME, SqlDateUtils.toTimestamp(run.getPingTime()))
                .set(SQLSchema.END_TIME, SqlDateUtils.toTimestamp(run.getEndTime()))
                .set(SQLSchema.RESTART_ON_FAIL, run.isRestartOnFail())
                .set(SQLSchema.RESTART_ON_REBOOT, run.isRestartOnReboot())
                .set(SQLSchema.MESSAGE, run.getMessage())
                .set(SQLSchema.VERSION, run.getVersion() + 1)
                .where(SQLSchema.RUN_ID.eq(run.getRunId()))
                .and(SQLSchema.VERSION.eq(run.getVersion()));
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
        return getInternal(run.getRunId());
    }

    @Override
    public void remove(Collection<Run> runs) {
        List<Object[]> params = runs.stream().map(r -> new Object[]{r.getRunId()}).collect(toList());
        String sql = DSL().delete(getRunsTable()).where(SQLSchema.RUN_ID.eq((Long) null)).getSQL();
        jdbcOperations.batchUpdate(sql, params);
    }

    protected abstract DSLContext DSL();

    protected abstract int getMaxInSize();

    protected abstract Table<Record> getRunsTable();
}
