package com.github.sc.scheduler.jdbc.repo.mysql;

import com.github.sc.scheduler.jdbc.repo.AbstractJdbcTimetableRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class MySqlJdbcTimetableRepository extends AbstractJdbcTimetableRepository {
    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.MYSQL);
    }

    @Override
    protected int getMaxInSize() {
        return 1000;
    }
}
