package com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QDefaultColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumnContext;
import liquibase.database.Database;
import liquibase.datatype.LiquibaseDataType;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

public abstract class QDefaultColumnBuilder<COLUMN_TYPE extends QDefaultColumn,
        BUILDER_TYPE extends QColumnBuilder<QTableColumn, ?>>
        implements QColumnBuilder<QTableColumn, BUILDER_TYPE> {
    protected final COLUMN_TYPE columnType;
    protected final QDynamicTable qDynamicTable;
    private final QTableColumnContext qTableColumn;

    protected QDefaultColumnBuilder(QTableColumnContext qTableColumnContext,
                                    QDynamicTable dynamicTable, String columnName) {
        this.columnType = construct(columnName);
        this.qTableColumn = qTableColumnContext;
        this.qDynamicTable = dynamicTable;
    }

    protected abstract COLUMN_TYPE construct(String columnName);

    @Override
    public BUILDER_TYPE notNull() {
        columnType.setNullable(Boolean.FALSE);
        return (BUILDER_TYPE) this;
    }

    @Override
    public BUILDER_TYPE nullable() {
        columnType.setNullable(Boolean.TRUE);
        return (BUILDER_TYPE) this;
    }

    @Override
    public BUILDER_TYPE nullable(boolean value) {
        return value ? nullable() : notNull();
    }

    @Override
    public BUILDER_TYPE useAsPrimaryKey() {
        columnType.setIsPrimaryKey(Boolean.TRUE);
        columnType.setNullable(Boolean.FALSE);
        return (BUILDER_TYPE) this;
    }

    @Override
    public BUILDER_TYPE notPrimaryKey() {
        columnType.setIsPrimaryKey(Boolean.FALSE);
        return (BUILDER_TYPE) this;
    }

    @Override
    public final QTableColumn createColumn() {
        createColumn0();
        return qTableColumn;
    }

    protected abstract void createColumn(QDynamicTable dynamicTable,
                                         Database database);


    protected String getDataTypeString(LiquibaseDataType dataType) {
        DataSource dataSource = qTableColumn.getContext().getDataSource();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            return dataType.toDatabaseDataType(qTableColumn.getContext()
                    .getDynamicContext().getDatabase(connection)).getType();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private void createColumn0() {
        DataSource dataSource = qTableColumn.getContext().getDataSource();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            createColumn(qDynamicTable,
                    qTableColumn.getContext()
                            .getDynamicContext().getDatabase(connection));
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
