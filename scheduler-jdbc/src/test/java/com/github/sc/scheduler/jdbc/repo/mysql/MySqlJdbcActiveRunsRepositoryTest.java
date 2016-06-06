package com.github.sc.scheduler.repo.jdbc.mysql;

import com.github.sc.scheduler.jdbc.repo.AbstractJdbcActiveRunsRepositoryTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration({
        "classpath*:mysql-database-ctx.xml",
        "classpath*:context/scheduler-mysql-jdbc-repositories-ctx.xml"
})
@Transactional
public class MySqlJdbcActiveRunsRepositoryTest extends AbstractJdbcActiveRunsRepositoryTest {
}