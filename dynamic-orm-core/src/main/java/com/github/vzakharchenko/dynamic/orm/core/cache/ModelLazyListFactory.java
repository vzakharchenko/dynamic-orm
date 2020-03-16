package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheBuilder;
import com.querydsl.sql.RelationalPath;

import java.util.List;

/**
 * Created by vzakharchenko on 13.11.14.
 */
public abstract class ModelLazyListFactory {

    public static <MODEL extends DMLModel> List<MODEL> buildModelLazyList(
            List<CompositeKey> keys, CacheBuilder<MODEL> cacheBuilder) {
        return new ModelLazyList<MODEL>(keys, cacheBuilder);
    }

    public static <MODEL extends DMLModel> LazyList<MODEL> buildLazyList(
            RelationalPath<?> qTable, List<CompositeKey> keys,
            Class<MODEL> modelClass, QueryContextImpl queryContext) {
        return new LazyListImpl<>(qTable, keys, modelClass, queryContext);
    }

}
