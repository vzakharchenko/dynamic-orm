package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.core.Ordered;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.util.function.BiConsumer;

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

    private void afterCompletion(TransactionalCache transactionalCache) {
        if (transactionalCache != null) {
            transactionalCache.getInternalCache().forEach(new BiConsumer<Serializable, Serializable>() {
                @Override
                public void accept(Serializable key, Serializable value) {
                    Cache.ValueWrapper valueWrapper = targetCache.get(key);
                    if (valueWrapper != null && valueWrapper.get() != null) {
                        targetCache.evict(key);
                    } else {
                        targetCache.put(key, value);
                    }
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
