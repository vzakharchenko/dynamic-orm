package com.github.vzakharchenko.dynamic.orm.core.query.cache;


import com.github.vzakharchenko.dynamic.orm.core.cache.MapModel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface RawCacheBuilder {
    Map<Serializable, MapModel> findAllOfMapByIds(List<? extends Serializable> keys);

    boolean isPresentInCache(Serializable key);
}
