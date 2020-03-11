package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models;

import java.io.Serializable;
import java.util.List;

public class SchemaTable implements Serializable {
    private String name;
    private List<SchemaColumn> columns;
    private List<SchemaPrimaryKey> primaryKeys;
    private List<SchemaForeignKey> foreignKeys;
    private List<SchemaIndex> indices;

    private String primaryGeneratorType;
    private String sequanceName;
    private String versionColumn;
    private SchemaSoftDelete softDeleteColumn;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SchemaColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<SchemaColumn> columns) {
        this.columns = columns;
    }

    public List<SchemaPrimaryKey> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<SchemaPrimaryKey> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public List<SchemaForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<SchemaForeignKey> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public List<SchemaIndex> getIndices() {
        return indices;
    }

    public void setIndices(List<SchemaIndex> indices) {
        this.indices = indices;
    }

    public String getPrimaryGeneratorType() {
        return primaryGeneratorType;
    }

    public void setPrimaryGeneratorType(String primaryGeneratorType) {
        this.primaryGeneratorType = primaryGeneratorType;
    }

    public String getSequanceName() {
        return sequanceName;
    }

    public void setSequanceName(String sequanceName) {
        this.sequanceName = sequanceName;
    }

    public String getVersionColumn() {
        return versionColumn;
    }

    public void setVersionColumn(String versionColumn) {
        this.versionColumn = versionColumn;
    }

    public SchemaSoftDelete getSoftDeleteColumn() {
        return softDeleteColumn;
    }

    public void setSoftDeleteColumn(SchemaSoftDelete softDeleteColumn) {
        this.softDeleteColumn = softDeleteColumn;
    }
}
