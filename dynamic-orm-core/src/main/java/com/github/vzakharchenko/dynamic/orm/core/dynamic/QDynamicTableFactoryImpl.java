package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 *
 */
public class QDynamicTableFactoryImpl implements QDynamicTableFactory, AccessDynamicContext {
    private final DataSource dataSource;

    private DynamicContext dynamicContext;

    private Database database;


    public QDynamicTableFactoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            this.database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
        } catch (DatabaseException e) {
            throw new IllegalStateException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public QDynamicTableFactoryImpl(DataSource dataSource, Database database) {
        this.dataSource = dataSource;
        this.database = database;
    }

    private DynamicContext getDynamicContext() {
        if (dynamicContext == null) {
            dynamicContext = new DynamicContext(database);
        }
        return dynamicContext;
    }

    @Override
    public QDynamicTable getQDynamicTableByName(String tableName) {
        return getDynamicContext().getQTable(tableName);
    }


    @Override
    public QTableBuilder buildTable(String tableName) {
        return QDynamicTableBuilder.createBuilder(tableName, dataSource, getDynamicContext());
    }

    @Override
    public void clearCache() {
        getDynamicContext().clear();
    }
}
