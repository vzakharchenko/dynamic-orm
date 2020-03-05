package com.github.vzakharchenko.dynamic.orm.core;

import com.querydsl.sql.*;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.core.*;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 13.04.15
 * Time: 19:03
 */
public abstract class QueryDslJdbcTemplateFactory {


    public static SQLTemplates getDialect(DataSource dataSource, boolean quote) {
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            return getDialect(database, quote);
        } catch (DatabaseException e) {
            throw new DataAccessResourceFailureException("Unable to determine database type: ", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }


    public static SQLTemplates getDialect(Database database, boolean quote) {
        if (database instanceof OracleDatabase) {
            return new OracleTemplates(quote);
        } else if (database instanceof PostgresDatabase) {
            return new PostgreSQLTemplates(quote);
        } else if (database instanceof MySQLDatabase) {
            return new MySQLTemplates(quote);
        } else if (database instanceof HsqlDatabase) {
            return new HSQLDBTemplates(quote);
        } else if (database instanceof H2Database) {
            return new H2Templates(quote);
        } else if (database instanceof DerbyDatabase) {
            return new DerbyTemplates(quote);
        } else if (database instanceof FirebirdDatabase) {
            return new FirebirdTemplates(quote);
        } else if (database instanceof SQLiteDatabase) {
            return new SQLiteTemplates(quote);
        } else if (database instanceof MSSQLDatabase) {
            return new SQLServerTemplates(quote);
        } else {
            throw new InvalidDataAccessResourceUsageException(database
                    .getDatabaseProductName() + " is an unsupported database");
        }

    }
}
