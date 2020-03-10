package com.github.vzakharchenko.dynamic.orm.core.dynamic.fk;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;

public interface QForeignKeyBuilder {
    QTableBuilder buildForeignKey(
            Path localColumn, RelationalPath<?> remoteQTable, Path remotePrimaryKey);

    QTableBuilder buildForeignKey(
            String localColumnName, RelationalPath<?> remoteQTable, Path remotePrimaryKey);

    QTableBuilder buildForeignKey(String localColumnName, RelationalPath<?> remoteQTable);

    QTableBuilder buildForeignKey(String localColumnName,
                                  String dynamicTableName);
}
