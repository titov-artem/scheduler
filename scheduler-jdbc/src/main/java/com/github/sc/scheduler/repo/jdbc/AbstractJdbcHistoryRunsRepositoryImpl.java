package com.github.sc.scheduler.repo.jdbc;

import org.jooq.Record;
import org.jooq.Table;

public abstract class AbstractJdbcHistoryRunsRepositoryImpl extends AbstractJdbcRunsRepository {
    @Override
    protected Table<Record> getRunsTable() {
        return SQLSchema.HISTORY_RUNS_TABLE;
    }
}
