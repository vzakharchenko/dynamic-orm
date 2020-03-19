package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.StatisticCacheKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.core.Ordered;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class OrmTransactionSynchronizationAdapter extends TransactionSynchronizationAdapter {

    public static final String TRANSACTION_NAME = ". Transaction Name:";
    private static final Logger LOGGER = LoggerFactory
            .getLogger(OrmTransactionSynchronizationAdapter.class);
    private final String cacheName;
    private final String transactionName;
    private final Cache targetCache;


    public OrmTransactionSynchronizationAdapter(String cacheName,
                                                String transactionName,
                                                Cache targetCache) {
        super();
        this.cacheName = cacheName;
        this.transactionName = transactionName;
        this.targetCache = targetCache;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private void evict(String message, Serializable evictKey) {
        LOGGER.debug(message + evictKey +
                TRANSACTION_NAME + transactionName);
        evict(evictKey);
    }


    private void evict(String cacheValue, StatisticCacheKey key, Serializable value) {
        if (!Objects.equals(cacheValue, value)) {
            targetCache.evict(key);
        }
    }

    private void afterCompletion(StatisticCacheKey key, Serializable value) {
        String s = targetCache.get(key, String.class);
        if (s != null) {
            evict(s, key, value);
        } else {
            targetCache.put(key, value);
        }
    }

    private void afterCompletion(TransactionalCache transactionalCache) {
        if (transactionalCache != null) {
            transactionalCache.getInternalCache().forEach((key, value) -> {
                if (key instanceof StatisticCacheKey) {
                    afterCompletion((StatisticCacheKey) key, value);
                } else {
                    targetCache.put(key, value);
                }
            });
            transactionalCache.getEvictObjects().forEach(evictKey ->
                    evict("Cleaning ", evictKey));
            transactionalCache.getDeletedObjects().forEach(evictKey ->
                    evict("Delete model ", evictKey));
            transactionalCache.getInsertedObjects().forEach(evictKey ->
                    evict("Insert model ", evictKey));
            transactionalCache.getUpdatedObjects().forEach(evictKey ->
                    evict("Update model ", evictKey));
        }
    }

    private void evict(CompositeKey compositeKey) {
        String key = StringUtils.upperCase(compositeKey.getTable().getTableName());
        targetCache.evict(key);
        targetCache.put(new StatisticCacheKey(key), UUID.randomUUID().toString());
    }

    private void evict(Serializable evictKey) {
        if (evictKey instanceof CompositeKey) {
            CompositeKey compositeKey = (CompositeKey) evictKey;
            evict(compositeKey);
        } else {
            targetCache.evict(evictKey);
        }
    }

    @Override
    public void afterCompletion(int status) {
        if (status == STATUS_COMMITTED) {
            LOGGER.debug("Starting merge Transaction cache for " +
                    cacheName + " Cache. Transaction Name:" +
                    transactionName);
            TransactionalCache transactionalCache =
                    (TransactionalCache)
                            TransactionSynchronizationManager
                                    .getResource(cacheName);
            afterCompletion(transactionalCache);
        }
        TransactionSynchronizationManager.unbindResource(cacheName);
    }
}
