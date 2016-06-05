package com.github.sc.scheduler.repo.jdbc.postgresql;

import com.github.sc.scheduler.repo.jdbc.AbstractJdbcTaskRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class Postgresql9_5JdbcTaskRepository extends AbstractJdbcTaskRepository {
    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.POSTGRES_9_5);
    }
}
