package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheBuilder;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.RawCacheBuilder;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;

/**
 * Created by vzakharchenko on 14.11.14.
 */
public class ModelLazyList<MODEL extends DMLModel> extends AbstractList<MODEL> {
    private static final int MAX_LAZYLIST_CACHE_SIZE = 100;
    private final List<Serializable> listIds;
    private final CacheBuilder<MODEL> cacheBuilder;

    protected ModelLazyList(List<Serializable> listIds, CacheBuilder<MODEL> cacheBuilder) {
        super();
        this.listIds = listIds;
        this.cacheBuilder = cacheBuilder;
    }

    @Override
    public MODEL get(int index) {
        Serializable key = listIds.get(index);
        if (!((RawCacheBuilder) cacheBuilder).isPresentInCache(key)) {
            int toIndex = index + MAX_LAZYLIST_CACHE_SIZE;
            cacheBuilder.findAllByIds(listIds.subList(
                    index, toIndex < listIds.size() ? toIndex : listIds.size()));
        }
        return cacheBuilder.findOneById(listIds.get(index));
    }

    @Override
    public int size() {
        return listIds.size();
    }
}
