package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTableFactory;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QCustomColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.index.QIndexBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models.*;
import com.github.vzakharchenko.dynamic.orm.core.helper.ClassHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGeneratorSequence;
import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Objects;

import static com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators.SEQUENCE;

public final class SchemaTableLoaderHelper {

    private SchemaTableLoaderHelper() {
    }

    private static void loadColumn(QTableColumn columnBuilder, SchemaColumn schemaColumn) {
        QCustomColumnBuilder<QTableColumn, ?> builder = columnBuilder.addCustomColumn(schemaColumn.getName()).column(
                metadata -> JavaTypeUtils.createPath(schemaColumn.getClassName(),
                        ClassHelper.getClassType(schemaColumn.getaType()), metadata));
        builder.jdbcType(schemaColumn.getJdbcType()).nullable(schemaColumn.getNullable())
                .size(schemaColumn.getSize())
                .decimalDigits(schemaColumn.getDecimalDigits()).create();
    }

    private static void loadGenerator(QTableBuilder qTableBuilder, SchemaTable schemaTable) {
        String primaryGeneratorType = schemaTable.getPrimaryGeneratorType();
        String sequanceName = schemaTable.getSequanceName();

        if (primaryGeneratorType != null) {
            PKGenerator pkGenerator;
            PrimaryKeyGenerators primaryKeyGenerators = PrimaryKeyGenerators
                    .valueOf(primaryGeneratorType);
            if (Objects.equals(primaryGeneratorType, SEQUENCE.name())) {
                pkGenerator = new PKGeneratorSequence(sequanceName);
            } else {
                pkGenerator = primaryKeyGenerators.getPkGenerator();
            }
            qTableBuilder.addPrimaryKey().addPrimaryKeyGenerator(pkGenerator);
        }
    }

    private static void loadPrimaryKeys(QTableBuilder qTableBuilder,
                                        SchemaTable schemaTable,
                                        List<SchemaPrimaryKey> primaryKeys) {
        if (primaryKeys != null) {
            primaryKeys.forEach(schemaPrimaryKey -> qTableBuilder
                    .addPrimaryKey().addPrimaryKey(schemaPrimaryKey.getColumn())
            );
            loadGenerator(qTableBuilder, schemaTable);
        }
    }

    private static void clusteredIndex(QIndexBuilder builder, SchemaIndex schemaIndex) {
        if (BooleanUtils.isTrue(schemaIndex.isClustered())) {
            builder.clustered();
        }
    }

    private static void loadIndices(QTableBuilder qTableBuilder, List<SchemaIndex> indices) {
        if (indices != null) {
            indices.forEach(schemaIndex -> {
                QIndexBuilder builder = qTableBuilder
                        .addIndex(schemaIndex.getColumns().toArray(new String[0]));
                clusteredIndex(builder, schemaIndex);
                if (schemaIndex.getUniq()) {
                    builder.buildUniqueIndex();
                } else {
                    builder.buildIndex();
                }
            });
        }
    }

//    private static void loadForeignKey(QTableBuilder qTableBuilder, List<SchemaForeignKey> foreignKeys) {
//        if (foreignKeys != null) {
//            foreignKeys.forEach(foreignKey -> {
//                QForeignKeyBuilder builder = qTableBuilder
//                        .addForeignKey(foreignKey.getLocalColumns().toArray(new String[0]));
//                builder.buildForeignKey(foreignKey.getTable(),
//                        foreignKey.getRemoteColumns().toArray(new String[0]));
//            });
//        }
//    }

    private static void loadColumns(QTableBuilder qTableBuilder, List<SchemaColumn> columns) {
        if (columns != null) {
            columns.forEach(schemaColumn -> {
                QTableColumn tableColumn = qTableBuilder.columns();
                loadColumn(tableColumn, schemaColumn);
            });
        }
    }

    private static void softDeletedColumn(QTableBuilder qTableBuilder,
                                          SchemaSoftDelete softDelete) {
        if (softDelete != null) {
            qTableBuilder.addSoftDeleteColumn(softDelete.getColumn(),
                    softDelete.getDeletedValue(), softDelete.getDeletedValue());
        }
    }

    private static void versionColumn(QTableBuilder qTableBuilder,
                                      String column) {
        if (column != null) {
            qTableBuilder.addVersionColumn(column);
        }
    }

    public static void loadTables(QDynamicTableFactory dynamicTableFactory,
                                  List<SchemaTable> tables) {
        tables.forEach(schemaTable -> {
            QTableBuilder qTableBuilder = dynamicTableFactory
                    .buildTables(schemaTable.getName());
            loadColumns(qTableBuilder, schemaTable.getColumns());
            loadPrimaryKeys(qTableBuilder, schemaTable, schemaTable.getPrimaryKeys());
            loadIndices(qTableBuilder, schemaTable.getIndices());
            softDeletedColumn(qTableBuilder, schemaTable.getSoftDeleteColumn());
            versionColumn(qTableBuilder, schemaTable.getVersionColumn());
            qTableBuilder.finish();
        });
    }
}
