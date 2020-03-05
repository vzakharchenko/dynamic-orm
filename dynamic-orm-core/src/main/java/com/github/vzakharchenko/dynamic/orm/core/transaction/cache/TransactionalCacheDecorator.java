package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.PrimaryKeyCacheKey;
import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Created by vzakharchenko on 29.11.14.
 */
public class TransactionalCacheDecorator implements TransactionalCache {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(TransactionalCacheDecorator.class);

    private final Cache targetCache;


    private final CacheKeyLockStrategy cacheKeyLockStrategy;


    public TransactionalCacheDecorator(Cache targetCache,
                                       CacheKeyLockStrategy cacheKeyLockStrategy) {
        this.targetCache = targetCache;
        this.cacheKeyLockStrategy = cacheKeyLockStrategy;
    }

    private TransactionalCacheImpl getTransactionCache() {
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();

        final String cacheName = targetCache.getName();
        TransactionalCacheImpl transactionalCache =
                (TransactionalCacheImpl) TransactionSynchronizationManager
                        .getResource(cacheName);
        if (transactionalCache != null) {
            return transactionalCache;
        }
        LOGGER.info("Starting Transaction cache for " +
                cacheName + " transactionName:" + transactionName);

        transactionalCache = new TransactionalCacheImpl(targetCache, cacheKeyLockStrategy);
        TransactionSynchronizationManager.registerSynchronization(
                new OrmTransactionSynchronizationAdapter(cacheName, transactionName, targetCache));
        TransactionSynchronizationManager.bindResource(cacheName, transactionalCache);
        return transactionalCache;
    }


    public <T> T getFromActiveTransaction(Serializable key, Class<T> tClass) {
        TransactionalCacheImpl transactionalCache = getTransactionCache();
        T value = transactionalCache.getFromCache(key, tClass);
        if (value == null) {
            if (transactionalCache.getEvictObjects().contains(key)) {
                return null;
            }
            Cache.ValueWrapper valueWrapper = targetCache.get(key);
            value = valueWrapper != null ? (T) valueWrapper.get() : null;
            if (value instanceof DMLModel) {
                value = (T) ModelHelper.cloneModel((DMLModel) value);
            }
            if (value != null) {
                transactionalCache.putToCache(key, (Serializable) value);
            }
        }
        return value;
    }

    @Override
    public <T> T getFromCache(Serializable key, Class<T> tClass) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            return getFromActiveTransaction(key, tClass);
        } else {
            Cache.ValueWrapper valueWrapper = targetCache.get(key);
            Object value = valueWrapper != null ? valueWrapper.get() : null;
            if (value instanceof DMLModel) {
                return (T) ModelHelper.cloneModel((DMLModel) value);
            }
            return (T) value;
        }
    }

    @Override
    public boolean isInCache(Serializable key) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionalCache transactionalCache = getTransactionCache();
            if (transactionalCache.isInCache(key)) {
                return true;
            }
        }
        Cache.ValueWrapper valueWrapper = targetCache.get(key);
        return valueWrapper != null && valueWrapper.get() != null;
    }

    @Override
    public boolean isDeleted(Serializable key) {
        DBHelper.transactionCheck();
        TransactionalCache transactionalCache = getTransactionCache();
        return transactionalCache.isDeleted(key);
    }

    @Override
    public void putToCache(Serializable key, Serializable value) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionalCache transactionalCache = getTransactionCache();
            transactionalCache.putToCache(key, value);
        } else {
            targetCache.put(key, value);
        }
    }

    @Override
    public void cacheEvict(Serializable key) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionalCache transactionalCache = getTransactionCache();
            transactionalCache.cacheEvict(key);
        } else {
            targetCache.evict(key);
        }
    }

    @Override
    public void deleteModel(PrimaryKeyCacheKey key) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionalCache transactionalCache = getTransactionCache();
            transactionalCache.deleteModel(key);
        } else {
            targetCache.evict(key);
        }
    }

    @Override
    public void insertModel(PrimaryKeyCacheKey key) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionalCache transactionalCache = getTransactionCache();
            transactionalCache.insertModel(key);
        } else {
            cacheEvict(key);
        }
    }

    @Override
    public void updateModel(PrimaryKeyCacheKey key) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionalCache transactionalCache = getTransactionCache();
            transactionalCache.updateModel(key);
        } else {
            cacheEvict(key);
        }
    }


    @Override
    public Map<Serializable, Serializable> getInternalCache() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionalCache transactionalCache = getTransactionCache();
            return transactionalCache.getInternalCache();
        } else {
            throw new IllegalAccessError("Trying to get InternalCache" +
                    " without active transaction");
        }
    }


    @Override
    public Set<Serializable> getEvictObjects() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionalCache transactionalCache = getTransactionCache();
            return transactionalCache.getEvictObjects();
        } else {
            throw new IllegalAccessError("Trying to get EvictObjects" +
                    " without active transaction");
        }
    }

    @Override
    public Set<Serializable> getUpdatedObjects() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionalCache transactionalCache = getTransactionCache();
            return transactionalCache.getUpdatedObjects();
        }
        return null;
    }

    @Override
    public Set<Serializable> getInsertedObjects() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionalCache transactionalCache = getTransactionCache();
            return transactionalCache.getInsertedObjects();
        }
        return null;
    }

    @Override
    public Set<Serializable> getDeletedObjects() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionalCache transactionalCache = getTransactionCache();
            return transactionalCache.getDeletedObjects();
        }
        return null;
    }

    @Override
    public <T> T getFromTargetCache(Serializable key, Class<T> tClass) {

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionalCache transactionalCache = getTransactionCache();
            return transactionalCache.getFromTargetCache(key, tClass);
        } else {
            Cache.ValueWrapper valueWrapper = targetCache.get(key);
            if (valueWrapper != null && valueWrapper.get() != null) {
                return (T) valueWrapper.get();
            }
            return null;
        }

    }


    @Override
    public void lock(Serializable key) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionalCache transactionalCache = getTransactionCache();
            transactionalCache.lock(key);
        } else {
            cacheKeyLockStrategy.lock(key);
        }
    }

    @Override
    public void unLock(Serializable key) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionalCache transactionalCache = getTransactionCache();
            transactionalCache.unLock(key);
        } else {
            cacheKeyLockStrategy.unLock(key);
        }
    }

    @Override
    public void clearAll() {
        targetCache.clear();
    }
}
