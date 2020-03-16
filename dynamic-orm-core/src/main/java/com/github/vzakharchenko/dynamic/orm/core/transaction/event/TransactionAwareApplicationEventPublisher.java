package com.github.vzakharchenko.dynamic.orm.core.transaction.event;

import com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction.*;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Objects;

/**
 * TransactionAware event publisher.  All events will be sent only after transaction.
 */
public class TransactionAwareApplicationEventPublisher implements TransactionalEventPublisher {

    private ApplicationEventPublisher publisher;


    private void checkTransaction(TransactionEventType transactionEventType) {
        if (isTransactionEvent(transactionEventType) && !TransactionSynchronizationManager
                .isSynchronizationActive()) {
            throw new IllegalStateException("Transaction is Not Active");
        }
    }

    private void publishCombineEvent(final Object event) {
        TransactionalCombinedEvent<Object> transactionalCombinedEvent =
                (TransactionalCombinedEvent<Object>) event;
        Serializable resourceName = transactionalCombinedEvent.getResourceName();
        CombinatedObject<Object> combinatedObject =
                (CombinatedObject<Object>) TransactionSynchronizationManager
                        .getResource(resourceName);
        if (combinatedObject == null) {
            combinatedObject = new CombinatedObject(transactionalCombinedEvent);
            TransactionSynchronizationManager
                    .bindResource(resourceName, combinatedObject);
            TransactionSynchronizationManager
                    .registerSynchronization(
                            new CombineTransactionSynchronizationAdapter(
                                    event, resourceName, publisher));
        } else {
            combinatedObject.combinate(transactionalCombinedEvent);
        }
    }

    @Override
    public void publishEvent(final Object event) {
        Assert.notNull(publisher);
        Assert.notNull(event);
        TransactionEventType transactionEventType = getTransactionType(event);
        checkTransaction(transactionEventType);
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && !Objects.equals(transactionEventType,
                TransactionEventType.WITHOUT_SYNCHRONIZATION)) {
            if (isTransactionalCombinedEvent(event)) {
                publishCombineEvent(event);
            } else {
                TransactionSynchronizationManager.registerSynchronization(
                        new SimpleTransactionSynchronizationAdapter(
                                event, publisher));
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

    protected TransactionEventType getTransactionType(Object event) {
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
