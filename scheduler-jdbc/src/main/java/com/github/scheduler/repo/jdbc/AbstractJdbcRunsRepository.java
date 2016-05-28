package com.github.scheduler.repo.jdbc;

import com.github.scheduler.model.EngineRequirementsImpl;
import com.github.scheduler.model.Run;
import com.github.scheduler.model.RunImpl;
import com.github.scheduler.repo.RunsRepository;
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

import static com.github.scheduler.repo.jdbc.SQLSchema.*;
import static com.github.scheduler.repo.jdbc.util.SqlDateUtils.toInstant;
import static com.github.scheduler.repo.jdbc.util.SqlDateUtils.toTimestamp;
import static java.util.stream.Collectors.toList;

/**
 * @author titov.artem.u@yandex.com on 02.11.15.
 */
public abstract class AbstractJdbcRunsRepository implements RunsRepository {

    protected static final RowMapper<Run> ROW_MAPPER = (rs, i) ->
            RunImpl.builder(
                    rs.getLong(RUN_ID.getName()),
                    rs.getString(TASK_ID.getName()),
                    new EngineRequirementsImpl(
                            rs.getInt(WEIGHT.getName()),
                            rs.getString(EXECUTOR.getName()),
                            rs.getString(SERVICE.getName())
                    ),
                    Run.Status.valueOf(rs.getString(STATUS.getName())),
                    toInstant(rs.getTimestamp(QUEUED_TIME.getName()))
            )
                    .withAcquiredTime(toInstant(rs.getTimestamp(ACQUIRED_TIME.getName())))
                    .withHost(rs.getString(HOST.getName()))
                    .withStartTime(toInstant(rs.getTimestamp(START_TIME.getName())))
                    .withPingTime(toInstant(rs.getTimestamp(PING_TIME.getName())))
                    .withEndTime(toInstant(rs.getTimestamp(END_TIME.getName())))
                    .withMessage(rs.getString(MESSAGE.getName()))
                    .withVersion(rs.getInt(VERSION.getName()))
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
            Query query = DSL().select().from(getRunsTable()).where(RUN_ID.eq(runId));
            return jdbcOperations.queryForObject(query.getSQL(), ROW_MAPPER, query.getBindValues().toArray());
        } catch (IncorrectResultSizeDataAccessException ignore) {
            return null;
        }
    }

    @Override
    public List<Run> getRuns() {
        return jdbcOperations.query(DSL().select().from(getRunsTable()).getSQL(), ROW_MAPPER);
    }

    @Override
    public List<Run> get(String taskId) {
        Query query = DSL().select()
                .from(getRunsTable())
                .where(TASK_ID.eq(taskId));
        return jdbcOperations.query(query.getSQL(), ROW_MAPPER, query.getBindValues().toArray());
    }

    @Override
    public Multimap<String, Run> getRuns(Collection<String> taskIds) {
        ListMultimap<String, Run> out = ArrayListMultimap.create();
        for (List<String> chunk : Iterables.partition(taskIds, getMaxInSize())) {
            Query query = DSL().select().from(getRunsTable()).where(TASK_ID.in(chunk));
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
                .set(TASK_ID, run.getTaskId())
                .set(WEIGHT, run.getEngineRequirements().getWeight())
                .set(EXECUTOR, run.getEngineRequirements().getExecutor())
                .set(SERVICE, run.getEngineRequirements().getService())
                .set(STATUS, run.getStatus().name())
                .set(QUEUED_TIME, toTimestamp(run.getQueuedTime()))
                .set(ACQUIRED_TIME, toTimestamp(run.getAcquiredTime()))
                .set(HOST, run.getHost())
                .set(START_TIME, toTimestamp(run.getStartTime()))
                .set(PING_TIME, toTimestamp(run.getPingTime()))
                .set(END_TIME, toTimestamp(run.getEndTime()))
                .set(MESSAGE, run.getMessage())
                .set(VERSION, run.getVersion());
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(query.getSQL(),
                Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.TIMESTAMP, Types.TIMESTAMP, Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP,
                Types.VARCHAR, Types.INTEGER);
        pscf.setReturnGeneratedKeys(true);
        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(query.getBindValues());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(psc, keyHolder);
        long runId = getKey(keyHolder, RUN_ID.getName()).longValue();
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
                    .set(RUN_ID, run.getRunId())
                    .set(TASK_ID, run.getTaskId())
                    .set(WEIGHT, run.getEngineRequirements().getWeight())
                    .set(EXECUTOR, run.getEngineRequirements().getExecutor())
                    .set(SERVICE, run.getEngineRequirements().getService())
                    .set(STATUS, run.getStatus().name())
                    .set(QUEUED_TIME, toTimestamp(run.getQueuedTime()))
                    .set(ACQUIRED_TIME, toTimestamp(run.getAcquiredTime()))
                    .set(HOST, run.getHost())
                    .set(START_TIME, toTimestamp(run.getStartTime()))
                    .set(PING_TIME, toTimestamp(run.getPingTime()))
                    .set(END_TIME, toTimestamp(run.getEndTime()))
                    .set(MESSAGE, run.getMessage())
                    .set(VERSION, run.getVersion())
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
                .set(TASK_ID, run.getTaskId())
                .set(WEIGHT, run.getEngineRequirements().getWeight())
                .set(EXECUTOR, run.getEngineRequirements().getExecutor())
                .set(SERVICE, run.getEngineRequirements().getService())
                .set(STATUS, run.getStatus().name())
                .set(QUEUED_TIME, toTimestamp(run.getQueuedTime()))
                .set(ACQUIRED_TIME, toTimestamp(run.getAcquiredTime()))
                .set(HOST, run.getHost())
                .set(START_TIME, toTimestamp(run.getStartTime()))
                .set(PING_TIME, toTimestamp(run.getPingTime()))
                .set(END_TIME, toTimestamp(run.getEndTime()))
                .set(MESSAGE, run.getMessage())
                .set(VERSION, run.getVersion() + 1)
                .where(RUN_ID.eq(run.getRunId()))
                .and(VERSION.eq(run.getVersion()));
        jdbcOperations.update(query.getSQL(), query.getBindValues().toArray());
        return getInternal(run.getRunId());
    }

    @Override
    public void remove(Collection<Run> runs) {
        List<Object[]> params = runs.stream().map(r -> new Object[]{r.getRunId()}).collect(toList());
        String sql = DSL().delete(getRunsTable()).where(RUN_ID.eq((Long) null)).getSQL();
        jdbcOperations.batchUpdate(sql, params);
    }

    protected abstract DSLContext DSL();

    protected abstract int getMaxInSize();

    protected abstract Table<Record> getRunsTable();
}
