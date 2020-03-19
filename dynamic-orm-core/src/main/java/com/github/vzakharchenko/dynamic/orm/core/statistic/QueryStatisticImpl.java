package com.github.vzakharchenko.dynamic.orm.core.statistic;

import com.github.vzakharchenko.dynamic.orm.core.query.cache.StatisticCacheHolder;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.StatisticCacheKey;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import com.google.common.collect.ImmutableList;
import com.querydsl.sql.RelationalPath;

import java.io.Serializable;
import java.util.*;

/**
 *
 */
public class QueryStatisticImpl implements QueryStatistic, QueryStatisticRegistrator {
    private final Set<RelationalPath> qTables = new HashSet<>();

    @Override
    public void register(RelationalPath<?> qTable) {
        qTables.add(qTable);
    }

    @Override
    public void register(Collection<RelationalPath> qTables0) {
        this.qTables.addAll(qTables0);
    }

    @Override
    public List<RelationalPath> getTables() {
        return ImmutableList.copyOf(qTables);
    }

    private String getUuid(TransactionalCache transactionalCache, StatisticCacheKey key) {
        String uuid = transactionalCache.getFromCache(key, String.class);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        return uuid;
    }

    @Override
    public StatisticCacheHolder get(TransactionalCache transactionalCache,
                                    String sql,
                                    List<? extends Serializable> primaryKeys) {
        Map<StatisticCacheKey, String> uuids = new HashMap<>();
        getTables().forEach(qTable -> {
            StatisticCacheKey key = new StatisticCacheKey(qTable.getTableName());
            String uuid = getUuid(transactionalCache, key);
            transactionalCache
                    .putToCache(key, uuid);
            uuids.put(key, uuid);
        });
        StatisticCacheHolder statisticCacheHolder = new StatisticCacheHolder(primaryKeys, uuids);
        transactionalCache.putToCache(sql, statisticCacheHolder);
        return statisticCacheHolder;
    }


}
