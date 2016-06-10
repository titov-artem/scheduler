package com.github.sc.scheduler.jdbc.repo;

import com.github.sc.scheduler.core.model.Run;
import com.github.sc.scheduler.core.model.RunImpl;
import com.github.sc.scheduler.core.repo.ActiveRunsRepository;
import com.github.sc.scheduler.core.utils.TransactionSupport;
import com.github.sc.scheduler.jdbc.utils.SqlDateUtils;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJdbcActiveRunsRepository extends AbstractJdbcRunsRepository implements ActiveRunsRepository {

    private TransactionSupport transactionSupport;

    @Override
    public List<Run> getAll() {
        return getJdbcOperations().query(DSL().select().from(getRunsTable()).orderBy(SQLSchema.RUN_ID.asc()).getSQL(), ROW_MAPPER);
    }

    @Override
    public void ping(long runId, Instant pingTime) {
        Query query = DSL().update(SQLSchema.RUNS_TABLE).set(SQLSchema.PING_TIME, SqlDateUtils.toTimestamp(pingTime));
        getJdbcOperations().update(query.getSQL(), query.getBindValues().toArray());
    }

    @Override
    public List<Run> create(Run run, int count, int concurrencyLevel) {
        Query query = DSL().insertInto(getRunsTable(),
                SQLSchema.TASK_ID,
                SQLSchema.WEIGHT,
                SQLSchema.EXECUTOR,
                SQLSchema.SERVICE,
                SQLSchema.STATUS,
                SQLSchema.QUEUED_TIME,
                SQLSchema.ACQUIRED_TIME,
                SQLSchema.HOST,
                SQLSchema.START_TIME,
                SQLSchema.PING_TIME,
                SQLSchema.END_TIME,
                SQLSchema.RESTART_ON_FAIL,
                SQLSchema.RESTART_ON_REBOOT,
                SQLSchema.MESSAGE,
                SQLSchema.VERSION
        ).select(DSL().select(
                DSL.val(run.getTaskId()).as(SQLSchema.TASK_ID),
                DSL.val(run.getEngineRequirements().getWeight()).as(SQLSchema.WEIGHT),
                DSL.val(run.getEngineRequirements().getExecutor()).as(SQLSchema.EXECUTOR),
                DSL.val(run.getEngineRequirements().getService()).as(SQLSchema.SERVICE),
                DSL.val(run.getStatus().name()).as(SQLSchema.STATUS),
                DSL.val(SqlDateUtils.toTimestamp(run.getQueuedTime())).as(SQLSchema.QUEUED_TIME),
                DSL.val(SqlDateUtils.toTimestamp(run.getAcquiredTime())).as(SQLSchema.ACQUIRED_TIME),
                DSL.val(run.getHost()).as(SQLSchema.HOST),
                DSL.val(SqlDateUtils.toTimestamp(run.getStartTime())).as(SQLSchema.START_TIME),
                DSL.val(SqlDateUtils.toTimestamp(run.getPingTime())).as(SQLSchema.PING_TIME),
                DSL.val(SqlDateUtils.toTimestamp(run.getEndTime())).as(SQLSchema.END_TIME),
                DSL.val(run.isRestartOnFail()).as(SQLSchema.RESTART_ON_FAIL),
                DSL.val(run.isRestartOnReboot()).as(SQLSchema.RESTART_ON_REBOOT),
                DSL.val(run.getMessage()).as(SQLSchema.MESSAGE),
                DSL.val(run.getVersion()).as(SQLSchema.VERSION)
                ).where(
                DSL().selectCount().from(getRunsTable()).where(SQLSchema.TASK_ID.eq(run.getTaskId())).asField().lt(concurrencyLevel)
                )
        );
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(query.getSQL(),
                Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.TIMESTAMP, Types.TIMESTAMP, Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP,
                Types.BOOLEAN, Types.BOOLEAN,
                Types.VARCHAR, Types.INTEGER,
                Types.VARCHAR, Types.INTEGER);
        pscf.setReturnGeneratedKeys(true);
        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(query.getBindValues());
        List<Run> out = new ArrayList<>();
        // todo rewrite with db sequence on batch insert
        transactionSupport.doInTransaction(() -> {
            for (int i = 0; i < count; i++) {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                getJdbcOperations().update(psc, keyHolder);
                long runId = getKey(keyHolder, SQLSchema.RUN_ID.getName()).longValue();
                out.add(RunImpl.builder(run).withRunId(runId).build());
            }
        });
        return out;
    }

    @Override
    protected Table<Record> getRunsTable() {
        return SQLSchema.RUNS_TABLE;
    }

    @Required
    public void setTransactionSupport(TransactionSupport transactionSupport) {
        this.transactionSupport = transactionSupport;
    }
}
