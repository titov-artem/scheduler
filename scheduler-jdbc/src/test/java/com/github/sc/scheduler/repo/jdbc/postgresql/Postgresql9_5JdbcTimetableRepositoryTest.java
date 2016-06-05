package com.github.sc.scheduler.repo.jdbc.postgresql;

import com.github.sc.scheduler.repo.jdbc.AbstractJdbcTimetableRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration({
        "classpath*:postgresql-database-ctx.xml",
        "classpath*:context/scheduler-postgresql-jdbc-repositories-ctx.xml"
})
public class Postgresql9_5JdbcTimetableRepositoryTest extends AbstractJdbcTimetableRepositoryTest {
}