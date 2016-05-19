package ru.yandex.qe.common.scheduler.repo.jdbc;

import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Table;
import ru.yandex.qe.common.scheduler.model.Run;
import ru.yandex.qe.common.scheduler.repo.ActiveRunsRepository;

import java.time.Instant;
import java.util.List;

import static ru.yandex.qe.common.scheduler.repo.jdbc.SQLSchema.PING_TIME;
import static ru.yandex.qe.common.scheduler.repo.jdbc.SQLSchema.RUNS_TABLE;
import static ru.yandex.qe.common.scheduler.repo.jdbc.SQLSchema.RUN_ID;
import static ru.yandex.qe.common.scheduler.repo.jdbc.util.SqlDateUtils.toTimestamp;

public abstract class AbstractJdbcActiveRunsRepository extends AbstractJdbcRunsRepository implements ActiveRunsRepository {

    @Override
    public List<Run> getRuns() {
        return getJdbcOperations().query(DSL().select().from(RUNS_TABLE).orderBy(RUN_ID.asc()).getSQL(), ROW_MAPPER);
    }

    @Override
    public void ping(long runId, Instant pingTime) {
        Query query = DSL().update(RUNS_TABLE).set(PING_TIME, toTimestamp(pingTime));
        getJdbcOperations().update(query.getSQL(), query.getBindValues().toArray());
    }

    @Override
    protected Table<Record> getRunsTable() {
        return RUNS_TABLE;
    }
}
