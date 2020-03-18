package com.github.vzakharchenko.dynamic.orm.core.dynamic.column;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.*;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QCustomColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QNumberColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QSizeColumnBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class QTableColumnContextImpl implements QTableColumnContext {

    private final QDynamicBuilderContext builderContext;
    private final QTableBuilder qTableBuilder;
    private final QDynamicTable dynamicTable;

    public QTableColumnContextImpl(QDynamicBuilderContext builderContext,
                                   QTableBuilder qTableBuilder,
                                   QDynamicTable dynamicTable) {
        this.builderContext = builderContext;
        this.qTableBuilder = qTableBuilder;
        this.dynamicTable = dynamicTable;
    }

    @Override
    public QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addStringColumn(String columnName) {
        Assert.notNull(columnName);
        return new QStringBuilder(this,
                dynamicTable, columnName);
    }

    @Override
    public QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addCharColumn(String columnName) {
        Assert.notNull(columnName);
        return new QCharBuilder(this,
                dynamicTable, columnName);
    }

    @Override
    public QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addClobColumn(String columnName) {
        Assert.notNull(columnName);
        return new QClobBuilder(this, dynamicTable,
                columnName);
    }

    @Override
    public QColumnBuilder<QTableColumn, ? extends QColumnBuilder<QTableColumn, ?>>
    addBooleanColumn(String columnName) {
        Assert.notNull(columnName);
        return new QBooleanBuilder(this,
                dynamicTable, columnName);
    }

    @Override
    public QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addBlobColumn(String columnName) {
        Assert.notNull(columnName);
        return new QBlobBuilder(this,
                dynamicTable, columnName);
    }

    @Override
    public <T extends Number>
    QNumberColumnBuilder<QTableColumn, QNumberColumnBuilder<QTableColumn, ?>>
    addNumberColumn(String columnName, Class<T> typeClass) {
        Assert.notNull(columnName);
        Assert.notNull(typeClass);
        return new QNumberBuilder(this,
                dynamicTable, columnName, typeClass);
    }

    @Override
    public QCustomColumnBuilder<QTableColumn, QCustomColumnBuilder<QTableColumn, ?>>
    addCustomColumn(String columnName) {
        return new QCustomBuilder(this,
                dynamicTable, StringUtils.upperCase(columnName));
    }

    @Override
    public QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addDateColumn(String columnName) {
        Assert.notNull(columnName);
        return new QDateBuilder(this,
                dynamicTable, columnName);
    }

    @Override
    public QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addDateTimeColumn(String columnName) {
        Assert.notNull(columnName);
        return new QDateTimeBuilder(this,
                dynamicTable, columnName);
    }

    @Override
    public QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addTimeColumn(String columnName) {
        Assert.notNull(columnName);
        return new QTimeBuilder(this,
                dynamicTable, columnName);
    }

    @Override
    public QModifyColumn modifyColumn() {
        return new QModifyColumnImpl(this, dynamicTable);
    }

    @Override
    public QTableColumn dropColumns(String... columns) {
        return modifyColumn().dropColumns(columns).finish();
    }

    @Override
    public QTableBuilder finish() {
        return qTableBuilder;
    }

    @Override
    public QDynamicBuilderContext getContext() {
        return builderContext;
    }
}
