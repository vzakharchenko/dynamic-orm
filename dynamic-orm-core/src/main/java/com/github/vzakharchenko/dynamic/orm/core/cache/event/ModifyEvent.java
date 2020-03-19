package com.github.vzakharchenko.dynamic.orm.core.cache.event;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.querydsl.sql.RelationalPath;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modify Event.
 */
public abstract class ModifyEvent<EVENT extends AbstractModifyEvent<EVENT>>
        extends AbstractModifyEvent<EVENT> {

    protected ModifyEvent(RelationalPath<?> qTable) {
        super(qTable);
    }

    @Override
    public ModifyEventBuilder<EVENT> modelClass(Class<? extends DMLModel> modelClass) {
        this.modelClass0 = modelClass;
        return this;
    }

    @Override
    public ModifyEventBuilder<EVENT> diffColumnModelMap(
            Map<CompositeKey, DiffColumnModel> diffColumnModelMap) {
        this.diffColumnModelMap0 = diffColumnModelMap;
        return this;
    }

    @Override
    public ModifyEventBuilder<EVENT> cacheEventType(
            CacheEventType cacheEventType) {
        this.cacheEventType0 = cacheEventType;
        return this;
    }

    @Override
    public EVENT create() {
        this.transactionHistory.add(clone());
        return (EVENT) this;
    }

    @Override
    public final RelationalPath<?> getSource() {
        return (RelationalPath) super.getSource();
    }


    @Override
    public void combine(EVENT modelModifyEvent) {
        Map<CompositeKey, DiffColumnModel> newDiffColumnModelMap
                = new HashMap<>(diffColumnModelMap0);
        Map<CompositeKey, DiffColumnModel> diffColumnMap = modelModifyEvent
                .getDiffColumnModelMap();
        for (Map.Entry<CompositeKey, DiffColumnModel> entry : diffColumnMap.entrySet()) {
            combine(newDiffColumnModelMap, entry);
        }
        this.diffColumnModelMap0 = newDiffColumnModelMap;
        this.transactionHistory.add(modelModifyEvent);
        this.cacheEventType0 = CacheEventType.BATCH;
        oldModel = null;
        newModel = null;
    }

    @Override
    public final List<EVENT> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    @Override
    public EVENT clone() {
        try {
            return (EVENT) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }
}
