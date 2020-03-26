package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import com.github.vzakharchenko.dynamic.orm.core.helper.CacheHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import org.springframework.cache.Cache;

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

    private final Cache targetCache;


    public TransactionalCacheImpl(Cache targetCache) {
        this.targetCache = targetCache;
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
    }

    @Override
    public void deleteModel(CompositeKey key) {
        cacheEvict(key);
        cacheEvict(CacheHelper.buildAllDataCache(key.getTable()));
        deletedObjects.put(key, key);
        insertedObjects.remove(key);
        updatedObjects.remove(key);
    }

    @Override
    public void insertModel(CompositeKey key) {
        cacheEvict(CacheHelper.buildAllDataCache(key.getTable()));
        insertedObjects.put(key, key);
    }

    @Override
    public void updateModel(CompositeKey key) {
        cacheEvict(CacheHelper.buildAllDataCache(key.getTable()));
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
