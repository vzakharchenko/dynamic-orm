package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.querydsl.core.types.Path;

import java.io.Serializable;

/**
 *
 */
public class CachedColumnWithValue extends CachedColumn {
    private Serializable value;

    public CachedColumnWithValue() {
        super();
    }

    public CachedColumnWithValue(
            String tableName, String columnName, Serializable value) {
        super(tableName, columnName);
        this.value = value;
    }

    public CachedColumnWithValue(Path column, Serializable value) {
        super(ModelHelper.getTableName(column), ModelHelper.getColumnName(column));
        this.value = value;
    }


    public Serializable getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CachedColumnWithValue)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        CachedColumnWithValue that = (CachedColumnWithValue) o;

        return !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        return 31 * result + (value != null ? value.hashCode() : 0);
    }
}
