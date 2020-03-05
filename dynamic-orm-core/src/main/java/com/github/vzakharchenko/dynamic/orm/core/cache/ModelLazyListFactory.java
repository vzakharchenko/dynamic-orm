package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.querydsl.sql.RelationalPath;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vzakharchenko on 13.11.14.
 */
public abstract class ModelLazyListFactory {

    static <MODEL extends DMLModel> List<MODEL> buildModelLazyList(
            List<Serializable> keys, CacheBuilder<MODEL> cacheBuilder) {
        try {
            return new ModelLazyList<>(keys, cacheBuilder);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static <MODEL extends DMLModel> LazyList<MODEL> buildLazyList(
            RelationalPath<?> qTable, List<Serializable> keys,
            Class<MODEL> modelClass, QueryContextImpl queryContext) {
        try {
            return new LazyListImpl<>(qTable, keys, modelClass, queryContext);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

}
