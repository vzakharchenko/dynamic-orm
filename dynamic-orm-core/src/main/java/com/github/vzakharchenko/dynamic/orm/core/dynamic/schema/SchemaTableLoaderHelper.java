package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTableFactory;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QNumberColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder.QSizeColumnBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models.SchemaColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models.SchemaPrimaryKey;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models.SchemaSoftDelete;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models.SchemaTable;
import com.github.vzakharchenko.dynamic.orm.core.helper.ClassHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGeneratorSequence;
import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;

import java.util.List;
import java.util.Objects;

import static com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators.SEQUENCE;

public final class SchemaTableLoaderHelper {

    private SchemaTableLoaderHelper() {
    }

    private static QColumnBuilder<QTableColumn, ?>
    loadSizeColumn(QSizeColumnBuilder<QTableColumn, ?> sizeColumnBuilder,
                   SchemaColumn schemaColumn) {
        return sizeColumnBuilder.size(schemaColumn.getSize());
    }

    private static QColumnBuilder<QTableColumn, ?>
    loadNumberColumn(QNumberColumnBuilder<QTableColumn, ?> numberColumnBuilder,
                     SchemaColumn schemaColumn) {
        numberColumnBuilder.decimalDigits(schemaColumn.getDecimalDigits());
        return loadSizeColumn(numberColumnBuilder, schemaColumn);
    }

    private static QColumnBuilder<QTableColumn, ?> stringPathLoader(
            QTableColumn columnBuilder, SchemaColumn schemaColumn
    ) {
        QSizeColumnBuilder<QTableColumn, ?> qSizeBuilder;
        switch (schemaColumn.getJdbcType()) {
            case "VARCHAR2":
            case "VARCHAR":
                qSizeBuilder = columnBuilder.addStringColumn(schemaColumn.getName());
                break;
            case "CHAR":
                qSizeBuilder = columnBuilder.addCharColumn(schemaColumn.getName());
                break;
            case "CLOB":
                qSizeBuilder = columnBuilder.addClobColumn(schemaColumn.getName());
                break;
            default:
                throw new IllegalStateException("Unsupported JDBC Type: " +
                        schemaColumn.getJdbcType());
        }
        return loadSizeColumn(qSizeBuilder, schemaColumn);
    }

    // CHECKSTYLE:OFF
    private static void loadColumn(QTableColumn columnBuilder, SchemaColumn schemaColumn) {
        QColumnBuilder<QTableColumn, ?> qSimpleBuilder;

        switch (schemaColumn.getClassName()) {
            case "StringPath":
                qSimpleBuilder = stringPathLoader(
                        columnBuilder, schemaColumn);
                break;
            case "TimePath":
                qSimpleBuilder = loadSizeColumn(
                        columnBuilder.addTimeColumn(schemaColumn.getName()), schemaColumn);
                break;
            case "DatePath":
                qSimpleBuilder = loadSizeColumn(
                        columnBuilder.addDateColumn(schemaColumn.getName()), schemaColumn);
                break;
            case "DateTimePath":
                qSimpleBuilder = loadSizeColumn(
                        columnBuilder.addDateTimeColumn(schemaColumn.getName()), schemaColumn);
                break;
            case "SimplePath":
                qSimpleBuilder = loadSizeColumn(
                        columnBuilder.addBlobColumn(schemaColumn.getName()), schemaColumn);
                break;
            case "BooleanPath":
                qSimpleBuilder =
                        columnBuilder.addBooleanColumn(schemaColumn.getName());
                break;
            case "NumberPath":
                qSimpleBuilder =
                        loadNumberColumn(columnBuilder.addNumberColumn(schemaColumn.getName(),
                                ClassHelper.getNumberClass(schemaColumn.getaType())), schemaColumn);
                break;
            default:
                throw new IllegalStateException("Unsupported column Type: " +
                        schemaColumn.getClassName());

        }
        qSimpleBuilder.nullable(schemaColumn.getNullable()).create();
    }
    // CHECKSTYLE:ON

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

    private static void loadColumns(QTableBuilder qTableBuilder, List<SchemaColumn> columns) {
        if (columns != null) {
            columns.forEach(schemaColumn -> {
                QTableColumn tableColumn = qTableBuilder.addColumns();
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
            softDeletedColumn(qTableBuilder, schemaTable.getSoftDeleteColumn());
            versionColumn(qTableBuilder, schemaTable.getVersionColumn());
            qTableBuilder.finish();
        });
    }
}
