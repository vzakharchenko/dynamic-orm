package com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction;

/**
 * this interface  marks the event as a transactional event
 */
public interface TransactionEvent {
    /**
     * transactional event type
     *
     * @return TransactionEventType
     */
    TransactionEventType getTransactionType();
}
