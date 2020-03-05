package com.github.vzakharchenko.dynamic.orm.core.dynamic;

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
    QTableBuilder buildTable(String tableName);


}
