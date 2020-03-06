package com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

public abstract class AbstractTransactionSynchronizationAdapter
        extends TransactionSynchronizationAdapter {

    protected final Object event;
    protected final ApplicationEventPublisher publisher;

    protected AbstractTransactionSynchronizationAdapter(Object event,
                                                        ApplicationEventPublisher publisher) {
        super();
        this.event = event;
        this.publisher = publisher;
    }

    protected TransactionEventType getTransactionType() {
        if (event instanceof TransactionEvent) {
            TransactionEvent transactionEvent = (TransactionEvent) event;
            return transactionEvent.getTransactionType();
        } else {
            return null;
        }
    }
}
