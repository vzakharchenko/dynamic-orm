package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.CachedAllData;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumn;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.event.EventFactory;
import com.github.vzakharchenko.dynamic.orm.core.helper.CacheHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.StatisticCacheKey;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        afterModification(diffColumnModelMap, false);
        diffColumnModelMap.keySet().forEach(
                compositeKey -> queryContext.getTransactionCache()
                        .insertModel(compositeKey));
        queryContext.getTransactionalEventPublisher()
                .publishEvent(EventFactory.insertCacheEvent(qTable,
                        modelClass, diffColumnModelMap));
        queryContext.getTransactionalEventPublisher()
                .publishEvent(EventFactory.insertDiffEvent(qTable,
                        modelClass, diffColumnModelMap));
    }

    @Override
    public void afterDelete(Map<CompositeKey, DiffColumnModel> diffColumnModelMap) {
        afterModification(diffColumnModelMap, true);
        diffColumnModelMap.keySet().forEach(
                compositeKey -> queryContext.getTransactionCache()
                        .deleteModel(compositeKey));
        queryContext.getTransactionalEventPublisher()
                .publishEvent(EventFactory.deleteCacheEvent(qTable, modelClass,
                        diffColumnModelMap));
        queryContext.getTransactionalEventPublisher()
                .publishEvent(EventFactory.deleteDiffEvent(qTable, modelClass,
                        diffColumnModelMap));
    }


    private void clearPrimaryKey(CompositeKey compositeKey) {
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        transactionCache.cacheEvict(compositeKey);
    }

    private void clearAllData() {
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        CachedAllData cachedAllData = CacheHelper
                .buildAllDataCache(this.qTable);
        transactionCache.cacheEvict(cachedAllData);
    }


    private void afterModification(Map<CompositeKey, DiffColumnModel> diffColumnModelMap,
                                   boolean clearOldModel) {
        diffColumnModelMap.forEach((primaryKeyValue, diffColumnModel) -> {
            Assert.notNull(primaryKeyValue);
            //clear Primary key
            if (clearOldModel) {
                clearPrimaryKey(primaryKeyValue);
            }
            //clear Fetch All
            clearAllData();
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

        afterModification(diffColumnModelMap, true);
        diffColumnModelMap.keySet().forEach(
                compositeKey -> queryContext.getTransactionCache()
                        .updateModel(compositeKey));
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
        transactionCache.cacheEvict(
                new StatisticCacheKey(StringUtils.upperCase(qTable.getTableName())));
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
