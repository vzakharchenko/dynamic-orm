package com.github.vzakharchenko.dynamic.orm.core.dynamic;


import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.fk.QForeignKeyBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.index.QIndexBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.pk.QPrimaryKeyBuilder;
import com.querydsl.core.types.Path;

import java.io.Serializable;

/**
 * Table Builder
 */
public interface QTableBuilder {

    QTableColumn columns();

    QPrimaryKeyBuilder addPrimaryKey();

    QForeignKeyBuilder addForeignKey(String... localColumns);

    QForeignKeyBuilder addForeignKeyPath(Path<?>... localColumns);

    QIndexBuilder addIndex(String... localColumns);

    QIndexBuilder addIndex(Path<?>... localColumns);

    QTableBuilder addVersionColumn(
            String columnName);

    QTableBuilder addVersionColumn(Path<?> versionColumn);

    QTableBuilder addSoftDeleteColumn(
            String columnName, Serializable value, Serializable defaultValue);

    /**
     * batch build table
     *
     * @param tableName
     * @return
     */
    QTableBuilder buildNextTable(String tableName);

    QDynamicTableFactory finish();

}
