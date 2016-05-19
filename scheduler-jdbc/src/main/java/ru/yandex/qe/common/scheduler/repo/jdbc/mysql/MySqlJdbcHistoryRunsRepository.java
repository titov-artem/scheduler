package ru.yandex.qe.common.scheduler.repo.jdbc.mysql;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import ru.yandex.qe.common.scheduler.repo.HistoryRunsRepository;
import ru.yandex.qe.common.scheduler.repo.jdbc.AbstractJdbcHistoryRunsRepositoryImpl;

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
