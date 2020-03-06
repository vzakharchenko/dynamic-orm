package com.github.vzakharchenko.dynamic.orm.core.dynamic;


import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.DynamicStructureSaver;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.DynamicStructureUpdater;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class QDynamicTableBuilder implements QTableBuilder {

    private final DynamicContext dynamicContext;
    private final DataSource dataSource;
    private DynamicStructureUpdater dynamicStructureUpdater;
    private QDynamicTable qDynamicTable;

    private Map<String, QDynamicTable> dynamicTableMap = new HashMap<>();

    private QDynamicTableBuilder(
            QDynamicTable qDynamicTable, DataSource dataSource,
            DynamicContext dynamicContext, Map<String, QDynamicTable> dynamicTableMap) {
        this(qDynamicTable, dataSource, dynamicContext);
        this.dynamicTableMap = dynamicTableMap;
    }

    private QDynamicTableBuilder(QDynamicTable qDynamicTable,
                                 DataSource dataSource,
                                 DynamicContext dynamicContext) {
        this.qDynamicTable = qDynamicTable;
        this.dynamicStructureUpdater = new DynamicStructureSaver(dataSource);
        this.dynamicContext = dynamicContext;
        this.dataSource = dataSource;
    }


    private static QTableBuilder createBuilder(
            String tableName,
            DataSource dataSource,
            DynamicContext dynamicContext,
            Map<String, QDynamicTable> dynamicTableMap) {
        return new QDynamicTableBuilder(
                dynamicContext.createQTable(tableName), dataSource, dynamicContext, dynamicTableMap);
    }

    public static QTableBuilder createBuilder(
            String tableName, DataSource dataSource, DynamicContext dynamicContext) {
        return new QDynamicTableBuilder(dynamicContext.createQTable(tableName), dataSource, dynamicContext);
    }

    @Override
    public QTableBuilder createStringColumn(String columnName, int size, boolean notNull) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            qDynamicTable = qDynamicTable
                    .createStringColumn(dynamicContext
                            .getDatabase(connection), columnName, size, notNull);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return this;
    }

    @Override
    public QTableBuilder createCharColumn(String columnName, int size, boolean notNull) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            qDynamicTable = qDynamicTable
                    .createCharColumn(dynamicContext
                            .getDatabase(connection), columnName, size, notNull);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return this;
    }

    @Override
    public QTableBuilder createClobColumn(
            String columnName, int size, boolean notNull) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            qDynamicTable = qDynamicTable
                    .createClobColumn(dynamicContext
                            .getDatabase(connection), columnName, size, notNull);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return this;
    }

    @Override
    public QTableBuilder createBooleanColumn(String columnName, boolean notNull) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            qDynamicTable = qDynamicTable
                    .createBooleanColumn(dynamicContext
                            .getDatabase(connection), columnName, notNull);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return this;
    }

    @Override
    public QTableBuilder createBlobColumn(
            String columnName, int size, boolean notNull) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            qDynamicTable = qDynamicTable
                    .createBlobColumn(dynamicContext
                            .getDatabase(connection), columnName, size, notNull);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return this;
    }
    // CHECKSTYLE:OFF
    @Override
    public <T extends Number & Comparable<?>> QTableBuilder createNumberColumn(
            String columnName, Class<T> typeClass, Integer size, Integer decimalDigits,
            boolean notNull) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            qDynamicTable = qDynamicTable
                    .createNumberColumn(dynamicContext
                                    .getDatabase(connection), columnName,
                            typeClass, size, decimalDigits, notNull);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return this;
    }
    // CHECKSTYLE:ON

    @Override
    public QTableBuilder createDateColumn(String columnName, boolean notNull) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            qDynamicTable = qDynamicTable.createDateColumn(dynamicContext
                    .getDatabase(connection), columnName, notNull);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return this;
    }

    @Override
    public QTableBuilder createDateTimeColumn(String columnName, boolean notNull) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            qDynamicTable = qDynamicTable.createDateTimeColumn(dynamicContext
                    .getDatabase(connection), columnName, notNull);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return this;
    }

    @Override
    public QTableBuilder createTimeColumn(String columnName, boolean notNull) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            qDynamicTable = qDynamicTable.createTimeColumn(dynamicContext
                    .getDatabase(connection), columnName, notNull);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return this;
    }

    @Override
    public QTableBuilder addPrimaryKey(Path path) {
        qDynamicTable = qDynamicTable.addPrimaryKey(path);
        return this;
    }

    @Override
    public QTableBuilder addPrimaryKey(String columnName) {
        qDynamicTable = qDynamicTable.addPrimaryKey(columnName);
        return this;
    }

    @Override
    public QTableBuilder addPrimaryKeyGenerator(PKGenerator<?> pkGenerator) {
        if (!ModelHelper.hasPrimaryKey(qDynamicTable)) {
            throw new IllegalStateException("First add Primary key to Table " +
                    qDynamicTable.getTableName());
        }
        qDynamicTable = qDynamicTable.addPKGenerator(pkGenerator);
        return this;
    }

    @Override
    public <T extends Number & Comparable<?>> QTableBuilder addPrimaryNumberKey(
            String columnName, Class<T> typeClass, int size, int decimalDigits) {
        QTableBuilder qTableBuilder = createNumberColumn(
                columnName, typeClass, size, decimalDigits, true);
        return qTableBuilder.addPrimaryKey(columnName);
    }

    @Override
    public QTableBuilder addPrimaryStringKey(String columnName, int size) {
        QTableBuilder qTableBuilder = createStringColumn(columnName, size, true);
        return qTableBuilder.addPrimaryKey(columnName);
    }

    @Override
    public QTableBuilder addForeignKey(
            Path localColumn, RelationalPath<?> remoteQTable, Path remotePrimaryKey) {
        qDynamicTable = qDynamicTable.addForeignKey(localColumn, remoteQTable, remotePrimaryKey);
        return this;
    }

    @Override
    public QTableBuilder addForeignKey(
            String localColumnName, RelationalPath<?> remoteQTable, Path remotePrimaryKey) {
        qDynamicTable = qDynamicTable.addForeignKey(
                localColumnName, remoteQTable, remotePrimaryKey);
        return this;
    }

    @Override
    public QTableBuilder addForeignKey(String localColumnName, RelationalPath<?> remoteQTable) {
        qDynamicTable = qDynamicTable.addForeignKey(localColumnName, remoteQTable);
        return this;
    }

    @Override
    public QTableBuilder addForeignKey(String localColumnName,
                                       String dynamicTableName) {
        QDynamicTable qDynamicTable0 = dynamicTableMap
                .get(StringUtils.upperCase(dynamicTableName));
        if (qDynamicTable0 == null) {
            qDynamicTable0 = dynamicContext.getQTable(dynamicTableName);
        }
        return addForeignKey(localColumnName, qDynamicTable0);
    }

    @Override
    public QTableBuilder addVersionColumn(String columnName) {
        qDynamicTable = qDynamicTable.addVersionColumn(columnName);
        return this;
    }

    @Override
    public QTableBuilder addIndex(Path<?> columnName, boolean unique) {
        qDynamicTable.addIndex(columnName, unique);
        return this;
    }

    @Override
    public QTableBuilder addIndex(String columnName, boolean unique) {
        qDynamicTable = qDynamicTable.addIndex(columnName, unique);
        return this;
    }

    @Override
    public QTableBuilder addVersionColumn(Path<?> versionColumn) {
        qDynamicTable = qDynamicTable.addVersionColumn(versionColumn);
        return this;
    }

    @Override
    public QTableBuilder addSoftDeleteColumn(
            String columnName, Serializable value, Serializable defaultValue) {
        qDynamicTable = qDynamicTable.addSoftDeleteColumn(columnName, value, defaultValue);
        return this;
    }

    @Override
    public QTableBuilder addCustomField(Serializable key, Serializable value) {
        qDynamicTable = qDynamicTable.registerCustomFields(key, value);
        return this;
    }

    @Override
    public <TYPE extends Serializable> QTableBuilder addSoftDeleteColumn(
            Path<TYPE> column, TYPE value, TYPE defaultValue) {
        qDynamicTable = qDynamicTable.addSoftDeleteColumn(column, value, defaultValue);
        return this;
    }

    @Override
    public QTableBuilder buildNextTable(String tableName) {
        dynamicTableMap.put(StringUtils.upperCase(
                qDynamicTable.getTableName()), qDynamicTable);
        return createBuilder(tableName, dataSource, dynamicContext, dynamicTableMap);
    }


    @Override
    public void buildSchema() {
        dynamicTableMap.put(StringUtils.upperCase(
                qDynamicTable.getTableName()), qDynamicTable);
        dynamicStructureUpdater.update(dynamicTableMap);
        dynamicTableMap.values().forEach(dynamicContext::registerQTable);
    }

    @Override
    public void support() {
        dynamicTableMap.put(StringUtils.upperCase(
                qDynamicTable.getTableName()), qDynamicTable);
        dynamicTableMap.values().forEach(dynamicContext::registerQTable);
    }

    @Override
    public void clear() {
        dynamicTableMap.clear();
        dynamicStructureUpdater = new DynamicStructureSaver(dataSource);
    }
}


