package com.github.vzakharchenko.dynamic.orm.core.query.cache;


import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.MapModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface RawCacheBuilder<MODEL extends DMLModel> extends CacheBuilder<MODEL> {
    Map<CompositeKey, MapModel> findAllOfMapByIds(List<CompositeKey> keys);

    boolean isPresentInCache(Serializable key);
}
