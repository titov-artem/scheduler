package com.github.sc.scheduler.jdbc.repo.postgresql;

import com.github.sc.scheduler.jdbc.repo.AbstractJdbcTimetableRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class Postgresql9_5JdbcTimetableRepository extends AbstractJdbcTimetableRepository {
    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.POSTGRES_9_5);
    }

    @Override
    protected int getMaxInSize() {
        return 30000;
    }
}
