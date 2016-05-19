package ru.yandex.qe.common.scheduler.repo.jdbc.postgresql;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import ru.yandex.qe.common.scheduler.repo.jdbc.AbstractJdbcActiveRunsRepository;

/**
 * @author titov.artem.u@yandex.com on 02.11.15.
 */
public class Postgresql9_4JdbcActiveRunsRepository extends AbstractJdbcActiveRunsRepository {
    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.POSTGRES_9_4);
    }

    @Override
    protected int getMaxInSize() {
        return 30000;
    }
}
