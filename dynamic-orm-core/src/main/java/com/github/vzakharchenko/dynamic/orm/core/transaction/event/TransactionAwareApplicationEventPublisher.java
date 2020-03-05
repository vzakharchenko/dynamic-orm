package com.github.vzakharchenko.dynamic.orm.core.transaction.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction.CombinatedObject;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction.TransactionEvent;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction.TransactionEventType;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction.TransactionalCombinedEvent;

import java.io.Serializable;
import java.util.Objects;

/*
 *   TransactionAware event publisher. So all events will be sent only after transaction
 * */
public class TransactionAwareApplicationEventPublisher implements TransactionalEventPublisher {

    private ApplicationEventPublisher publisher = null;


    @Override
    public void publishEvent(final Object event) {
        Assert.notNull(publisher);
        Assert.notNull(event);
        TransactionEventType transactionEventType = getTransactionType(event);
        if (isTransactionEvent(transactionEventType) && !TransactionSynchronizationManager
                .isSynchronizationActive()) {
            throw new IllegalStateException("Transaction is Not Active");
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && !Objects.equals(transactionEventType,
                TransactionEventType.WITHOUT_SYNCHRONIZATION)) {

            if (isTransactionalCombinedEvent(event)) {
                TransactionalCombinedEvent<Object> transactionalCombinedEvent =
                        (TransactionalCombinedEvent<Object>) event;

                Serializable resourceName = transactionalCombinedEvent.resourceName();
                CombinatedObject<Object> combinatedObject =
                        (CombinatedObject<Object>) TransactionSynchronizationManager
                                .getResource(resourceName);
                if (combinatedObject == null) {
                    combinatedObject = new CombinatedObject(transactionalCombinedEvent);
                    TransactionSynchronizationManager
                            .bindResource(resourceName, combinatedObject);
                    TransactionSynchronizationManager
                            .registerSynchronization(new TransactionSynchronizationAdapter() {
                                @Override
                                public void beforeCommit(boolean readOnly) {
                                    TransactionEventType transactionEventType =
                                            getTransactionType(event);
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
                                            getTransactionType(event);
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
                            });
                } else {
                    combinatedObject.combinate(transactionalCombinedEvent);
                }
            } else {
                TransactionSynchronizationManager.registerSynchronization(
                        new TransactionSynchronizationAdapter() {
                            @Override
                            public void afterCommit() {
                                TransactionEventType transactionEventType =
                                        getTransactionType(event);
                                if (transactionEventType == null || Objects
                                        .equals(transactionEventType,
                                                TransactionEventType.AFTER_COMMIT)) {
                                    publisher.publishEvent(event);
                                }
                            }

                            @Override
                            public void beforeCommit(boolean readOnly) {
                                TransactionEventType transactionEventType =
                                        getTransactionType(event);
                                if (Objects.equals(transactionEventType,
                                        TransactionEventType.BEFORE_COMMIT)) {
                                    publisher.publishEvent(event);
                                }
                            }
                        });

            }

        } else {
            publisher.publishEvent(event);
        }
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        publishEvent((Object) event);

    }

    private boolean isTransactionEvent(TransactionEventType transactionEventType) {
        return transactionEventType != null && !Objects.equals(transactionEventType,
                TransactionEventType.WITHOUT_SYNCHRONIZATION);
    }

    private boolean isTransactionalCombinedEvent(Object event) {
        return event instanceof TransactionalCombinedEvent;
    }

    private TransactionEventType getTransactionType(Object event) {
        if (event instanceof TransactionEvent) {
            TransactionEvent transactionEvent = (TransactionEvent) event;
            return transactionEvent.getTransactionType();
        } else {
            return null;
        }
    }


    @Override
    public void setApplicationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        publisher = applicationEventPublisher;
    }

}
