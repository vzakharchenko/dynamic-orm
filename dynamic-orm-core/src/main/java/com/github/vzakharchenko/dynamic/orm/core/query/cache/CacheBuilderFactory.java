package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.querydsl.sql.RelationalPath;

/**
 *
 */
public abstract class CacheBuilderFactory {

    public static <MODEL extends DMLModel> CacheBuilder<MODEL> build(
            Class<MODEL> modelClass,
            RelationalPath<?> qTable,
            QueryContextImpl queryContext) {
        if (!PrimaryKeyHelper.hasPrimaryKey(qTable)) {
            throw new IllegalStateException("primaryKey is not Found:" + qTable);
        }
        return new CacheBuilderImpl<>(qTable, modelClass, queryContext);
    }

}
