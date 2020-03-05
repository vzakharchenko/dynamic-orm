package com.github.vzakharchenko.dynamic.orm.core.cache.event;

import com.querydsl.sql.RelationalPath;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction.TransactionEventType;

import java.io.Serializable;
import java.util.Map;

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


    protected CacheEvent(
            CacheEventType cacheEventType, RelationalPath<?> qTable,
            Class<? extends DMLModel> modelClass,
            Map<Serializable, DiffColumnModel> diffColumnModelMap) {
        super(cacheEventType, qTable, modelClass, diffColumnModelMap);
    }


    @Override
    public TransactionEventType getTransactionType() {
        return TransactionEventType.AFTER_COMMIT;
    }

}
