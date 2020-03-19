package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatistic;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StatisticCacheHolder<T extends Serializable> implements Serializable {
    private final List<T> cacheValues;
    private final Map<StatisticCacheKey, String> uuids = new HashMap<>();

    public StatisticCacheHolder(List<T> cacheValues, Map<StatisticCacheKey, String> uuids) {
        this.cacheValues = cacheValues;
        this.uuids.putAll(uuids);
    }

    public List<T> getCacheValues() {
        return cacheValues;
    }

    public boolean valid(QueryStatistic queryStatistic, TransactionalCache transactionCache) {
        return queryStatistic.getTables().stream().allMatch(qTable -> {
            StatisticCacheKey key = new StatisticCacheKey(qTable.getTableName());
            String uuid = uuids.get(key);
            String id = transactionCache.getFromCache(
                    key, String.class);
            if (id == null) {
                transactionCache.putToCache(key, UUID.randomUUID().toString());
            }
            return StringUtils.equalsIgnoreCase(id, uuid);
        });
    }
}
