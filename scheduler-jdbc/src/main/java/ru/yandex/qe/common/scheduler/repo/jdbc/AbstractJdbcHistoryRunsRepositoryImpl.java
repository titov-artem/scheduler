package ru.yandex.qe.common.scheduler.repo.jdbc;

import org.jooq.Record;
import org.jooq.Table;

import static ru.yandex.qe.common.scheduler.repo.jdbc.SQLSchema.HISTORY_RUNS_TABLE;

public abstract class AbstractJdbcHistoryRunsRepositoryImpl extends AbstractJdbcRunsRepository {
    @Override
    protected Table<Record> getRunsTable() {
        return HISTORY_RUNS_TABLE;
    }
}
