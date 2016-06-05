package com.github.sc.scheduler.repo.jdbc.mysql;

import com.github.sc.scheduler.repo.jdbc.AbstractJdbcTaskArgsRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration({
        "classpath*:mysql-database-ctx.xml",
        "classpath*:context/scheduler-mysql-jdbc-repositories-ctx.xml"
})
public class MySqlJdbcTaskArgsRepositoryTest extends AbstractJdbcTaskArgsRepositoryTest {
}