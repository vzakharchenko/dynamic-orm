package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.index.QIndexBuilder;
import com.querydsl.core.types.Path;

public class QIndexBuilderImpl implements QIndexBuilder {

    private final QTableBuilder tableBuilder;
    private final QDynamicTable dynamicTable;

    public QIndexBuilderImpl(QTableBuilder tableBuilder,
                             QDynamicTable dynamicTable) {
        this.tableBuilder = tableBuilder;
        this.dynamicTable = dynamicTable;
    }

    @Override
    public QTableBuilder buildIndex(Path<?> columnName, boolean unique) {
        dynamicTable.addIndex(columnName, unique);
        return tableBuilder;
    }

    @Override
    public QTableBuilder buildIndex(String columnName, boolean unique) {
        dynamicTable.addIndex(columnName, unique);
        return tableBuilder;
    }
}
