package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QNumberColumnImpl;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumnContext;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QNumberColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QSizeColumnBuilderImpl;
import liquibase.database.Database;

public class QNumberBuilder
        extends QSizeColumnBuilderImpl<QNumberColumnImpl,
        QNumberColumnBuilder<QTableColumn, ?>>
        implements QNumberColumnBuilder<QTableColumn,
        QNumberColumnBuilder<QTableColumn, ?>> {


    public QNumberBuilder(QTableColumnContext qTableColumn, String columnName,
                          Class<? extends Number> tClass) {
        super(qTableColumn, columnName);
        columnType.setNumberClass(tClass);
    }


    @Override
    protected QNumberColumnImpl construct(String columnName) {
        return new QNumberColumnImpl(columnName);
    }

    @Override
    protected void createColumn(QDynamicTable dynamicTable,
                                Database database) {
        dynamicTable
                .createNumberColumn(database, columnType);
    }

    @Override
    public QNumberColumnBuilder<QTableColumn, ?> decimalDigits(Integer decimalDigits) {
        columnType.setDecimalDigits(decimalDigits);
        return this;
    }
}
