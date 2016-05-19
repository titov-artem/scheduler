package ru.yandex.qe.common.scheduler.repo.jdbc.mysql;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import ru.yandex.qe.common.scheduler.model.Run;
import ru.yandex.qe.common.scheduler.repo.ActiveRunsRepository;
import ru.yandex.qe.common.scheduler.repo.jdbc.AbstractJdbcActiveRunsRepository;
import ru.yandex.qe.common.scheduler.repo.jdbc.AbstractJdbcRunsRepository;

import java.util.List;

/**
 * @author titov.artem.u@yandex.com on 02.11.15.
 */
public class MySqlJdbcActiveRunsRepository extends AbstractJdbcActiveRunsRepository {

    @Override
    protected DSLContext DSL() {
        return DSL.using(SQLDialect.MYSQL);
    }

    @Override
    protected int getMaxInSize() {
        return 1000;
    }
}
