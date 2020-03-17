package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QCustomColumnImpl;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumnContext;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.CustomColumnCreator;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QCustomColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QSizeColumnBuilderImpl;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadataFactory;
import liquibase.database.Database;
import liquibase.datatype.LiquibaseDataType;
import org.springframework.util.Assert;

public class QCustomBuilder
        extends QSizeColumnBuilderImpl<QCustomColumnImpl,
        QCustomColumnBuilder<QTableColumn, ?>>
        implements QCustomColumnBuilder<QTableColumn,
        QCustomColumnBuilder<QTableColumn, ?>> {


    public QCustomBuilder(QTableColumnContext qTableColumn,
                          QDynamicTable dynamicTable,
                          String columnName) {
        super(qTableColumn, dynamicTable, columnName);
    }


    @Override
    protected QCustomColumnImpl construct(String columnName) {
        return new QCustomColumnImpl(columnName);
    }

    @Override
    protected void createColumn(QDynamicTable dynamicTable,
                                Database database) {
        dynamicTable
                .addColumn(new CustomColumnMetaDataInfo(columnType));
    }

    @Override
    public QCustomColumnBuilder<QTableColumn, ?> decimalDigits(Integer decimalDigits) {
        columnType.setDecimalDigits(decimalDigits);
        return this;
    }

    @Override
    public QCustomColumnBuilder<QTableColumn, ?> jdbcType(LiquibaseDataType dataType) {
        Assert.notNull(dataType, "DataType is null");
        columnType.setJdbc(getDataTypeString(dataType));
        return this;
    }

    @Override
    public QCustomColumnBuilder<QTableColumn, ?>
    column(CustomColumnCreator customColumnCreator) {
        Path<?> column = customColumnCreator
                .create(PathMetadataFactory
                        .forProperty(qDynamicTable, columnType.columnName()));
        Assert.notNull(column, "Created Column Are Null");
        columnType.setColumn(column);
        return this;
    }
}
