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

    /**
     * add/modify/drop column
     *
     * @return Column builder
     */
    QTableColumn columns();

    /**
     * add primary key for table
     * add column to composite primary key
     * remove column from primary key
     *
     * @return primary key builder
     */
    QPrimaryKeyBuilder primaryKey();

    /**
     * add foreign Key
     *
     * @param localColumns - column names
     *                     you should create columns first using method columns()
     * @return foreign Key builder
     */
    QForeignKeyBuilder foreignKey(String... localColumns);

    /**
     * add foreign Key
     *
     * @param localColumns - columns
     * @return foreign Key builder
     */
    QForeignKeyBuilder foreignKeyPath(Path<?>... localColumns);

    /**
     * create/drop sql index
     *
     * @param localColumns - columns
     * @return Index builder
     */
    QIndexBuilder index(String... localColumns);

    /**
     * create/drop sql index
     *
     * @param localColumns - columns
     * @return Index builder
     */
    QIndexBuilder index(Path<?>... localColumns);

    /**
     * mark column as  Optimistic Locking
     *
     * @param columnName column Name
     * @return table builder
     */
    QTableBuilder addVersionColumn(
            String columnName);

    /**
     * mark column as  Optimistic Locking
     *
     * @param versionColumn column
     * @return table builder
     */
    QTableBuilder addVersionColumn(Path<?> versionColumn);

    /**
     * mark column as soft delete column
     * soft deletion means not real deletion of the record, but simply marks the record as deleted
     *
     * @param columnName   column name
     * @param value        deleted value
     * @param defaultValue not deleted value
     * @return table builder
     */
    QTableBuilder addSoftDeleteColumn(
            String columnName, Serializable value, Serializable defaultValue);

    /**
     * build next table
     *
     * @param tableName name of next table
     * @return build next table
     */
    QTableBuilder buildNextTable(String tableName);

    /**
     * return to table factory
     *
     * @return Dynamic Builder Factory
     */
    QDynamicTableFactory endBuildTables();

}
