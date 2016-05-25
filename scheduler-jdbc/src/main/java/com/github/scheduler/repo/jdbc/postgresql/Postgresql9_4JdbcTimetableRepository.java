package com.github.scheduler.repo.jdbc.postgresql;

import com.github.scheduler.repo.jdbc.AbstractJdbcTimetableRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class Postgresql9_4JdbcTimetableRepository extends AbstractJdbcTimetableRepository {
    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.POSTGRES_9_4);
    }
}
