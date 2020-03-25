package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.index.QIndexBuilder;
import com.querydsl.core.types.Path;

import java.util.List;

public class QIndexBuilderImpl implements QIndexBuilder {

    private final QTableBuilder tableBuilder;
    private final QDynamicTable dynamicTable;
    private final List<Path<?>> localColumns;
    private boolean clusteredFlag;

    public QIndexBuilderImpl(QTableBuilder tableBuilder,
                             List<Path<?>> localColumns,
                             QDynamicTable dynamicTable) {
        this.tableBuilder = tableBuilder;
        this.localColumns = localColumns;
        this.dynamicTable = dynamicTable;
    }

    private QTableBuilder buildIndex(boolean unique) {
        dynamicTable.addIndex(localColumns, unique, clusteredFlag);
        return tableBuilder;
    }

    @Override
    public QIndexBuilder clustered() {
        clusteredFlag = true;
        return this;
    }

    @Override
    public QTableBuilder addIndex() {
        return buildIndex(false);
    }

    @Override
    public QTableBuilder addUniqueIndex() {
        return buildIndex(true);
    }

    @Override
    public QTableBuilder drop() {
        dynamicTable.removeIndex(localColumns);
        return tableBuilder;
    }
}
