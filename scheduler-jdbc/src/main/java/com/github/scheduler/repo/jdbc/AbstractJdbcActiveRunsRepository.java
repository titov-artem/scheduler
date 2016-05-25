package com.github.scheduler.repo.jdbc;

import com.github.scheduler.model.Run;
import com.github.scheduler.repo.ActiveRunsRepository;
import com.github.scheduler.repo.jdbc.util.SqlDateUtils;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Table;

import java.time.Instant;
import java.util.List;

public abstract class AbstractJdbcActiveRunsRepository extends AbstractJdbcRunsRepository implements ActiveRunsRepository {

    @Override
    public List<Run> getRuns() {
        return getJdbcOperations().query(DSL().select().from(SQLSchema.RUNS_TABLE).orderBy(SQLSchema.RUN_ID.asc()).getSQL(), ROW_MAPPER);
    }

    @Override
    public void ping(long runId, Instant pingTime) {
        Query query = DSL().update(SQLSchema.RUNS_TABLE).set(SQLSchema.PING_TIME, SqlDateUtils.toTimestamp(pingTime));
        getJdbcOperations().update(query.getSQL(), query.getBindValues().toArray());
    }

    @Override
    protected Table<Record> getRunsTable() {
        return SQLSchema.RUNS_TABLE;
    }
}
