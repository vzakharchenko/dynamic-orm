package com.github.vzakharchenko.dynamic.orm.core.cache.event;

import com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction.TransactionEventType;
import com.querydsl.sql.RelationalPath;

/**
 * Audit event
 * <p>
 * Attention! This event is only for tables with primary keys
 * <p>
 * This event is dispatched after a commit Transaction
 * <p>
 * This event contains the history of changes during the transaction
 */
public final class CacheEvent extends ModifyEvent<CacheEvent> {


    protected CacheEvent(RelationalPath<?> qTable) {
        super(qTable);
    }

    @Override
    public TransactionEventType getTransactionType() {
        return TransactionEventType.AFTER_COMMIT;
    }

}
