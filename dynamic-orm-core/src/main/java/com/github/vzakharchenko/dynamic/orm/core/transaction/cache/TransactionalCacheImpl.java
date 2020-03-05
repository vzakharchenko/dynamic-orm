package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import com.github.vzakharchenko.dynamic.orm.core.cache.CachedAllData;
import com.github.vzakharchenko.dynamic.orm.core.cache.PrimaryKeyCacheKey;
import org.springframework.cache.Cache;
import org.springframework.core.Ordered;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by vzakharchenko on 29.11.14.
 */
public class TransactionalCacheImpl implements TransactionalCache {

    private final Map<Serializable, Serializable> transactionCache = new HashMap<>();
    private final Map<Serializable, Serializable> evictValues = new HashMap<>();
    private final Map<Serializable, Serializable> deletedObjects = new HashMap<>();
    private final Map<Serializable, Serializable> insertedObjects = new HashMap<>();
    private final Map<Serializable, Serializable> updatedObjects = new HashMap<>();

    private final Map<Serializable, Serializable> transactionalLockedKey = new HashMap<>();

    private final Cache targetCache;

    private final CacheKeyLockStrategy cacheKeyLockStrategy;

    public TransactionalCacheImpl(Cache targetCache,
                                  CacheKeyLockStrategy cacheKeyLockStrategy) {
        this.targetCache = targetCache;
        this.cacheKeyLockStrategy = cacheKeyLockStrategy;
    }

    @Override
    public <T> T getFromCache(Serializable key, Class<T> tClass) {

        return (T) transactionCache.get(key);
    }

    @Override
    public boolean isInCache(Serializable key) {
        return transactionCache.containsKey(key);
    }

    @Override
    public boolean isDeleted(Serializable key) {
        return deletedObjects.containsKey(key);
    }


    @Override
    public void putToCache(Serializable key, Serializable value) {
        if (deletedObjects.containsKey(key)) {
            throw new IllegalAccessError("Trying to put deleted Object " + key + " to Cache");
        }

        if (putModelToTargetCache(key, value)) {
            return;
        }


        transactionCache.put(key, value);
    }

    @Override
    public void cacheEvict(Serializable key) {
        transactionCache.remove(key);
        evictValues.put(key, key);
        lock(key);
        unLock(key);
    }

    @Override
    public void deleteModel(PrimaryKeyCacheKey key) {
        cacheEvict(key);
        cacheEvict(new CachedAllData(key.getTableName()));
        deletedObjects.put(key, key);
        insertedObjects.remove(key);
        updatedObjects.remove(key);
    }

    @Override
    public void insertModel(PrimaryKeyCacheKey key) {
        cacheEvict(new CachedAllData(key.getTableName()));
        insertedObjects.put(key, key);
    }

    @Override
    public void updateModel(PrimaryKeyCacheKey key) {
        cacheEvict(new CachedAllData(key.getTableName()));
        cacheEvict(key);
        updatedObjects.put(key, key);
    }

    @Override
    public <T> T getFromTargetCache(Serializable key, Class<T> tClass) {
        if (deletedObjects.containsKey(key)) {
            return null;
        }
        Cache.ValueWrapper valueWrapper = targetCache.get(key);
        if (valueWrapper != null && valueWrapper.get() != null) {
            return (T) valueWrapper.get();
        }
        return null;
    }

    @Override
    public void lock(Serializable key) {
        if (!transactionalLockedKey.containsKey(key)) {
            cacheKeyLockStrategy.lock(key);
        }
    }

    @Override
    public void unLock(Serializable key) {
        if (!transactionalLockedKey.containsKey(key)) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCompletion(int status) {
                            cacheKeyLockStrategy.unLock(key);
                        }

                        @Override
                        public int getOrder() {
                            return Ordered.HIGHEST_PRECEDENCE + 1;
                        }
                    });
            transactionalLockedKey.put(key, key);
        }

    }

    @Override
    public void clearAll() {
        targetCache.clear();
    }

    public boolean putModelToTargetCache(Serializable key, Serializable value) {

        if (deletedObjects.containsKey(key)) {
            return false;
        }
        if (updatedObjects.containsKey(key)) {
            return false;
        }
        if (insertedObjects.containsKey(key)) {
            return false;
        }
        if (evictValues.containsKey(key)) {
            return false;
        }
        if (transactionCache.containsKey(key)) {
            return false;
        }

        targetCache.put(key, value);
        transactionCache.put(key, value);
        return true;
    }

    @Override
    public Map<Serializable, Serializable> getInternalCache() {
        return transactionCache;
    }

    @Override
    public Set<Serializable> getEvictObjects() {
        return evictValues.keySet();
    }

    @Override
    public Set<Serializable> getUpdatedObjects() {
        return updatedObjects.keySet();
    }

    @Override
    public Set<Serializable> getInsertedObjects() {
        return insertedObjects.keySet();
    }

    @Override
    public Set<Serializable> getDeletedObjects() {
        return deletedObjects.keySet();
    }

}
