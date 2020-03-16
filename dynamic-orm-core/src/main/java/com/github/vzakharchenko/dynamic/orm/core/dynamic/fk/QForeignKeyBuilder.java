package com.github.vzakharchenko.dynamic.orm.core.dynamic.fk;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;

public interface QForeignKeyBuilder {
    QTableBuilder buildForeignKey(
            RelationalPath<?> remoteQTable, Path<?>... remotePrimaryKey);

    QTableBuilder buildForeignKey(
            QDynamicTable remoteDynamicTable, String... remotePrimaryKey);

    QTableBuilder buildForeignKey(
            String remoteDynamicTableName, String... remotePrimaryKey);

    QTableBuilder buildForeignKey(RelationalPath<?> remoteQTable);

    QTableBuilder buildForeignKey(String remoteDynamicTableName);

    QTableBuilder buildForeignKey(QDynamicTable remoteDynamicTable);
}
