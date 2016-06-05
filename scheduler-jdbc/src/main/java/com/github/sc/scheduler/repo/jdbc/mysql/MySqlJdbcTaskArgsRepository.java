package com.github.sc.scheduler.repo.jdbc.mysql;

import com.github.sc.scheduler.repo.jdbc.AbstractJdbcTaskArgsRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class MySqlJdbcTaskArgsRepository extends AbstractJdbcTaskArgsRepository {
    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.MYSQL);
    }
}
