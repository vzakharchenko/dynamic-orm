package com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QSizeColumnImpl;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumnContext;


public abstract class QSizeColumnBuilderImpl<COLUMN_TYPE extends QSizeColumnImpl,
        BUILDER_TYPE extends QSizeColumnBuilder<QTableColumn, ?>>
        extends QDefaultColumnBuilder<COLUMN_TYPE, BUILDER_TYPE>
        implements QSizeColumnBuilder<QTableColumn, BUILDER_TYPE> {


    protected QSizeColumnBuilderImpl(QTableColumnContext qTableColumn,
                                     String columnName) {
        super(qTableColumn, columnName);
    }

    @Override
    public BUILDER_TYPE size(Integer size) {
        columnType.setColumnSize(size);
        return (BUILDER_TYPE) this;
    }

}
