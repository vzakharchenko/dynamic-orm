package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class DynamicContext {
    private final Map<String, QDynamicTable> dynamicTableMap = new ConcurrentHashMap<>();

    private final Database database;

    public DynamicContext(Database database) {
        this.database = database;
    }


    public QDynamicTable getQTable(String tableName) {
        QDynamicTable qDynamicTable = dynamicTableMap.get(StringUtils.upperCase(tableName));
        if (qDynamicTable == null) {
            throw new IllegalStateException("dynamic table with name " + tableName +
                    " is not found. May be you should Build this table first ");
        }
        return qDynamicTable;
    }

    public void registerQTable(QDynamicTable qDynamicTable) {
        dynamicTableMap.put(StringUtils.upperCase(qDynamicTable.getTableName()), qDynamicTable);
    }

    public Database getDatabase(Connection connection) {
        database.setConnection(new JdbcConnection(connection));
        return database;
    }

    public void clear() {
        dynamicTableMap.clear();
    }

    public Collection<QDynamicTable> getQTables() {
        return dynamicTableMap.values();
    }

    public QDynamicTable createQTable(String tableName) {
        QDynamicTable qDynamicTable = dynamicTableMap.get(StringUtils.upperCase(tableName));
        return qDynamicTable != null ? qDynamicTable : new QDynamicTable(tableName);
    }
}
