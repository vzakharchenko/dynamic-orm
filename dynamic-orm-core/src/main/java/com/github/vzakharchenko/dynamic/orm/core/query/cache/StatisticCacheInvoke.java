package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import java.io.Serializable;
import java.util.List;

public interface StatisticCacheInvoke<T extends Serializable> {
    List<T> invoke();
}
