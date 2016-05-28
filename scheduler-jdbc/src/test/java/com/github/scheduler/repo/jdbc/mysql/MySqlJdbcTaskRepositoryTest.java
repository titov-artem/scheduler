package com.github.scheduler.repo.jdbc.mysql;

import com.github.scheduler.repo.jdbc.AbstractJdbcTaskRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration({
        "classpath*:mysql-database-ctx.xml",
        "classpath*:context/scheduler-mysql-jdbc-repositories-ctx.xml"
})
public class MySqlJdbcTaskRepositoryTest extends AbstractJdbcTaskRepositoryTest {
}