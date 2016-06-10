package com.github.sc.scheduler.jdbc.repo.mysql;

import com.github.sc.scheduler.jdbc.repo.AbstractJdbcActiveRunsRepositoryTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
@ContextConfiguration({
        "classpath*:mysql-database-ctx.xml",
        "classpath*:context/scheduler-mysql-jdbc-repositories-ctx.xml"
})
@Transactional
public class MySqlJdbcActiveRunsRepositoryTest extends AbstractJdbcActiveRunsRepositoryTest {
}