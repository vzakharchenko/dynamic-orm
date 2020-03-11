package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QSizeColumnImpl;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumnContext;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QSizeColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QSizeColumnBuilderImpl;
import liquibase.database.Database;

public class QBlobBuilder extends QSizeColumnBuilderImpl<QSizeColumnImpl,
        QSizeColumnBuilder<QTableColumn, ?>> {


    public QBlobBuilder(QTableColumnContext qTableColumn,
                        QDynamicTable dynamicTable,
                        String columnName) {
        super(qTableColumn, dynamicTable, columnName);
    }

    @Override
    protected QSizeColumnImpl construct(String columnName) {
        return new QSizeColumnImpl(columnName);
    }

    @Override
    protected void createColumn(QDynamicTable dynamicTable, Database database) {
        dynamicTable
                .createBlobColumn(database, columnType);
    }
}
