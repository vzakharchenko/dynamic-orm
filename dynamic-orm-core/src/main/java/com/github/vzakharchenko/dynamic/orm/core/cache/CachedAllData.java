package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.querydsl.sql.RelationalPath;
import org.apache.commons.lang3.StringUtils;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;

import java.io.Serializable;

/**
 *
 */
public class CachedAllData implements Serializable {
    private String tableName;

    public CachedAllData() {
    }


    public CachedAllData(String tableName) {
        this.tableName = StringUtils
                .upperCase(tableName);
    }

    public CachedAllData(RelationalPath<?> qTable) {
        this.tableName = StringUtils.upperCase(ModelHelper.getTableName(qTable));
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CachedAllData)) {
            return false;
        }

        CachedAllData that = (CachedAllData) o;

        return tableName.equals(that.tableName);

    }

    @Override
    public int hashCode() {
        return tableName.hashCode();
    }
}
