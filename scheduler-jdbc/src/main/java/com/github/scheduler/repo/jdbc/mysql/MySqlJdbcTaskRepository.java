package com.github.scheduler.repo.jdbc.mysql;

import com.github.scheduler.repo.jdbc.AbstractJdbcTaskRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class MySqlJdbcTaskRepository extends AbstractJdbcTaskRepository {
    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.MYSQL);
    }
}
