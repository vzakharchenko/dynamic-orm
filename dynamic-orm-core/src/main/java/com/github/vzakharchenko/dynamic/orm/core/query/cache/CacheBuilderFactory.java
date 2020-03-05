package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.querydsl.sql.RelationalPath;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;

/**
 *
 */
public abstract class CacheBuilderFactory {

    public static <MODEL extends DMLModel> CacheBuilder<MODEL> build(
            Class<MODEL> modelClass,
            RelationalPath<?> qTable,
            QueryContextImpl queryContext) {
        if (!ModelHelper.hasPrimaryKey(qTable)) {
            throw new IllegalStateException("primaryKey is not Found:" + qTable);
        }
        return new CacheBuilderImpl<>(qTable, modelClass, queryContext);
    }

}
