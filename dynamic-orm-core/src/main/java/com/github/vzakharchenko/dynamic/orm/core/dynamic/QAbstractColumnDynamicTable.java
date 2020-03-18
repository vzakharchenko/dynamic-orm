package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Date;


/**
 *
 */
public abstract class QAbstractColumnDynamicTable<DYNAMIC_TABLE extends QAbstractColumnDynamicTable>
        extends QAbstractDynamicTable<DYNAMIC_TABLE> {

    protected QAbstractColumnDynamicTable(String tableName) {
        super(tableName);
    }

    @Override
    public Path<?> getColumnByName(String columnName) {
        return columns.get(StringUtils.upperCase(columnName));
    }

    public <T> SimpleExpression<T> getColumnByName(String columnName, Class<T> tClass) {
        Path<?> column = getColumnByName(columnName);
        if (column == null) {
            throw new IllegalStateException("column " + columnName +
                    " is not found in table " + getTableName());
        }
        Assert.isTrue(tClass.isAssignableFrom(column.getType()));
        return (SimpleExpression<T>) column;
    }

    public StringPath getStringColumnByName(String columnName) {
        return (StringPath) getColumnByName(columnName, String.class);
    }

    public StringPath getCharColumnByName(String columnName) {
        return getStringColumnByName(columnName);
    }

    public StringPath getClobColumnByName(String columnName) {
        return getStringColumnByName(columnName);
    }

    public BooleanPath getBooleanColumnByName(String columnName) {
        return (BooleanPath) getColumnByName(columnName, Boolean.class);
    }

    public SimplePath<byte[]> getBlobColumnByName(String columnName) {
        return (SimplePath<byte[]>) getColumnByName(columnName, byte[].class);
    }

    public DatePath<Date> getDateColumnByName(String columnName) {
        return (DatePath<Date>) getColumnByName(columnName, Date.class);
    }

    public DateTimePath<Date> getDateTimeColumnByName(String columnName) {
        return (DateTimePath<Date>) getColumnByName(columnName, Date.class);
    }

    public TimePath<Date> getTimeColumnByName(String columnName) {
        return (TimePath<Date>) getColumnByName(columnName, Date.class);
    }

    public <T extends Number & Comparable<?>> NumberPath<T> getNumberColumnByName(
            String columnName, Class<T> tClass) {
        return (NumberPath<T>) getColumnByName(columnName, tClass);
    }

    public <T extends Number & Comparable<?>> NumberPath<T> getNumberColumnByName(
            String columnName) {
        return (NumberPath) getColumnByName(columnName, Number.class);
    }
}
