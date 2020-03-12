package com.github.vzakharchenko.dynamic.orm.core;

import liquibase.database.core.*;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNotNull;

public class QueryDslJdbcTemplateFactoryTest {

    @Test
    public void factoryTest() {
        assertNotNull(QueryDslJdbcTemplateFactory.getDialect(new OracleDatabase(), true));
        assertNotNull(QueryDslJdbcTemplateFactory.getDialect(new PostgresDatabase(), true));
        assertNotNull(QueryDslJdbcTemplateFactory.getDialect(new MySQLDatabase(), true));
        assertNotNull(QueryDslJdbcTemplateFactory.getDialect(new HsqlDatabase(), true));
        assertNotNull(QueryDslJdbcTemplateFactory.getDialect(new H2Database(), true));
        assertNotNull(QueryDslJdbcTemplateFactory.getDialect(new DerbyDatabase(), true));
        assertNotNull(QueryDslJdbcTemplateFactory.getDialect(new FirebirdDatabase(), true));
        assertNotNull(QueryDslJdbcTemplateFactory.getDialect(new Firebird3Database(), true));
        assertNotNull(QueryDslJdbcTemplateFactory.getDialect(new SQLiteDatabase(), true));
        assertNotNull(QueryDslJdbcTemplateFactory.getDialect(new MSSQLDatabase(), true));
        assertNotNull(QueryDslJdbcTemplateFactory.getDialect(new Db2zDatabase(), true));
    }

    @Test(expectedExceptions = InvalidDataAccessResourceUsageException.class)
    public void factoryTestFailed() {
        QueryDslJdbcTemplateFactory.getDialect(new SybaseASADatabase(), true);
    }
}
