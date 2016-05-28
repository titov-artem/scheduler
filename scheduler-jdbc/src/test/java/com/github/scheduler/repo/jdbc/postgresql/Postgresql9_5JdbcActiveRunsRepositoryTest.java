package com.github.scheduler.repo.jdbc.postgresql;

import com.github.scheduler.repo.jdbc.AbstractJdbcActiveRunsRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration({
        "classpath*:postgresql-database-ctx.xml",
        "classpath*:context/scheduler-postgresql-jdbc-repositories-ctx.xml"
})
public class Postgresql9_5JdbcActiveRunsRepositoryTest extends AbstractJdbcActiveRunsRepositoryTest {
}