package com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction;

/**
 *
 */
public class CombinatedObject<EVENT> {
    private TransactionalCombinedEvent<EVENT> transactionalCombinedEvent;

    public CombinatedObject(TransactionalCombinedEvent<EVENT> transactionalCombinedEvent) {
        this.transactionalCombinedEvent = transactionalCombinedEvent;
    }

    public void combinate(EVENT event) {
        transactionalCombinedEvent.combinate(event);
    }

    public TransactionEventType transactionEventType() {
        return transactionalCombinedEvent.getTransactionType();
    }

    public TransactionalCombinedEvent<EVENT> getTransactionalCombinedEvent() {
        return transactionalCombinedEvent;
    }
}
