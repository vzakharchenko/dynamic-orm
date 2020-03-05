package com.github.vzakharchenko.dynamic.orm.core.dynamic;


import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;

import java.io.Serializable;

/**
 * Table Builder
 */
public interface QTableBuilder {


    QTableBuilder createStringColumn(String columnName, int size, boolean notNull);

    QTableBuilder createCharColumn(String columnName, int size, boolean notNull);

    QTableBuilder createClobColumn(String columnName, int size, boolean notNull);

    QTableBuilder createBooleanColumn(String columnName, boolean notNull);

    QTableBuilder createBlobColumn(String columnName, int size, boolean notNull);

    <T extends Number & Comparable<?>> QTableBuilder createNumberColumn(
            String columnName,
            Class<T> typeClass,
            Integer size, Integer decimalDigits, boolean notNull);

    QTableBuilder createDateColumn(String columnName, boolean notNull);

    QTableBuilder createDateTimeColumn(String columnName, boolean notNull);

    QTableBuilder createTimeColumn(String columnName, boolean notNull);


    QTableBuilder addPrimaryKey(Path path);

    QTableBuilder addPrimaryKey(String columnName);

    QTableBuilder addPrimaryKeyGenerator(PKGenerator<?> pkGenerator);

    <T extends Number & Comparable<?>> QTableBuilder addPrimaryNumberKey(
            String columnName, Class<T> typeClass, int size, int decimalDigits);

    QTableBuilder addPrimaryStringKey(String columnName, int size);

    QTableBuilder addForeignKey(
            Path localColumn, RelationalPath<?> remoteQTable, Path remotePrimaryKey);

    QTableBuilder addForeignKey(
            String localColumnName, RelationalPath<?> remoteQTable, Path remotePrimaryKey);

    QTableBuilder addForeignKey(
            String localColumnName, RelationalPath<?> remoteQTable);

    QTableBuilder addForeignKey(
            String localColumnName, String dynamicTableName);

    QTableBuilder addVersionColumn(
            String columnName);

    QTableBuilder addIndex(Path<?> columnName, boolean unique);

    QTableBuilder addIndex(String columnName, boolean unique);

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
