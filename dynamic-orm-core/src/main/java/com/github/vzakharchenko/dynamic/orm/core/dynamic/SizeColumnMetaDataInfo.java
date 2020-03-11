package com.github.vzakharchenko.dynamic.orm.core.dynamic;


import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QSizeColumn;
import com.querydsl.core.types.Path;

/**
 *
 */
public class SizeColumnMetaDataInfo implements ColumnMetaDataInfo {


    private final Path column;

    private final Integer size;

    private final String jdbcType;

    private final Boolean nullable;

    private final Boolean primaryKey;

    public SizeColumnMetaDataInfo(Path column, String type,
                                  QSizeColumn sizeColumn) {
        this.column = column;
        this.jdbcType = type;
        this.size = sizeColumn.size();
        this.nullable = !sizeColumn.notNull();
        this.primaryKey = sizeColumn.isPrimaryKey();
    }

    public SizeColumnMetaDataInfo(Path column, String type,
                                  Integer size,
                                  QColumn sizeColumn) {
        this.column = column;
        this.jdbcType = type;
        this.size = size;
        this.nullable = !sizeColumn.notNull();
        this.primaryKey = sizeColumn.isPrimaryKey();
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
        return null;
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
