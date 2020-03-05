package com.github.vzakharchenko.dynamic.orm.core.cache.event;

import com.querydsl.sql.RelationalPath;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;

import java.io.Serializable;
import java.util.Map;

/**
 *
 */
public abstract class EventFactory {

    public static <MODEL extends DMLModel> DiffEvent insertDiffEvent(
            RelationalPath<?> qTable, Class<MODEL> modelClass, Map<Serializable,
            DiffColumnModel> diffColumnModelMap) {
        return new DiffEvent(CacheEventType.INSERT, qTable, modelClass, diffColumnModelMap);
    }

    public static <MODEL extends DMLModel> DiffEvent updateDiffEvent(
            RelationalPath<?> qTable, Class<MODEL> modelClass,
            Map<Serializable, DiffColumnModel> diffColumnModelMap) {
        return new DiffEvent(CacheEventType.UPDATE, qTable, modelClass, diffColumnModelMap);
    }

    public static <MODEL extends DMLModel> DiffEvent deleteDiffEvent(
            RelationalPath<?> qTable, Class<MODEL> modelClass,
            Map<Serializable, DiffColumnModel> diffColumnModelMap) {
        return new DiffEvent(CacheEventType.DELETE, qTable, modelClass, diffColumnModelMap);
    }

    public static <MODEL extends DMLModel> DiffEvent softDeleteDiffEvent(
            RelationalPath<?> qTable, Class<MODEL> modelClass, Map<Serializable,
            DiffColumnModel> diffColumnModelMap) {
        return new DiffEvent(CacheEventType.SOFT_DELETE, qTable, modelClass, diffColumnModelMap);
    }

    public static <MODEL extends DMLModel> CacheEvent softDeleteCacheEvent(
            RelationalPath<?> qTable, Class<MODEL> modelClass,
            Map<Serializable, DiffColumnModel> diffColumnModelMap) {
        return new CacheEvent(CacheEventType.SOFT_DELETE, qTable, modelClass, diffColumnModelMap);
    }

    public static <MODEL extends DMLModel> CacheEvent insertCacheEvent(
            RelationalPath<?> qTable, Class<MODEL> modelClass, Map<Serializable,
            DiffColumnModel> diffColumnModelMap) {
        return new CacheEvent(CacheEventType.INSERT, qTable, modelClass, diffColumnModelMap);
    }

    public static <MODEL extends DMLModel> CacheEvent updateCacheEvent(
            RelationalPath<?> qTable, Class<MODEL> modelClass,
            Map<Serializable, DiffColumnModel> diffColumnModelMap) {
        return new CacheEvent(CacheEventType.UPDATE, qTable, modelClass, diffColumnModelMap);
    }

    public static <MODEL extends DMLModel> CacheEvent deleteCacheEvent(
            RelationalPath<?> qTable, Class<MODEL> modelClass, Map<Serializable,
            DiffColumnModel> diffColumnModelMap) {
        return new CacheEvent(CacheEventType.DELETE, qTable, modelClass, diffColumnModelMap);
    }

}
