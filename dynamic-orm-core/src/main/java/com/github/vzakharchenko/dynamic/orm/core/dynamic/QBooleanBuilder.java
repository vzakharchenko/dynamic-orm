package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QDefaultColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumnContext;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QDefaultColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QSizeColumnBuilder;
import liquibase.database.Database;

public class QBooleanBuilder extends QDefaultColumnBuilder<QDefaultColumn,
        QSizeColumnBuilder<QTableColumn, ?>> {


    public QBooleanBuilder(QTableColumnContext qTableColumn, String columnName) {
        super(qTableColumn, columnName);
    }

    @Override
    protected QDefaultColumn construct(String columnName) {
        return new QDefaultColumn(columnName);
    }

    @Override
    protected void createColumn(QDynamicTable dynamicTable,
                                Database database) {
        dynamicTable
                .createBooleanColumn(database, columnType);
    }

}
