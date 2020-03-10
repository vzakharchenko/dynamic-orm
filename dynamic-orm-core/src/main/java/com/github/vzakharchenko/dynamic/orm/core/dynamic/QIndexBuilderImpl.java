package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.index.QIndexBuilder;
import com.querydsl.core.types.Path;

public class QIndexBuilderImpl implements QIndexBuilder {

    private final QDynamicBuilderContext dynamicBuilderContext;

    public QIndexBuilderImpl(QDynamicBuilderContext dynamicBuilderContext) {
        this.dynamicBuilderContext = dynamicBuilderContext;
    }


    @Override
    public QTableBuilder buildIndex(Path<?> columnName, boolean unique) {
        dynamicBuilderContext.getDynamicTable().addIndex(columnName, unique);
        return dynamicBuilderContext;
    }

    @Override
    public QTableBuilder buildIndex(String columnName, boolean unique) {
        dynamicBuilderContext.getDynamicTable().addIndex(columnName, unique);
        return dynamicBuilderContext;
    }
}
