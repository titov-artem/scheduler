package ru.yandex.qe.common.scheduler.repo.jdbc.postgresql;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import ru.yandex.qe.common.scheduler.repo.jdbc.AbstractJdbcTaskRepository;

public class Postgresql9_4JdbcTaskRepository extends AbstractJdbcTaskRepository {
    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.POSTGRES_9_4);
    }
}
