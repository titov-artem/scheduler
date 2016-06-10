package com.github.sc.scheduler.jdbc.repo.postgresql;

import com.github.sc.scheduler.core.repo.HistoryRunsRepository;
import com.github.sc.scheduler.jdbc.repo.AbstractJdbcHistoryRunsRepositoryImpl;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class Postgresql9_5JdbcHistoryRunsRepository extends AbstractJdbcHistoryRunsRepositoryImpl implements HistoryRunsRepository {
    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.POSTGRES_9_5);
    }

    @Override
    protected int getMaxInSize() {
        return 30000;
    }
}
