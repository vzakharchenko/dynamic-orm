package com.github.vzakharchenko.dynamic.orm.core.dynamic;


import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumnContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.fk.QForeignKeyBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.index.QIndexBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.pk.QPrimaryKeyBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.DynamicStructureSaver;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.DynamicStructureUpdater;
import com.querydsl.core.types.Path;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class QDynamicTableBuilder implements QDynamicBuilderContext {

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
                dynamicContext.createQTable(tableName),
                dataSource, dynamicContext, dynamicTableMap);
    }

    public static QTableBuilder createBuilder(
            String tableName, DataSource dataSource, DynamicContext dynamicContext) {
        return new QDynamicTableBuilder(dynamicContext.createQTable(tableName), dataSource, dynamicContext);
    }


    @Override
    public QTableColumn addColumns() {
        return new QTableColumnContextImpl(this);
    }

    @Override
    public QPrimaryKeyBuilder addPrimaryKey() {
        return new QPrimaryKeyBuilderImpl(this);
    }

    @Override
    public QForeignKeyBuilder addForeignKey() {
        return new QForeignKeyBuilderImpl(this);
    }

    @Override
    public QIndexBuilder addIndex() {
        return new QIndexBuilderImpl(this);
    }

    @Override
    public QTableBuilder addVersionColumn(String columnName) {
        qDynamicTable = qDynamicTable.addVersionColumn(columnName);
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
        dynamicContext.registerQTables(dynamicTableMap.values());
    }

    @Override
    public void support() {
        dynamicTableMap.put(StringUtils.upperCase(
                qDynamicTable.getTableName()), qDynamicTable);
        dynamicContext.registerQTables(dynamicTableMap.values());
    }

    @Override
    public void clear() {
        dynamicTableMap.clear();
        dynamicStructureUpdater = new DynamicStructureSaver(dataSource);
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public QDynamicTable getDynamicTable() {
        return qDynamicTable;
    }

    @Override
    public DynamicContext getDynamicContext() {
        return dynamicContext;
    }

    @Override
    public Map<String, QDynamicTable> getContextTables() {
        return dynamicTableMap;
    }
}


