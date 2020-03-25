package com.github.vzakharchenko.dynamic.orm.core.dynamic.column;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QCustomColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QNumberColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QSizeColumnBuilder;


public interface QTableColumn {
    QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>>
    addStringColumn(String columnName);

    QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>> addCharColumn(String columnName);

    QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>> addClobColumn(String columnName);

    QColumnBuilder<QTableColumn, ? extends QColumnBuilder<QTableColumn, ?>> addBooleanColumn(String columnName);

    QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>> addBlobColumn(String columnName);

    <T extends Number> QNumberColumnBuilder<QTableColumn,
            QNumberColumnBuilder<QTableColumn, ?>>
    addNumberColumn(
            String columnName,
            Class<T> typeClass);

    QCustomColumnBuilder<QTableColumn, QCustomColumnBuilder<QTableColumn, ?>>
    addCustomColumn(String columnName);

    QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>> addDateColumn(String columnName);

    QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>> addDateTimeColumn(String columnName);

    QSizeColumnBuilder<QTableColumn, QSizeColumnBuilder<QTableColumn, ?>> addTimeColumn(String columnName);

    QModifyColumn modifyColumn();

    QTableColumn dropColumns(String... columns);

    QTableBuilder endColumns();

}
