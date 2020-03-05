package com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction;

/**
 * Event Synchronisation type
 */
public enum TransactionEventType {
    BEFORE_COMMIT, //send an event before commit Transaction(In transaction)
    AFTER_COMMIT, //send an event after commit Transaction
    WITHOUT_SYNCHRONIZATION //To send an event here and now
}
