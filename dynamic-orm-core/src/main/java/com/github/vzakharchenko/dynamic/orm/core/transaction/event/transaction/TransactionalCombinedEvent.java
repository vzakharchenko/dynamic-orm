package com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction;

import java.io.Serializable;
import java.util.List;

/**
 * Merging Transaction events
 * Are Sending a lot of events in the transaction, but obtains only one event
 */
public interface TransactionalCombinedEvent<EVENT> extends TransactionEvent {

    /**
     * unique transaction resource name
     */
    Serializable resourceName();

    /**
     * Merging event
     *
     * @param event
     */
    void combine(EVENT event);


    /**
     * transaction history
     *
     * @return
     */
    List<EVENT> getTransactionHistory();
}
