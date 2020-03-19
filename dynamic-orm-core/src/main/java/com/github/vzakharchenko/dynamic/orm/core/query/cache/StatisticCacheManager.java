package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatistic;

import java.io.Serializable;
import java.util.List;

public interface StatisticCacheManager<T extends Serializable> {
     List<T> get(String sqlString,
                       QueryStatistic queryStatistic,
                       StatisticCacheInvoke<T> invoke);
}
