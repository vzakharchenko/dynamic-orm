package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.fk.QForeignKeyBuilder;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QForeignKeyBuilderImpl implements QForeignKeyBuilder {

    private final QTableBuilder tableBuilder;
    private final QDynamicTable dynamicTable;
    private final QDynamicBuilderContext dynamicBuilderContext;
    private final List<Path<?>> localColumns;

    public QForeignKeyBuilderImpl(QTableBuilder tableBuilder,
                                  QDynamicTable dynamicTable,
                                  List<Path<?>> localColumns,
                                  QDynamicBuilderContext dynamicBuilderContext) {
        this.tableBuilder = tableBuilder;
        this.dynamicTable = dynamicTable;
        this.localColumns = localColumns;
        this.dynamicBuilderContext = dynamicBuilderContext;
    }

    private QDynamicTable getDynamicTable() {
        return dynamicTable;
    }


    @Override
    public QTableBuilder addForeignKey(RelationalPath<?> remoteQTable,
                                       Path<?>... remotePrimaryKeys) {
        return addForeignKey(remoteQTable, Arrays.asList(remotePrimaryKeys));
    }

    public QTableBuilder addForeignKey(RelationalPath<?> remoteQTable,
                                       List<Path<?>> remotePrimaryKeys) {
        getDynamicTable().addForeignKey(localColumns, remoteQTable, remotePrimaryKeys);
        return tableBuilder;
    }

    @Override
    public QTableBuilder addForeignKey(QDynamicTable remoteDynamicTable,
                                       String... remotePrimaryKeys) {
        return addForeignKey(remoteDynamicTable,
                Arrays.stream(remotePrimaryKeys).map((Function<String, Path<?>>)
                        s -> (Path<?>) remoteDynamicTable.getColumnByName(s, Object.class))
                        .collect(Collectors.toList()));
    }

    @Override
    public QTableBuilder addForeignKey(String remoteDynamicTableName,
                                       String... remotePrimaryKeys) {
        return addForeignKey(getDynamicTable(remoteDynamicTableName), remotePrimaryKeys);
    }

    @Override
    public QTableBuilder addForeignKey(RelationalPath<?> remoteQTable) {
        return addForeignKey(remoteQTable, PrimaryKeyHelper
                .getPrimaryKeyColumns(remoteQTable).stream()
                .map((Function<Path<?>, Path<?>>) path -> path).collect(Collectors.toList()));
    }

    private QDynamicTable getDynamicTable(String remoteDynamicTableName) {
        QDynamicTable qDynamicTable0 = dynamicBuilderContext
                .getContextTables().get(StringUtils.upperCase(remoteDynamicTableName, Locale.US));
        if (qDynamicTable0 == null) {
            qDynamicTable0 = dynamicBuilderContext.getDynamicContext()
                    .getQTable(remoteDynamicTableName);
        }
        return qDynamicTable0;
    }

    @Override
    public QTableBuilder addForeignKey(String remoteDynamicTableName) {
        return addForeignKey(getDynamicTable(remoteDynamicTableName));
    }

    @Override
    public QTableBuilder addForeignKey(QDynamicTable remoteDynamicTable) {
        return addForeignKey(remoteDynamicTable,
                PrimaryKeyHelper.getPrimaryKeyColumns(remoteDynamicTable).toArray(new Path[0]));
    }

    @Override
    public QTableBuilder drop() {
        dynamicTable.removeForeignKey(localColumns);
        return tableBuilder;
    }
}
