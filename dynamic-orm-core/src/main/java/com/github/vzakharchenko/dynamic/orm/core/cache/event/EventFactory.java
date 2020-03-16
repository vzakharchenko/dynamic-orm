package com.github.vzakharchenko.dynamic.orm.core.cache.event;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.querydsl.sql.RelationalPath;

import java.util.Map;

/**
 * Spring Event Factory.
 */
public abstract class EventFactory {

    /**
     * insert Event
     *
     * @param qTable             metadata model
     * @param modelClass         pojo model
     * @param diffColumnModelMap diff map
     * @param <MODEL>            static or dynamic table class
     * @return diff event
     */
    public static <MODEL extends DMLModel> DiffEvent insertDiffEvent(
            RelationalPath<?> qTable,
            Class<MODEL> modelClass,
            Map<CompositeKey, DiffColumnModel> diffColumnModelMap) {
        return new DiffEvent(qTable)
                .cacheEventType(CacheEventType.INSERT)
                .modelClass(modelClass)
                .diffColumnModelMap(diffColumnModelMap).create();
    }

    /**
     * update event
     *
     * @param qTable             metadata model
     * @param modelClass         pojo model
     * @param diffColumnModelMap diff map
     * @param <MODEL>            static or dynamic table class
     * @return diff event
     */
    public static <MODEL extends DMLModel> DiffEvent updateDiffEvent(
            RelationalPath<?> qTable,
            Class<MODEL> modelClass,
            Map<CompositeKey, DiffColumnModel> diffColumnModelMap) {
//        return new DiffEvent(CacheEventType.UPDATE, qTable, modelClass, diffColumnModelMap);
        return new DiffEvent(qTable)
                .cacheEventType(CacheEventType.UPDATE)
                .modelClass(modelClass)
                .diffColumnModelMap(diffColumnModelMap).create();
    }

    /**
     * delete event
     *
     * @param qTable             metadata model
     * @param modelClass         pojo model
     * @param diffColumnModelMap diff map
     * @param <MODEL>            static or dynamic table class
     * @return diff event
     */
    public static <MODEL extends DMLModel> DiffEvent deleteDiffEvent(
            RelationalPath<?> qTable,
            Class<MODEL> modelClass,
            Map<CompositeKey, DiffColumnModel> diffColumnModelMap) {
        return new DiffEvent(qTable)
                .cacheEventType(CacheEventType.DELETE)
                .modelClass(modelClass)
                .diffColumnModelMap(diffColumnModelMap).create();
    }

    /**
     * soft delete event
     *
     * @param qTable             metadata model
     * @param modelClass         pojo model
     * @param diffColumnModelMap diff map
     * @param <MODEL>            static or dynamic table class
     * @return diff event
     */
    public static <MODEL extends DMLModel> DiffEvent softDeleteDiffEvent(
            RelationalPath<?> qTable,
            Class<MODEL> modelClass,
            Map<CompositeKey, DiffColumnModel> diffColumnModelMap) {
        return new DiffEvent(qTable)
                .cacheEventType(CacheEventType.SOFT_DELETE)
                .modelClass(modelClass)
                .diffColumnModelMap(diffColumnModelMap).create();
    }

    /**
     * soft delete cache event
     *
     * @param qTable             metadata model
     * @param modelClass         pojo model
     * @param diffColumnModelMap diff map
     * @param <MODEL>            static or dynamic table class
     * @return cache event
     */
    public static <MODEL extends DMLModel> CacheEvent softDeleteCacheEvent(
            RelationalPath<?> qTable,
            Class<MODEL> modelClass,
            Map<CompositeKey, DiffColumnModel> diffColumnModelMap) {
        return new CacheEvent(qTable)
                .cacheEventType(CacheEventType.SOFT_DELETE)
                .modelClass(modelClass)
                .diffColumnModelMap(diffColumnModelMap).create();
    }

    /**
     * insert cache event
     *
     * @param qTable             metadata model
     * @param modelClass         pojo model
     * @param diffColumnModelMap diff map
     * @param <MODEL>            static or dynamic table class
     * @return cache event
     */
    public static <MODEL extends DMLModel> CacheEvent insertCacheEvent(
            RelationalPath<?> qTable,
            Class<MODEL> modelClass, Map<CompositeKey,
            DiffColumnModel> diffColumnModelMap) {
        return new CacheEvent(qTable)
                .cacheEventType(CacheEventType.INSERT)
                .modelClass(modelClass)
                .diffColumnModelMap(diffColumnModelMap).create();
    }

    /**
     * update cache event
     *
     * @param qTable             metadata model
     * @param modelClass         pojo model
     * @param diffColumnModelMap diff map
     * @param <MODEL>            static or dynamic table class
     * @return cache event
     */
    public static <MODEL extends DMLModel> CacheEvent updateCacheEvent(
            RelationalPath<?> qTable,
            Class<MODEL> modelClass,
            Map<CompositeKey, DiffColumnModel> diffColumnModelMap) {
        return new CacheEvent(qTable)
                .cacheEventType(CacheEventType.UPDATE)
                .modelClass(modelClass)
                .diffColumnModelMap(diffColumnModelMap).create();
    }

    /**
     * delete cache event
     *
     * @param qTable             metadata model
     * @param modelClass         pojo model
     * @param diffColumnModelMap diff map
     * @param <MODEL>            static or dynamic table class
     * @return cache event
     */
    public static <MODEL extends DMLModel> CacheEvent deleteCacheEvent(
            RelationalPath<?> qTable,
            Class<MODEL> modelClass,
            Map<CompositeKey, DiffColumnModel> diffColumnModelMap) {
        return new CacheEvent(qTable)
                .cacheEventType(CacheEventType.DELETE)
                .modelClass(modelClass)
                .diffColumnModelMap(diffColumnModelMap).create();
    }

}
