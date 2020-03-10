package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.fk.QForeignKeyBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class QForeignKeyBuilderImpl implements QForeignKeyBuilder {

    private final QDynamicBuilderContext dynamicBuilderContext;

    public QForeignKeyBuilderImpl(QDynamicBuilderContext dynamicBuilderContext) {
        this.dynamicBuilderContext = dynamicBuilderContext;
    }

    private QDynamicTable getDynamicTable() {
        return dynamicBuilderContext.getDynamicTable();
    }

    @Override
    public QTableBuilder buildForeignKey(
            Path localColumn,
            RelationalPath<?> remoteQTable,
            Path remotePrimaryKey) {
        getDynamicTable().addForeignKey(localColumn, remoteQTable, remotePrimaryKey);
        return dynamicBuilderContext;
    }

    @Override
    public QTableBuilder buildForeignKey(
            String localColumnName,
            RelationalPath<?> remoteQTable,
            Path remotePrimaryKey) {
        getDynamicTable().addForeignKey(
                localColumnName, remoteQTable, remotePrimaryKey);
        return dynamicBuilderContext;
    }

    @Override
    public QTableBuilder buildForeignKey(String localColumnName, RelationalPath<?> remoteQTable) {
        getDynamicTable().addForeignKey(localColumnName, remoteQTable);
        return dynamicBuilderContext;
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
