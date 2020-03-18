package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.SchemaLoader;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.SchemaSaver;

/**
 * Build and supports dynamic tables at runtime
 */
public interface QDynamicTableFactory {

    /**
     * get dynamic table by name.
     * <p>
     * Attention! First you need to build a table. Using buildTable
     *
     * @param tableName
     * @return
     */
    QDynamicTable getQDynamicTableByName(String tableName);

    /**
     * build a table
     *
     * @param tableName
     * @return tableBuilder
     * @see QTableBuilder
     */
    QTableBuilder buildTables(String tableName);

    /**
     * drop a table
     *
     * @param tableNames
     * @return tableBuilder
     * @see QDynamicTableFactory
     */
    QDynamicTableFactory dropTableOrView(String... tableNames);


    /**
     * Is table exists
     * @param tableName
     * @return true if exists otherwise false
     */
    boolean isTableExist(String tableName);

    /**
     * add sequance
     *
     * @param sequenceName
     * @return builder
     */
    QSequenceBuilder createSequence(String sequenceName);

    /**
     * drop sequence
     *
     * @param  sequenceNames
     * @return tableBuilder
     * @see QDynamicTableFactory
     */
    QDynamicTableFactory dropSequence(String... sequenceNames);


    /**
     * create SQL View (Virtual table)
     *
     * @param viewName
     * @return
     */
    QViewBuilder createView(String viewName);

    /**
     * The construction of the table using ddl queries
     * <p>
     * Attention! existing columns are not deleted
     */
    void buildSchema();

    /**
     * save dynamic database structure
     *
     * @param schemaSaver
     */
    void saveSchema(SchemaSaver schemaSaver);

    /**
     * load dynamic database structure
     *
     * @param schemaLoader
     */
    void loadSchema(SchemaLoader schemaLoader);

    void clear();
}
