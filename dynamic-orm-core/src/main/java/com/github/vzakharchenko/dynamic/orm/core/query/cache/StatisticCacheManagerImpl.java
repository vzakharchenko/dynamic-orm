package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatistic;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;

import java.io.Serializable;
import java.util.List;

public class StatisticCacheManagerImpl<T extends Serializable> implements StatisticCacheManager<T> {

    private final TransactionalCache transactionCache;

    protected StatisticCacheManagerImpl(TransactionalCache transactionCache) {
        this.transactionCache = transactionCache;
    }

    @Override
    public List<T> get(String sqlString,
                       QueryStatistic queryStatistic,
                       StatisticCacheInvoke<T> invoke) {
        StatisticCacheHolder<T> statisticCacheHolder = transactionCache
                .getFromCache(sqlString, StatisticCacheHolder.class);
        if (statisticCacheHolder == null || !statisticCacheHolder.valid(queryStatistic,
                transactionCache)) {
            List<T> list = invoke.invoke();
            statisticCacheHolder = queryStatistic.get(transactionCache,
                    sqlString, list);
        }
        return statisticCacheHolder.getCacheValues();
    }
}
