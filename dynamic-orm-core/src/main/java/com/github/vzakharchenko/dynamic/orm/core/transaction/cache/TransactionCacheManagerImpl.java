package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by vzakharchenko on 04.12.14.
 */
public class TransactionCacheManagerImpl implements TransactionCacheManager {
    private final Map<String, TransactionalCache> cacheNames = new ConcurrentHashMap<>();

    private final CacheManager targetCacheManager;

    private CacheKeyLockStrategy cacheKeyLockStrategy = new SimpleCacheKeyLockStrategy();

    @Autowired
    public TransactionCacheManagerImpl(CacheManager targetCacheManager) {
        this.targetCacheManager = targetCacheManager;
    }

    @Override
    public TransactionalCache getTransactionalCache(String name) {
        if (cacheNames.get(name) == null) {
            Cache cache = targetCacheManager.getCache(name);
            TransactionalCache transactionalCache = new TransactionalCacheDecorator(cache,
                    cacheKeyLockStrategy);
            cacheNames.put(name, transactionalCache);
        }

        return cacheNames.get(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheNames.keySet();
    }

    public void setCacheKeyLockStrategy(CacheKeyLockStrategy cacheKeyLockStrategy) {
        this.cacheKeyLockStrategy = cacheKeyLockStrategy;
    }
}
