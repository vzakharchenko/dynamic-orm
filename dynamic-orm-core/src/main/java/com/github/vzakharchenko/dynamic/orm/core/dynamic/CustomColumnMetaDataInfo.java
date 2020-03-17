package com.github.vzakharchenko.dynamic.orm.core.dynamic;


import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QCustomColumn;
import com.querydsl.core.types.Path;

/**
 *
 */
public class CustomColumnMetaDataInfo implements ColumnMetaDataInfo {


    private final Path column;

    private final Integer size;

    private final String jdbcType;

    private final Boolean nullable;

    private final Integer decimalDigits;

    private final Boolean primaryKey;

    protected CustomColumnMetaDataInfo(QCustomColumn customColumn) {
        this.column = customColumn.customColumn();
        this.jdbcType = customColumn.jdbcType();
        this.size = customColumn.size();
        this.nullable = !customColumn.notNull();
        this.decimalDigits = customColumn.decimalDigits();
        this.primaryKey = customColumn.isPrimaryKey();
    }

    @Override
    public Path getColumn() {
        return column;
    }

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public String getJdbcType() {
        return jdbcType;
    }

    @Override
    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    @Override
    public Boolean isNullable() {
        return nullable;
    }

    @Override
    public Boolean isPrimaryKey() {
        return primaryKey;
    }

}
