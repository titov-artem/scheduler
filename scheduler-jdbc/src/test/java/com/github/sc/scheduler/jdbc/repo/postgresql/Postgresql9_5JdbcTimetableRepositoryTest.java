package com.github.sc.scheduler.jdbc.repo.postgresql;

import com.github.sc.scheduler.jdbc.repo.AbstractJdbcTimetableRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
@ContextConfiguration({
        "classpath*:postgresql-database-ctx.xml",
        "classpath*:context/scheduler-postgresql-jdbc-repositories-ctx.xml"
})
public class Postgresql9_5JdbcTimetableRepositoryTest extends AbstractJdbcTimetableRepositoryTest {
}