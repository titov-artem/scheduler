package com.github.scheduler.repo.jdbc.mysql;

import com.github.scheduler.repo.HistoryRunsRepository;
import com.github.scheduler.repo.jdbc.AbstractJdbcHistoryRunsRepositoryImpl;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * @author titov.artem.u@yandex.com on 02.11.15.
 */
public class MySqlJdbcHistoryRunsRepository extends AbstractJdbcHistoryRunsRepositoryImpl implements HistoryRunsRepository {

    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.MYSQL);
    }

    @Override
    protected int getMaxInSize() {
        return 1000;
    }
}
