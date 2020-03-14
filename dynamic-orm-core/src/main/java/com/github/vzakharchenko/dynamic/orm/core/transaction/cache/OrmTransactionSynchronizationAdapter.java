package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.core.Ordered;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class OrmTransactionSynchronizationAdapter extends TransactionSynchronizationAdapter {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(OrmTransactionSynchronizationAdapter.class);
    public static final String TRANSACTION_NAME = ". Transaction Name:";

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

    private void afterCompletion(TransactionalCache transactionalCache) {
        if (transactionalCache != null) {
            transactionalCache.getEvictObjects().forEach(evictKey -> {
                LOGGER.debug("Cleaning  " + evictKey +
                        TRANSACTION_NAME + transactionName);
                targetCache.evict(evictKey);
            });
            transactionalCache.getDeletedObjects().forEach(evictKey -> {
                LOGGER.debug("delete model  " + evictKey +
                        TRANSACTION_NAME + transactionName);
                targetCache.evict(evictKey);
            });
            transactionalCache.getInsertedObjects().forEach(evictKey -> {
                LOGGER.debug("added new model  " + evictKey +
                        TRANSACTION_NAME + transactionName);
                targetCache.evict(evictKey);
            });
            transactionalCache.getUpdatedObjects().forEach(evictKey -> {
                LOGGER.debug("updated model  " + evictKey +
                        TRANSACTION_NAME + transactionName);
                targetCache.evict(evictKey);
            });
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
