package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatistic;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class StatisticCacheHolder<T extends Serializable> implements Serializable {
    private final List<T> cacheValues;
    private final String uuid;

    public StatisticCacheHolder(List<T> cacheValues, String uuid) {
        this.cacheValues = cacheValues;
        this.uuid = uuid;
    }

    public List<T> getCacheValues() {
        return cacheValues;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean valid(QueryStatistic queryStatistic, TransactionalCache transactionCache) {
        return queryStatistic.getTables().stream().allMatch(qTable -> {
            StatisticCacheKey key = new StatisticCacheKey(qTable.getTableName());
            String id = transactionCache.getFromCache(
                    key, String.class);
            if (id == null) {
                transactionCache.putToCache(key, UUID.randomUUID().toString());
            }
            return StringUtils.equalsIgnoreCase(id, uuid);
        });
    }
}
