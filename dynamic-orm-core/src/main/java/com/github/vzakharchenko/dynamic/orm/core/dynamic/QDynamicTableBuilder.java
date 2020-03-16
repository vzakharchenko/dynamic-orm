package com.github.vzakharchenko.dynamic.orm.core.dynamic;


import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumnContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.fk.QForeignKeyBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.index.QIndexBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.pk.QPrimaryKeyBuilder;
import com.querydsl.core.types.Path;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
public final class QDynamicTableBuilder implements QTableBuilder {

    private final QDynamicBuilderContext dynamicContextHolder;
    private final DataSource dataSource;
    private QDynamicTable qDynamicTable;


    private QDynamicTableBuilder(QDynamicTable qDynamicTable,
                                 DataSource dataSource,
                                 QDynamicBuilderContext dynamicContextHolder) {
        this.qDynamicTable = qDynamicTable;
        this.dynamicContextHolder = dynamicContextHolder;
        this.dataSource = dataSource;
    }


    public static QTableBuilder createBuilder(
            String tableName,
            DataSource dataSource,
            QDynamicBuilderContext dynamicContextHolder) {
        return new QDynamicTableBuilder(
                dynamicContextHolder.getDynamicContext().createQTable(tableName),
                dataSource, dynamicContextHolder);
    }


    @Override
    public QTableColumn addColumns() {
        return new QTableColumnContextImpl(dynamicContextHolder,
                this, qDynamicTable);
    }

    @Override
    public QPrimaryKeyBuilder addPrimaryKey() {
        return new QPrimaryKeyBuilderImpl(this, qDynamicTable);
    }

    @Override
    public QForeignKeyBuilder addForeignKey(String... localColumns) {
        List<Path<?>> localStringColumns = Arrays.stream(localColumns)
                .map(StringUtils::upperCase).map((Function<String, Path<?>>)
                        s -> qDynamicTable.getColumnByName(s))
                .collect(Collectors.toList());
        return addForeignKey(localStringColumns);
    }

    @Override
    public QForeignKeyBuilder addForeignKeyPath(Path<?>... localColumns) {
        return addForeignKey(Arrays.asList(localColumns));
    }

    @Override
    public QIndexBuilder addIndex(String... localColumns) {
        List<Path<?>> localStringColumns = Arrays.stream(localColumns)
                .map(StringUtils::upperCase).map((Function<String, Path<?>>)
                        s -> qDynamicTable.getColumnByName(s))
                .collect(Collectors.toList());
        return addIndex(localStringColumns);
    }

    @Override
    public QIndexBuilder addIndex(Path<?>... localColumns) {
        return addIndex(Arrays.asList(localColumns));
    }

    private QIndexBuilder addIndex(List<Path<?>> localColumns) {
        return new QIndexBuilderImpl(this,
                localColumns, qDynamicTable);
    }

    private QForeignKeyBuilder addForeignKey(List<Path<?>> localColumns) {
        return new QForeignKeyBuilderImpl(this, qDynamicTable,
                localColumns, dynamicContextHolder);
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
    public QTableBuilder buildNextTable(String tableName) {
        dynamicContextHolder.getContextTables().put(StringUtils.upperCase(
                qDynamicTable.getTableName()), qDynamicTable);
        return createBuilder(tableName, dataSource, dynamicContextHolder);
    }

    @Override
    public QDynamicTableFactory finish() {
        dynamicContextHolder.getContextTables().put(qDynamicTable.getTableName(), qDynamicTable);
        return dynamicContextHolder;
    }

}


