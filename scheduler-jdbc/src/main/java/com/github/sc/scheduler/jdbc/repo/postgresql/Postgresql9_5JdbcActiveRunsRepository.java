package com.github.sc.scheduler.jdbc.repo.postgresql;

import com.github.sc.scheduler.jdbc.repo.AbstractJdbcActiveRunsRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class Postgresql9_5JdbcActiveRunsRepository extends AbstractJdbcActiveRunsRepository {
    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.POSTGRES_9_5);
    }

    @Override
    protected int getMaxInSize() {
        return 30000;
    }
}
