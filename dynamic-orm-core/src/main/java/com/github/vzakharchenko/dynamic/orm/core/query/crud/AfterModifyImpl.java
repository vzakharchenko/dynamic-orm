package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.Assert;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumn;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.PrimaryKeyCacheKey;
import com.github.vzakharchenko.dynamic.orm.core.cache.event.EventFactory;
import com.github.vzakharchenko.dynamic.orm.core.helper.CacheHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheContext;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;

import java.io.Serializable;
import java.util.*;

/**
 *
 */
public class AfterModifyImpl<MODEL extends DMLModel> implements AfterModify<MODEL> {

    private final RelationalPath<?> qTable;
    private final QueryContextImpl queryContext;
    private final Class<MODEL> modelClass;

    private final SoftDelete<?> softDelete;

    public AfterModifyImpl(RelationalPath<?> qTable, Class<MODEL> modelClass,
                           QueryContextImpl queryContext) {
        this.qTable = qTable;
        this.queryContext = queryContext;
        this.modelClass = modelClass;
        this.softDelete = queryContext.getSoftDeleteColumn(qTable, modelClass);
    }

    @Override
    public void afterInsert(Map<Serializable, DiffColumnModel> diffColumnModelMap) {
        afterModification(diffColumnModelMap, false, true);

        queryContext.getTransactionalEventPublisher()
                .publishEvent(EventFactory.insertCacheEvent(qTable,
                        modelClass, diffColumnModelMap));
        queryContext.getTransactionalEventPublisher()
                .publishEvent(EventFactory.insertDiffEvent(qTable,
                        modelClass, diffColumnModelMap));
    }

    @Override
    public void afterDelete(Map<Serializable, DiffColumnModel> diffColumnModelMap) {
        afterModification(diffColumnModelMap, true, false);

        queryContext.getTransactionalEventPublisher()
                .publishEvent(EventFactory.deleteCacheEvent(qTable, modelClass,
                        diffColumnModelMap));
        queryContext.getTransactionalEventPublisher()
                .publishEvent(EventFactory.deleteDiffEvent(qTable, modelClass,
                        diffColumnModelMap));
    }

    private void afterModification(Map<Serializable, DiffColumnModel> diffColumnModelMap,
                                   boolean clearOldModel, boolean clearNewModel) {

        TransactionalCache transactionCache = queryContext.getTransactionCache();
        CacheContext cacheContext = queryContext.getCacheContext();

        for (Map.Entry<Serializable, DiffColumnModel> entry : diffColumnModelMap.entrySet()) {
            Serializable primaryKeyValue = entry.getKey();
            DiffColumnModel diffColumnModel = entry.getValue();
            Assert.notNull(primaryKeyValue);
            //clear Primary key
            PrimaryKeyCacheKey primaryKeyCacheKey = CacheHelper
                    .buildPrimaryKeyCacheKey(primaryKeyValue, this.qTable);
            transactionCache.cacheEvict(primaryKeyCacheKey);
            // clear cached column
            List<Path<Serializable>> cachedColumns = cacheContext
                    .getCachedColumns(this.qTable.getTableName());
            for (Path<Serializable> cachedColumn : cachedColumns) {
                DiffColumn<Serializable> columnDiff = diffColumnModel
                        .getColumnDiff(cachedColumn);
                transactionCache.cacheEvict(CacheHelper.buildCachedColumn(cachedColumn));
                if (columnDiff.isChanged()) {
                    //clear oldModel
                    if (clearOldModel) {
                        transactionCache.cacheEvict(CacheHelper
                                .buildCachedColumnWithValue(cachedColumn,
                                        columnDiff.getOldValue()));
                    }
                    // clear new Model;
                    if (clearNewModel) {
                        transactionCache.cacheEvict(CacheHelper
                                .buildCachedColumnWithValue(cachedColumn,
                                        columnDiff.getNewValue()));
                    }
                } else {
                    transactionCache.cacheEvict(CacheHelper
                            .buildCachedColumnWithValue(cachedColumn,
                                    columnDiff.getNewValue()));
                }
            }
        }
        cleanQueryCache();

        sendSoftDeleteEventIfNeeded(diffColumnModelMap);

    }

    private void sendSoftDeleteEventIfNeeded(
            Map<Serializable, DiffColumnModel> diffColumnModelMap) {
        if (softDelete != null) {
            Map<Serializable, DiffColumnModel> softDeletedModelMap =
                    softDeletedMap(qTable, diffColumnModelMap);
            if (MapUtils.isNotEmpty(softDeletedModelMap)) {
                queryContext.getTransactionalEventPublisher()
                        .publishEvent(EventFactory.softDeleteCacheEvent(qTable,
                                modelClass, diffColumnModelMap));
                queryContext.getTransactionalEventPublisher()
                        .publishEvent(EventFactory.softDeleteDiffEvent(qTable,
                                modelClass, diffColumnModelMap));
            }
        }
    }

    @Override
    public void afterUpdate(Map<Serializable, DiffColumnModel> diffColumnModelMap) {

        afterModification(diffColumnModelMap, true, true);
        queryContext.getTransactionalEventPublisher()
                .publishEvent(EventFactory.updateCacheEvent(qTable,
                        modelClass, diffColumnModelMap));
        queryContext.getTransactionalEventPublisher()
                .publishEvent(EventFactory.updateDiffEvent(qTable,
                        modelClass, diffColumnModelMap));
    }

    @Override
    public void cleanQueryCache() {
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        CacheContext cacheContext = queryContext.getCacheContext();
        List<String> cachedQueries = cacheContext.getCachedQueries(qTable.getTableName());
        cachedQueries.forEach(transactionCache::cacheEvict);
    }

    private Map<Serializable, DiffColumnModel> softDeletedMap(
            RelationalPath<?> qTable0, Map<Serializable, DiffColumnModel> diffColumnModelMap) {
        Map<Serializable, DiffColumnModel> softDeletedModelMap = new HashMap<>();
        Path primaryKeyColumn = ModelHelper.getPrimaryKeyColumn(qTable0);
        for (Map.Entry<Serializable, DiffColumnModel> entry : diffColumnModelMap.entrySet()) {
            if (softDelete != null) {
                DiffColumnModel diffColumnModel = entry.getValue();
                DiffColumn primaryKeyDiff = diffColumnModel.getColumnDiff(primaryKeyColumn);
                if (primaryKeyDiff.getNewValue() != null) {
                    DiffColumn<? extends Serializable> columnDiff = diffColumnModel
                            .getColumnDiff(softDelete.getColumn());
                    if (Objects.equals(columnDiff.getNewValue(), softDelete.getDeletedValue())) {
                        softDeletedModelMap.put(entry.getKey(), diffColumnModel);
                    }
                }
            }
        }
        return Collections.unmodifiableMap(softDeletedModelMap);
    }
}
