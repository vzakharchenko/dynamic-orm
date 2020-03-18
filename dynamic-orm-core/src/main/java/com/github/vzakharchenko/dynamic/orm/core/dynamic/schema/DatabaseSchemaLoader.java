package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models.Schema;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models.*;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.DynamicStructureSaver;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.DynamicStructureUpdater;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.structure.core.*;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseSchemaLoader implements SchemaLoader {

    private final DynamicStructureUpdater dynamicStructureSaver;

    public DatabaseSchemaLoader(DataSource dataSource) {
        this.dynamicStructureSaver = new DynamicStructureSaver(dataSource);
    }

    private SchemaColumn createSchemaColumn(Column column) {
        SchemaColumn schemaColumn = new SchemaColumn();
        schemaColumn.setName(column.getName());
        schemaColumn.setNullable(column.isNullable());
        DataType type = column.getType();
        schemaColumn.setJdbcType(type.getTypeName());
        schemaColumn.setJdbcTypeid(type.getDataTypeId());
        schemaColumn.setSize(type.getColumnSize());
        schemaColumn.setDecimalDigits(type.getDecimalDigits());
        Class sqlType = JavaTypeUtils.getBySqlType(type.getDataTypeId());
        Assert.notNull(sqlType, "DataType " + type + "does not supported");
        schemaColumn.setaType(sqlType.getCanonicalName());
        schemaColumn.setClassName(JavaTypeUtils.getPathClass(type.getDataTypeId()).getSimpleName());
        return schemaColumn;
    }

    private List<SchemaPrimaryKey> createPrimaryKeys(PrimaryKey primaryKey) {
        List<SchemaPrimaryKey> primaryKeys = new ArrayList<>();
        if (primaryKey != null) {
            primaryKeys.addAll(primaryKey.getColumns().stream().map(column -> {
                SchemaPrimaryKey schemaPrimaryKey = new SchemaPrimaryKey();
                schemaPrimaryKey.setColumn(column.getName());
                return schemaPrimaryKey;
            }).collect(Collectors.toList()));
        }
        return primaryKeys;
    }

    private List<SchemaIndex> createIndices(List<Index> indexes) {
        List<SchemaIndex> indices = new ArrayList<>();
        if (indexes != null) {
            indices.addAll(indexes.stream().map(index -> {
                SchemaIndex schemaIndex = new SchemaIndex();
                schemaIndex.setUniq(index.isUnique());
                schemaIndex.setClustered(index.getClustered());
                schemaIndex.setColumns(getColumnNames(index.getColumns()));
                return schemaIndex;
            }).collect(Collectors.toList()));
        }
        return indices;
    }


    private SchemaTable createTable(Table table) {
        SchemaTable schemaTable = new SchemaTable();
        schemaTable.setName(table.getName());
        schemaTable.setColumns(
                table.getColumns().stream()
                        .map(this::createSchemaColumn)
                        .collect(Collectors.toList())
        );
        schemaTable.setPrimaryKeys(createPrimaryKeys(table.getPrimaryKey()));
        schemaTable.setIndices(createIndices(table.getIndexes()));
        schemaTable.setForeignKeys(createForeignKeys(table.getOutgoingForeignKeys()));
        return schemaTable;
    }

    private List<String> getColumnNames(List<Column> columns) {
        List<String> columnNames = new ArrayList<>();
        if (columns != null) {
            columnNames.addAll(columns.stream().map(Column::getName).collect(Collectors.toList()));
        }
        return columnNames;
    }

    private List<SchemaForeignKey> createForeignKeys(List<ForeignKey> foreignKeys) {
        List<SchemaForeignKey> foreignKeyList = new ArrayList<>();
        if (foreignKeys != null) {
            foreignKeyList.addAll(foreignKeys.stream().map(foreignKey -> {
                SchemaForeignKey schemaForeignKey = new SchemaForeignKey();
                schemaForeignKey.setRemoteColumns(getColumnNames(foreignKey.getForeignKeyColumns()));
                schemaForeignKey.setLocalColumns(getColumnNames(foreignKey.getPrimaryKeyColumns()));
                schemaForeignKey.setTable(foreignKey.getForeignKeyTable().getName());
                schemaForeignKey.setDynamicRemote(true);
                return schemaForeignKey;
            }).collect(Collectors.toList()));
        }
        return foreignKeyList;
    }

    private void addTables(Schema schema, Set<Table> tables) {
        if (tables != null) {
            schema.setTables(tables.stream().map(this::createTable)
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public Schema load() {
        DatabaseSnapshot databaseSnapshot = dynamicStructureSaver.getDatabaseSnapshot();
        Schema schema = new Schema();
        Set<Table> tables = databaseSnapshot.get(Table.class);
        addTables(schema, tables);
        return schema;
    }
}
