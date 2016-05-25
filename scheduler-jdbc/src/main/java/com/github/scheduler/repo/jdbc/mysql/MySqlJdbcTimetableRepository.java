package com.github.scheduler.repo.jdbc.mysql;

import com.github.scheduler.repo.jdbc.AbstractJdbcTimetableRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class MySqlJdbcTimetableRepository extends AbstractJdbcTimetableRepository {
    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.MYSQL);
    }
}
