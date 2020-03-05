package com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction;

import org.springframework.context.ApplicationEventPublisher;

import java.util.Objects;

public class SimpleTransactionSynchronizationAdapter
        extends AbstractTransactionSynchronizationAdapter {

    public SimpleTransactionSynchronizationAdapter(Object event,
                                                   ApplicationEventPublisher publisher) {
        super(event, publisher);
    }

    @Override
    public void afterCommit() {
        TransactionEventType transactionEventType =
                getTransactionType();
        if (transactionEventType == null || Objects
                .equals(transactionEventType,
                        TransactionEventType.AFTER_COMMIT)) {
            publisher.publishEvent(event);
        }
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        TransactionEventType transactionEventType =
                getTransactionType();
        if (Objects.equals(transactionEventType,
                TransactionEventType.BEFORE_COMMIT)) {
            publisher.publishEvent(event);
        }
    }
}
