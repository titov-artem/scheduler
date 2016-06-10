package com.github.sc.scheduler.jdbc.repo.mysql;

import com.github.sc.scheduler.jdbc.repo.AbstractJdbcTaskArgsRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
@ContextConfiguration({
        "classpath*:mysql-database-ctx.xml",
        "classpath*:context/scheduler-mysql-jdbc-repositories-ctx.xml"
})
public class MySqlJdbcTaskArgsRepositoryTest extends AbstractJdbcTaskArgsRepositoryTest {
}