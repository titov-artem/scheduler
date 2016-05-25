package com.github.scheduler.repo.jdbc.mysql;

import com.github.scheduler.repo.jdbc.AbstractJdbcTaskParamsRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class MySqlJdbcTaskParamsRepository extends AbstractJdbcTaskParamsRepository {
    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.MYSQL);
    }
}
