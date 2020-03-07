package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Collection;

/**
 *
 */
public class QDynamicTableFactoryImpl implements QDynamicTableFactory, AccessDynamicContext {
    private final DataSource dataSource;

    private DynamicContext dynamicContext;

    private final Database database;
    private final OrmQueryFactory ormQueryFactory;

    public QDynamicTableFactoryImpl(OrmQueryFactory ormQueryFactory,
                                    DataSource dataSource) {
        this.dataSource = dataSource;
        this.ormQueryFactory = ormQueryFactory;
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

    private DynamicContext getDynamicContext() {
        if (dynamicContext == null) {
            dynamicContext = new DynamicContext(database, ormQueryFactory);
        }
        return dynamicContext;
    }

    @Override
    public QDynamicTable getQDynamicTableByName(String tableName) {
        return getDynamicContext().getQTable(tableName);
    }

    @Override
    public Collection<QDynamicTable> getQDynamicTables() {
        return getDynamicContext().getQTables();
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
