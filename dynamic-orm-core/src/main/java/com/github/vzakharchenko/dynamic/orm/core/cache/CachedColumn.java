package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.querydsl.core.types.Path;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 *
 */
public class CachedColumn implements Serializable {
    private String tableName;
    private String columnName;

    public CachedColumn() {
    }

    public CachedColumn(Path column) {
        this(ModelHelper.getTableName(column), ModelHelper.getColumnName(column));
    }

    public CachedColumn(String tableName, String columnName) {
        this.tableName = StringUtils.upperCase(tableName);
        this.columnName = StringUtils.upperCase(columnName);
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CachedColumn)) {
            return false;
        }

        CachedColumn that = (CachedColumn) o;

        return tableName.equals(that.tableName) && columnName.equals(that.columnName);

    }

    @Override
    public int hashCode() {
        int result = tableName.hashCode();
        return 31 * result + columnName.hashCode();
    }
}
