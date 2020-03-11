package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.fk.QForeignKeyBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class QForeignKeyBuilderImpl implements QForeignKeyBuilder {

    private final QTableBuilder tableBuilder;
    private final QDynamicTable dynamicTable;
    private final QDynamicBuilderContext dynamicBuilderContext;

    public QForeignKeyBuilderImpl(QTableBuilder tableBuilder,
                                  QDynamicTable dynamicTable,
                                  QDynamicBuilderContext dynamicBuilderContext) {
        this.tableBuilder = tableBuilder;
        this.dynamicTable = dynamicTable;
        this.dynamicBuilderContext = dynamicBuilderContext;
    }

    private QDynamicTable getDynamicTable() {
        return dynamicTable;
    }

    @Override
    public QTableBuilder buildForeignKey(
            Path localColumn,
            RelationalPath<?> remoteQTable,
            Path remotePrimaryKey) {
        getDynamicTable().addForeignKey(localColumn, remoteQTable, remotePrimaryKey);
        return tableBuilder;
    }

    @Override
    public QTableBuilder buildForeignKey(
            String localColumnName,
            RelationalPath<?> remoteQTable,
            Path remotePrimaryKey) {
        getDynamicTable().addForeignKey(
                localColumnName, remoteQTable, remotePrimaryKey);
        return tableBuilder;
    }

    @Override
    public QTableBuilder buildForeignKey(String localColumnName, RelationalPath<?> remoteQTable) {
        getDynamicTable().addForeignKey(localColumnName, remoteQTable);
        return tableBuilder;
    }

    @Override
    public QTableBuilder buildForeignKey(String localColumnName,
                                         String dynamicTableName) {
        QDynamicTable qDynamicTable0 = dynamicBuilderContext
                .getContextTables().get(StringUtils.upperCase(dynamicTableName, Locale.US));
        if (qDynamicTable0 == null) {
            qDynamicTable0 = dynamicBuilderContext.getDynamicContext()
                    .getQTable(dynamicTableName);
        }
        return buildForeignKey(localColumnName, qDynamicTable0);
    }

}
