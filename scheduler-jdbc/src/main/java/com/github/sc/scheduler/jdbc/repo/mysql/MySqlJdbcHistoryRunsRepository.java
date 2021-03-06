package com.github.sc.scheduler.jdbc.repo.mysql;

import com.github.sc.scheduler.core.repo.HistoryRunsRepository;
import com.github.sc.scheduler.jdbc.repo.AbstractJdbcHistoryRunsRepositoryImpl;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * @author Artem Titov titov.artem.u@yandex.com
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
