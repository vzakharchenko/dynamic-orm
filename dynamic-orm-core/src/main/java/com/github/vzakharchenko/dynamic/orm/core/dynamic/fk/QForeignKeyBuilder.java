package com.github.vzakharchenko.dynamic.orm.core.dynamic.fk;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;

/**
 * Foreign Key Builder.
 */
public interface QForeignKeyBuilder {

    /**
     * add Foreign Key to Table
     *
     * @param remoteQTable     - remote qtable
     * @param remotePrimaryKey remote table columns
     * @return table builder
     */
    QTableBuilder addForeignKey(
            RelationalPath<?> remoteQTable, Path<?>... remotePrimaryKey);

    /**
     * add Foreign Key to Table
     *
     * @param remoteDynamicTable - remote dynamic Table
     * @param remotePrimaryKey   remote table columns
     * @return table builder
     */
    QTableBuilder addForeignKey(
            QDynamicTable remoteDynamicTable, String... remotePrimaryKey);

    /**
     * add Foreign Key to Table
     *
     * @param remoteDynamicTableName remote dynamic Table Name
     * @param remotePrimaryKey       remote table columns
     * @return table builder
     */
    QTableBuilder addForeignKey(
            String remoteDynamicTableName, String... remotePrimaryKey);

    /**
     * add Foreign Key to Table
     *
     * @param remoteQTable remote table
     * @return table builder
     */
    QTableBuilder addForeignKey(RelationalPath<?> remoteQTable);

    /**
     * add Foreign Key to Table
     *
     * @param remoteDynamicTableName - dynamic table name
     * @return table builder
     */
    QTableBuilder addForeignKey(String remoteDynamicTableName);

    /**
     * add Foreign Key to Table
     *
     * @param remoteDynamicTable - dynamic table
     * @return table builder
     */
    QTableBuilder addForeignKey(QDynamicTable remoteDynamicTable);

    /**
     * drop Foreign Key
     * @return table builder
     */
    QTableBuilder drop();
}
