package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.CachedAllData;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumn;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.PrimaryKeyCacheKey;
import com.github.vzakharchenko.dynamic.orm.core.cache.event.EventFactory;
import com.github.vzakharchenko.dynamic.orm.core.helper.CacheHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheContext;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

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
    public void afterInsert(Map<CompositeKey, DiffColumnModel> diffColumnModelMap) {
        afterModification(diffColumnModelMap, false, true);

        queryContext.getTransactionalEventPublisher()
                .publishEvent(EventFactory.insertCacheEvent(qTable,
                        modelClass, diffColumnModelMap));
        queryContext.getTransactionalEventPublisher()
                .publishEvent(EventFactory.insertDiffEvent(qTable,
                        modelClass, diffColumnModelMap));
    }

    @Override
    public void afterDelete(Map<CompositeKey, DiffColumnModel> diffColumnModelMap) {
        afterModification(diffColumnModelMap, true, false);

        queryContext.getTransactionalEventPublisher()
                .publishEvent(EventFactory.deleteCacheEvent(qTable, modelClass,
                        diffColumnModelMap));
        queryContext.getTransactionalEventPublisher()
                .publishEvent(EventFactory.deleteDiffEvent(qTable, modelClass,
                        diffColumnModelMap));
    }


    private void clearPrimaryKey(CompositeKey compositeKey) {
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        PrimaryKeyCacheKey primaryKeyCacheKey = CacheHelper
                .buildPrimaryKeyCacheKey(compositeKey);
        transactionCache.cacheEvict(primaryKeyCacheKey);
    }

    private void clearAllData() {
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        CachedAllData cachedAllData = CacheHelper
                .buildAllDataCache(this.qTable);
        transactionCache.cacheEvict(cachedAllData);
    }


    private void clearChangedColumn(Path<Serializable> cachedColumn,
                                    DiffColumn<Serializable> columnDiff,
                                    boolean clearOldModel, boolean clearNewModel) {
        TransactionalCache transactionCache = queryContext.getTransactionCache();
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
    }

    private void clearColumns(DiffColumnModel diffColumnModel,
                              boolean clearOldModel, boolean clearNewModel) {
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        CacheContext cacheContext = queryContext.getCacheContext();
        List<Path<Serializable>> cachedColumns = cacheContext
                .getCachedColumns(this.qTable.getTableName());
        cachedColumns.forEach(new Consumer<Path<Serializable>>() {
            @Override
            public void accept(Path<Serializable> cachedColumn) {
                DiffColumn<Serializable> columnDiff = diffColumnModel
                        .getColumnDiff(cachedColumn);
                transactionCache.cacheEvict(CacheHelper.buildCachedColumn(cachedColumn));
                if (columnDiff.isChanged()) {
                    clearChangedColumn(cachedColumn, columnDiff, clearOldModel, clearNewModel);
                } else {
                    transactionCache.cacheEvict(CacheHelper
                            .buildCachedColumnWithValue(cachedColumn,
                                    columnDiff.getNewValue()));
                }
            }
        });
    }

    private void afterModification(Map<CompositeKey, DiffColumnModel> diffColumnModelMap,
                                   boolean clearOldModel, boolean clearNewModel) {
        diffColumnModelMap.forEach((primaryKeyValue, diffColumnModel) -> {
            Assert.notNull(primaryKeyValue);
            //clear Primary key
            clearPrimaryKey(PrimaryKeyHelper.getCompositeKey(primaryKeyValue, qTable));
            //clear Fetch All
            clearAllData();
            // clear cached column
            clearColumns(diffColumnModel, clearOldModel, clearNewModel);
        });
        cleanQueryCache();

        sendSoftDeleteEventIfNeeded(diffColumnModelMap);

    }

    private void sendSoftDeleteEventIfNeeded(
            Map<CompositeKey, DiffColumnModel> diffColumnModelMap) {
        if (softDelete != null) {
            Map<CompositeKey, DiffColumnModel> softDeletedModelMap =
                    softDeletedMap(diffColumnModelMap);
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
    public void afterUpdate(Map<CompositeKey, DiffColumnModel> diffColumnModelMap) {

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

    private Map<CompositeKey, DiffColumnModel> softDeletedMap(
            Map<CompositeKey, DiffColumnModel> diffColumnModelMap) {
        Map<CompositeKey, DiffColumnModel> softDeletedModelMap = new HashMap<>();
        for (Map.Entry<CompositeKey, DiffColumnModel> entry : diffColumnModelMap.entrySet()) {
            if (softDelete != null) {
                DiffColumnModel diffColumnModel = entry.getValue();
                Map<Path<?>, DiffColumn<?>> columnDiffPrimaryKey = diffColumnModel
                        .getColumnDiffPrimaryKey();
                columnDiffPrimaryKey.forEach((path, diffColumn) -> {
                    if (diffColumn.getNewValue() != null) {
                        DiffColumn<? extends Serializable> columnDiff = diffColumnModel
                                .getColumnDiff(softDelete.getColumn());
                        if (Objects.equals(columnDiff.getNewValue(), softDelete
                                .getDeletedValue())) {
                            softDeletedModelMap.put(entry.getKey(), diffColumnModel);
                        }
                    }
                });
            }
        }
        return Collections.unmodifiableMap(softDeletedModelMap);
    }
}
