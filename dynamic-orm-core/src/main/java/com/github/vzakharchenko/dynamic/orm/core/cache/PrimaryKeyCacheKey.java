package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 *
 */
public class PrimaryKeyCacheKey implements Serializable {
    private CompositeKey key;

    private String tableName;

    public PrimaryKeyCacheKey() {
        super();
    }

    public PrimaryKeyCacheKey(CompositeKey key) {
        this.key = key;
        this.tableName = StringUtils.upperCase(ModelHelper.getTableName(key.getTable()));
    }

    public CompositeKey getKey() {
        return key;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrimaryKeyCacheKey)) {
            return false;
        }

        PrimaryKeyCacheKey that = (PrimaryKeyCacheKey) o;

        if (!key.equals(that.key)) {
            return false;
        }
        return tableName.equals(that.tableName);

    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        return 31 * result + tableName.hashCode();
    }
}
