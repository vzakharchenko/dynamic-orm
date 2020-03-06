package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import java.util.Collection;

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
     * get list of dynamic Tables
     * @return
     */
    Collection<QDynamicTable> getQDynamicTables();

    /**
     * build a table
     *
     * @param tableName
     * @return tableBuilder
     * @see QTableBuilder
     */
    QTableBuilder buildTable(String tableName);


}
