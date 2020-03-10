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

    QTableColumn addColumns();

    QPrimaryKeyBuilder addPrimaryKey();

    QForeignKeyBuilder addForeignKey();

    QIndexBuilder addIndex();

    QTableBuilder addVersionColumn(
            String columnName);

    QTableBuilder addVersionColumn(Path<?> versionColumn);

    QTableBuilder addSoftDeleteColumn(
            String columnName, Serializable value, Serializable defaultValue);

    QTableBuilder addCustomField(
            Serializable key, Serializable value);

    <TYPE extends Serializable> QTableBuilder addSoftDeleteColumn(
            Path<TYPE> column, TYPE value, TYPE defaultValue);

    /**
     * batch build table
     *
     * @param tableName
     * @return
     */
    QTableBuilder buildNextTable(String tableName);

    /**
     * The construction of the table using ddl queries
     * <p>
     * Attention! existing columns are not deleted
     */
    void buildSchema();

    /**
     * support of existing tables
     * <p>
     * ATTENTION! CONSISTENCY OF COLUMNS CAN NOT BE GUARANTEED
     * <p>
     * Is better use buildSchema()
     */
    void support();

    void clear();
}
