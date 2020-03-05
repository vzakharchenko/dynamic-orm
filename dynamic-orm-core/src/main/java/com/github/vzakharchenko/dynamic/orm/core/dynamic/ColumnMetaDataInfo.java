package com.github.vzakharchenko.dynamic.orm.core.dynamic;


import com.querydsl.core.types.Path;

import java.io.Serializable;

/**
 *
 */
public class ColumnMetaDataInfo implements Serializable {


    private final Path column;

    private final Integer size;

    private final String jdbcType;

    private Integer decimalDigits;

    private boolean nullable = true;

    protected ColumnMetaDataInfo(Path column, String type, Integer size,
                                 boolean nullable, Integer decimalDigits) {
        this.column = column;
        this.jdbcType = type;
        this.nullable = nullable;
        this.size = size;
        this.decimalDigits = decimalDigits;
    }

    protected ColumnMetaDataInfo(Path column, String type,
                                 Integer size, boolean nullable) {
        this.column = column;
        this.jdbcType = type;
        this.size = size;
        this.nullable = nullable;
    }

    public Path getColumn() {
        return column;
    }

    public Integer getSize() {
        return size;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    public Boolean isNullable() {
        return nullable;
    }

}
