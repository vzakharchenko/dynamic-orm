package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.querydsl.core.types.Path;

public class ModifyColumnMetaDataInfoImpl
        implements ModifyColumnMetaDataInfo {
    private final ColumnMetaDataInfo columnMetaDataInfo;
    private Path column;
    private Integer size;
    private Integer decimalDigits;
    private Boolean nullable;
    private Boolean primaryKey;

    public ModifyColumnMetaDataInfoImpl(ColumnMetaDataInfo columnMetaDataInfo) {
        this.columnMetaDataInfo = columnMetaDataInfo;
    }

    @Override
    public Path getColumn() {
        return column == null ? columnMetaDataInfo.getColumn() : column;
    }

    @Override
    public void setColumn(Path column) {
        this.column = column;
    }

    @Override
    public Integer getSize() {
        return size == null ? columnMetaDataInfo.getSize() : size;
    }

    @Override
    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String getJdbcType() {
        return columnMetaDataInfo.getJdbcType();
    }

    @Override
    public Integer getDecimalDigits() {
        return decimalDigits == null ? columnMetaDataInfo.getDecimalDigits() : decimalDigits;
    }

    @Override
    public void setDecimalDigits(Integer decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    @Override
    public Boolean isNullable() {
        return nullable == null ? columnMetaDataInfo.isNullable() : nullable;
    }

    @Override
    public Boolean isPrimaryKey() {
        return primaryKey == null ? columnMetaDataInfo.isPrimaryKey() : primaryKey;
    }

    @Override
    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }
}
