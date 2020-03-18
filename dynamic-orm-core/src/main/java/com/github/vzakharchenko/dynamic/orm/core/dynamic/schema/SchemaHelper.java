package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.*;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models.*;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.querydsl.core.types.Path;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.PrimaryKey;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SchemaHelper {

    private SchemaHelper() {
    }


    private static void updateSequanceModel(Schema schema, Map<String,
            SequanceModel> sequenceModelMap) {
        schema.setSequences(sequenceModelMap.values().stream().map(sequanceModel -> {
            SchemaSequence schemaSequence = new SchemaSequence();
            schemaSequence.setName(sequanceModel.getName());
            schemaSequence.setInitial(sequanceModel.getInitial());
            schemaSequence.setIncrement(sequanceModel.getIncrement());
            schemaSequence.setMax(sequanceModel.getMax());
            schemaSequence.setMin(sequanceModel.getMin());
            return schemaSequence;
        }).collect(Collectors.toList()));
    }

    private static SchemaColumn updateColumn(QDynamicTable dynamicTable, Path<?> path) {
        SchemaColumn schemaColumn = new SchemaColumn();
        ColumnMetaDataInfo metaInfo = dynamicTable.getMetaInfo(path);
        schemaColumn.setName(path.getMetadata().getName());
        Class<?> type = path.getType();
        schemaColumn.setaType(type.isArray() ? "ARRAY" : type.getCanonicalName());
        schemaColumn.setClassName(path.getClass().getSimpleName());
        schemaColumn.setDecimalDigits(metaInfo.getDecimalDigits());
        schemaColumn.setNullable(metaInfo.isNullable());
        schemaColumn.setJdbcType(metaInfo.getJdbcType());
        schemaColumn.setJdbcType(metaInfo.getJdbcType());
        schemaColumn.setSize(metaInfo.getSize());
        return schemaColumn;
    }

    private static void updateSchemaColumns(SchemaView schemaView, QDynamicTable dynamicTable) {
        List<Path<?>> columns = dynamicTable.getColumns();
        schemaView.setColumns(columns.stream().map(path -> updateColumn(dynamicTable, path))
                .collect(Collectors.toList()));
    }

    private static void updateViewModel(Schema schema, Map<String, ViewDataHolder> viewMap) {
        schema.setViews(viewMap.values().stream().map(
                holder -> {
                    SchemaView schemaView = new SchemaView();
                    ViewModel viewModel = holder.getViewModel();
                    schemaView.setName(viewModel.getName());
                    schemaView.setSql(viewModel.getSql());
                    updateSchemaColumns(schemaView, holder.getDynamicTable());
                    return schemaView;
                }).collect(Collectors.toList()));
    }

    private static void updateForeignKey(SchemaForeignKey schemaForeignKey,
                                         ForeignKey<?> foreignKey) {
        if (foreignKey.getEntity() instanceof QDynamicTable) {
            schemaForeignKey.setDynamicRemote(true);
            schemaForeignKey.setTable(foreignKey.getEntity().getTableName());
        } else {
            schemaForeignKey.setDynamicRemote(false);
            schemaForeignKey.setTable(foreignKey.getEntity().getClass().getCanonicalName());
        }
        schemaForeignKey.setLocalColumns(foreignKey.getLocalColumns().stream()
                .map((Function<Path<?>, String>) path ->
                        path.getMetadata().getName()).collect(Collectors.toList()));
        schemaForeignKey.setRemoteColumns(foreignKey.getForeignColumns());
    }

    private static void updateForeignKey(SchemaTable schemaTable, QDynamicTable dynamicTable) {
        schemaTable.setForeignKeys(dynamicTable.getForeignKeys().stream().map(foreignKey -> {
                    SchemaForeignKey schemaForeignKey = new SchemaForeignKey();
                    updateForeignKey(schemaForeignKey, foreignKey);
                    return schemaForeignKey;
                }).collect(Collectors.toList())
        );
    }

    private static void updatePrimaryKey(SchemaTable schemaTable, PrimaryKey<?> primaryKey) {
        schemaTable.setPrimaryKeys(primaryKey.getLocalColumns().stream()
                .map((Function<Path<?>, SchemaPrimaryKey>) path -> {
                    SchemaPrimaryKey schemaPrimaryKey = new SchemaPrimaryKey();
                    schemaPrimaryKey.setColumn(path.getMetadata().getName());
                    return schemaPrimaryKey;
                }).collect(Collectors.toList()));
    }

    private static void updateIndices(SchemaTable schemaTable, QDynamicTable dynamicTable) {
        schemaTable.setIndices(dynamicTable.getIndexDatas().stream().map(indexData -> {
            SchemaIndex schemaIndex = new SchemaIndex();
            schemaIndex.setColumns(indexData.getColumns().stream()
                    .map(ModelHelper::getColumnRealName)
                    .collect(Collectors.toList()));
            schemaIndex.setUniq(indexData.isUnique());
            schemaIndex.setClustered(indexData.isClustered());
            return schemaIndex;
        }).collect(Collectors.toList()));
    }


    private static void updateSoftDelete(SchemaTable schemaTable, QDynamicTable dynamicTable) {
        if (dynamicTable.getSoftDelete() != null) {
            SchemaSoftDelete schemaSoftDelete = new SchemaSoftDelete();
            schemaSoftDelete.setColumn(ModelHelper
                    .getColumnName(dynamicTable.getSoftDelete().getColumn()));
            schemaSoftDelete.setDefaultValue(dynamicTable.getSoftDelete().getDefaultValue());
            schemaSoftDelete.setDeletedValue(dynamicTable.getSoftDelete().getDeletedValue());
            schemaTable.setSoftDeleteColumn(schemaSoftDelete);
        }
    }

    private static void updateAdditional(SchemaTable schemaTable, QDynamicTable dynamicTable) {
        PKGenerator<?> pkGenerator = dynamicTable.getPkGenerator();
        if (pkGenerator != null) {
            schemaTable.setPrimaryGeneratorType(pkGenerator.getGeneratorType().name());
            schemaTable.setSequanceName(pkGenerator.name());
        }
        schemaTable.setVersionColumn(dynamicTable.getVersionColumn() != null ?
                ModelHelper.getColumnName(dynamicTable.getVersionColumn()) : null);
        updateSoftDelete(schemaTable, dynamicTable);
    }

    private static void updateTableModel(SchemaTable schemaTable, QDynamicTable dynamicTable) {
        schemaTable.setColumns(dynamicTable.getColumns()
                .stream().map(path -> updateColumn(dynamicTable, path))
                .collect(Collectors.toList()));
        updateAdditional(schemaTable, dynamicTable);
        updateForeignKey(schemaTable, dynamicTable);
        PrimaryKey<Object> primaryKey = dynamicTable.getPrimaryKey();
        if (primaryKey != null) {
            updatePrimaryKey(schemaTable, primaryKey);
        }
        updateIndices(schemaTable, dynamicTable);
    }

    private static void updateTableModel(Schema schema, Map<String,
            QDynamicTable> dynamicTableMap) {
        schema.setTables(dynamicTableMap.entrySet().stream().map(entry -> {
            SchemaTable schemaTable = new SchemaTable();
            schemaTable.setName(entry.getKey());
            updateTableModel(schemaTable, entry.getValue());
            return schemaTable;
        }).collect(Collectors.toList()));
    }

    public static Schema transform(Map<String, QDynamicTable> dynamicTableMap,
                                   Map<String, ViewDataHolder> viewMap,
                                   Map<String, SequanceModel> sequenceModelMap) {
        Schema schema = new Schema();
        updateSequanceModel(schema, sequenceModelMap);
        updateViewModel(schema, viewMap);
        updateTableModel(schema, dynamicTableMap);
        return schema;
    }
}
