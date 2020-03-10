package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.pk.QPrimaryKeyBuilder;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.querydsl.core.types.Path;

public class QPrimaryKeyBuilderImpl implements QPrimaryKeyBuilder {

    private final QDynamicBuilderContext dynamicBuilderContext;

    public QPrimaryKeyBuilderImpl(QDynamicBuilderContext dynamicBuilderContext) {
        this.dynamicBuilderContext = dynamicBuilderContext;
    }

    private QDynamicTable getDynamicTable() {
        return dynamicBuilderContext.getDynamicTable();
    }

    @Override
    public QPrimaryKeyBuilder addPrimaryKey(Path path) {
        getDynamicTable().addPrimaryKey(path);
        return this;
    }

    @Override
    public QPrimaryKeyBuilder addPrimaryKey(String columnName) {
        getDynamicTable().addPrimaryKey(columnName);
        return this;
    }

    @Override
    public QPrimaryKeyBuilder addPrimaryKeyGenerator(PKGenerator<?> pkGenerator) {
        QDynamicTable dynamicTable = getDynamicTable();
        if (!ModelHelper.hasPrimaryKey(dynamicTable)) {
            throw new IllegalStateException("First add Primary key to Table " +
                    dynamicTable.getTableName());
        }
        dynamicTable.addPKGenerator(pkGenerator);
        return this;
    }

    @Override
    public QTableBuilder finish() {
        return dynamicBuilderContext;
    }
}
