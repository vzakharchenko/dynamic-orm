package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.pk.QPrimaryKeyBuilder;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.querydsl.core.types.Path;

public class QPrimaryKeyBuilderImpl implements QPrimaryKeyBuilder {

    private final QTableBuilder tableBuilder;
    private final QDynamicTable dynamicTable;

    public QPrimaryKeyBuilderImpl(QTableBuilder tableBuilder,
                                  QDynamicTable qDynamicTable) {
        this.tableBuilder = tableBuilder;
        this.dynamicTable = qDynamicTable;
    }


    @Override
    public QPrimaryKeyBuilder addPrimaryKey(Path column) {
        dynamicTable.modifyPrimaryKey(column, false);
        return this;
    }

    @Override
    public QPrimaryKeyBuilder removePrimaryKey(Path column) {
        return removePrimaryKey(ModelHelper.getColumnName(column));
    }

    @Override
    public QPrimaryKeyBuilder addPrimaryKey(String columnName) {
        dynamicTable.addPrimaryKey(columnName);
        return this;
    }

    @Override
    public QPrimaryKeyBuilder removePrimaryKey(String columnName) {
        dynamicTable.removePrimaryKey(columnName);
        return this;
    }

    @Override
    public QPrimaryKeyBuilder addPrimaryKeyGenerator(PKGenerator<?> pkGenerator) {
        if (!PrimaryKeyHelper.hasPrimaryKey(dynamicTable)) {
            throw new IllegalStateException("First add Primary key to Table " +
                    dynamicTable.getTableName());
        }
        dynamicTable.addPKGenerator(pkGenerator);
        return this;
    }

    @Override
    public QTableBuilder endPrimaryKey() {
        return tableBuilder;
    }
}
