package com.github.vzakharchenko.dynamic.orm.core.dynamic;


import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QNumberColumn;
import com.querydsl.core.types.Path;

/**
 *
 */
public class NumberColumnMetaDataInfo implements ColumnMetaDataInfo {


    private final Path column;

    private final Integer size;

    private final String jdbcType;

    private final Boolean nullable;

    private final Integer decimalDigits;

    private final Boolean primaryKey;

    protected NumberColumnMetaDataInfo(Path column, String type,
                                       QNumberColumn numberColumn) {
        this.column = column;
        this.jdbcType = type;
        this.size = numberColumn.size();
        this.nullable = !numberColumn.notNull();
        this.decimalDigits = numberColumn.decimalDigits();
        this.primaryKey = numberColumn.isPrimaryKey();
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
