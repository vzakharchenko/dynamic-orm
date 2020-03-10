package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QSizeColumnImpl;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumnContext;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QSizeColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QSizeColumnBuilderImpl;
import liquibase.database.Database;

public class QDateBuilder extends QSizeColumnBuilderImpl<QSizeColumnImpl,
        QSizeColumnBuilder<QTableColumn, ?>> {


    public QDateBuilder(QTableColumnContext qTableColumn, String columnName) {
        super(qTableColumn, columnName);
    }

    @Override
    protected QSizeColumnImpl construct(String columnName) {
        return new QSizeColumnImpl(columnName);
    }

    @Override
    protected void createColumn(QDynamicTable dynamicTable, Database database) {
        dynamicTable
                .createDateColumn(database, columnType);
    }
}
