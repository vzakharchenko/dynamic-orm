package com.github.vzakharchenko.dynamic.orm.core.cache.event;

import com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction.TransactionEventType;
import com.querydsl.sql.RelationalPath;

/**
 * Audit event
 * <p>
 * Attention!  This event is only for tables with primary keys
 * <p>
 * This event is dispatched before a commit Transaction
 * <p>
 * This event contains the history of changes during the transaction
 * <p>
 * Transaction ,in which  changes were, is  still active
 */
public final class DiffEvent extends ModifyEvent<DiffEvent> {


    protected DiffEvent(RelationalPath<?> qTable) {
        super(qTable);
    }

    @Override
    public TransactionEventType getTransactionType() {
        return TransactionEventType.BEFORE_COMMIT;
    }

}
