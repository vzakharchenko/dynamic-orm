package com.github.vzakharchenko.dynamic.orm.core.cache.event;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;

import java.io.Serializable;
import java.util.Map;

/**
 * Event Builder.
 *
 * @param <EVENT>
 */
public interface ModifyEventBuilder<EVENT extends ModifyEvent<EVENT>> {
    /**
     * pojo model.
     *
     * @param modelClass pojo model.
     * @return Event Builder.
     */
    ModifyEventBuilder<EVENT> modelClass(Class<? extends DMLModel> modelClass);

    /**
     * diff between old and new model.
     *
     * @param diffColumnModelMap diff between old and new model.
     * @return Event Builder.
     */
    ModifyEventBuilder<EVENT> diffColumnModelMap(
            Map<Serializable, DiffColumnModel> diffColumnModelMap);

    /**
     * EVENT type
     *
     * @param cacheEventType event type: insert, update, delete, etc.
     * @return Event Builder.
     */
    ModifyEventBuilder<EVENT> cacheEventType(
            CacheEventType cacheEventType);

    /**
     * create event
     *
     * @return event
     */
    EVENT create();
}
