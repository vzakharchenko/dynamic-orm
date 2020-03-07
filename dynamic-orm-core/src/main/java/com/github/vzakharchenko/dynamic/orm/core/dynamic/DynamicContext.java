package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class DynamicContext {
    public static final String DYNAMIC_METADATA = "DYNAMIC_METADATA";
    private final Map<String, QDynamicTable> dynamicTableMap = new ConcurrentHashMap<>();

    private final Database database;
    private final OrmQueryFactory ormQueryFactory;

    public DynamicContext(Database database, OrmQueryFactory queryFactory) {
        this.database = database;
        this.ormQueryFactory = queryFactory;
    }


    public QDynamicTable getQTable(String tableName) {
        QDynamicTable qDynamicTable = dynamicTableMap.get(StringUtils.upperCase(tableName));
        if (qDynamicTable == null) {
            throw new IllegalStateException("dynamic table with name " + tableName +
                    " is not found. May be you should Build this table first ");
        }
        return qDynamicTable;
    }

    private void registerQTable(QDynamicTable qDynamicTable) {
        dynamicTableMap.put(StringUtils.upperCase(qDynamicTable.getTableName()), qDynamicTable);
    }

    public void registerQTables(Collection<QDynamicTable> qDynamicTables) {
        for (QDynamicTable qDynamicTable : qDynamicTables) {
            dynamicTableMap.put(StringUtils.upperCase(
                    qDynamicTable.getTableName()), qDynamicTable);
        }
        updateCache();
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

    private void updateCache() {
        CacheStorageImpl cacheStorage = new CacheStorageImpl();
        cacheStorage.setDynamicTables(new ArrayList<>(dynamicTableMap.values()));
        ormQueryFactory.getContext().getTransactionCache().putToCache(DYNAMIC_METADATA, cacheStorage);
    }

    private void updateDynamicTables() {
        CacheStorage cacheStorage = ormQueryFactory.getContext().getTransactionCache()
                .getFromCache(DYNAMIC_METADATA, CacheStorage.class);
        if (cacheStorage != null) {
            cacheStorage.getDynamicTables().forEach(this::registerQTable);
        }
    }

    public QDynamicTable createQTable(String tableName) {
        updateDynamicTables();
        QDynamicTable qDynamicTable = dynamicTableMap.get(StringUtils.upperCase(tableName));
        return qDynamicTable != null ? qDynamicTable : new QDynamicTable(tableName);
    }
}
