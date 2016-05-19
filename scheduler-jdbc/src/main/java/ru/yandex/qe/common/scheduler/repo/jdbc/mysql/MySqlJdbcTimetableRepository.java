package ru.yandex.qe.common.scheduler.repo.jdbc.mysql;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import ru.yandex.qe.common.scheduler.repo.jdbc.AbstractJdbcTimetableRepository;

public class MySqlJdbcTimetableRepository extends AbstractJdbcTimetableRepository {
    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.MYSQL);
    }
}
