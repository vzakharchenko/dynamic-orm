package com.github.vzakharchenko.dynamic.orm.core.dynamic.column;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.*;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QNumberColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QSizeColumnBuilder;

import javax.sql.DataSource;
import java.util.Map;

public class QTableColumnContextImpl implements QTableColumnContext {

    private final QDynamicBuilderContext builderContext;

    public QTableColumnContextImpl(QDynamicBuilderContext builderContext) {
        this.builderContext = builderContext;
    }

    @Override
    public DataSource getDataSource() {
        return builderContext.getDataSource();
    }

    @Override
    public QDynamicTable getDynamicTable() {
        return builderContext.getDynamicTable();
    }

    @Override
    public DynamicContext getDynamicContext() {
        return builderContext.getDynamicContext();
    }

    @Override
    public Map<String, QDynamicTable> getContextTables() {
        return builderContext.getContextTables();
    }

    @Override
    public QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addStringColumn(String columnName) {
        return new QStringBuilder(this, columnName);
    }

    @Override
    public QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addCharColumn(String columnName) {
        return new QCharBuilder(this, columnName);
    }

    @Override
    public QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addClobColumn(String columnName) {
        return new QClobBuilder(this, columnName);
    }

    @Override
    public QColumnBuilder<QTableColumn, ? extends QColumnBuilder<QTableColumn, ?>>
    addBooleanColumn(String columnName) {
        return new QBooleanBuilder(this, columnName);
    }

    @Override
    public QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addBlobColumn(String columnName) {
        return new QBlobBuilder(this, columnName);
    }

    @Override
    public <T extends Number & Comparable<?>>
    QNumberColumnBuilder<QTableColumn, QNumberColumnBuilder<QTableColumn, ?>>
    addNumberColumn(String columnName, Class<T> typeClass) {
        return new QNumberBuilder(this, columnName, typeClass);
    }

    @Override
    public QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addDateColumn(String columnName) {
        return new QDateBuilder(this, columnName);
    }

    @Override
    public QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addDateTimeColumn(String columnName) {
        return new QDateTimeBuilder(this, columnName);
    }

    @Override
    public QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addTimeColumn(String columnName) {
        return new QTimeBuilder(this, columnName);
    }

    @Override
    public QTableBuilder finish() {
        return builderContext;
    }
}
