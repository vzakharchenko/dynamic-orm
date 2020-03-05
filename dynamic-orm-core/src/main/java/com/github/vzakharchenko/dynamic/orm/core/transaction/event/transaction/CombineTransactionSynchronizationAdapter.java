package com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.util.Objects;

public class CombineTransactionSynchronizationAdapter
        extends AbstractTransactionSynchronizationAdapter {

    private final Serializable resourceName;

    public CombineTransactionSynchronizationAdapter(Object event,
                                                    Serializable resourceName,
                                                    ApplicationEventPublisher publisher) {
        super(event, publisher);
        this.resourceName = resourceName;
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        TransactionEventType transactionEventType =
                getTransactionType();
        if (Objects.equals(transactionEventType,
                TransactionEventType.BEFORE_COMMIT)) {
            CombinatedObject<Object> combinatedObject =
                    (CombinatedObject<Object>)
                            TransactionSynchronizationManager
                                    .getResource(resourceName);
            if (combinatedObject != null) {
                publisher.publishEvent(combinatedObject
                        .getTransactionalCombinedEvent());
            }
            TransactionSynchronizationManager
                    .unbindResource(resourceName);
        }
    }

    @Override
    public void afterCommit() {
        TransactionEventType transactionEventType =
                getTransactionType();
        if (transactionEventType == null || Objects
                .equals(transactionEventType,
                        TransactionEventType.AFTER_COMMIT)) {
            CombinatedObject<Object> combinatedObject =
                    (CombinatedObject<Object>)
                            TransactionSynchronizationManager
                                    .getResource(resourceName);
            if (combinatedObject != null) {
                publisher.publishEvent(combinatedObject
                        .getTransactionalCombinedEvent());
            }
            TransactionSynchronizationManager
                    .unbindResource(resourceName);
        }
    }
}
