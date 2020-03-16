package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.RawModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.*;
import com.github.vzakharchenko.dynamic.orm.core.helper.CacheHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyExpressionHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.util.*;

/**
 *
 */
public class CacheBuilderImpl<MODEL extends DMLModel>
        extends AbstractCacheBuilder<MODEL> {


    protected CacheBuilderImpl(RelationalPath<?> qTable,
                               Class<MODEL> modelClass,
                               QueryContextImpl queryContext) {
        super(qTable, modelClass, queryContext);
    }

    @Override
    public MODEL findOneById(Serializable key) {
        PrimaryKeyCacheKey pkCacheKey = CacheHelper.buildPrimaryKeyCacheKey(
                PrimaryKeyHelper.getCompositeKey(key, qTable));
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        Map<Path<?>, Object> modelMap = transactionCache.getFromCache(pkCacheKey, HashMap.class);
        if (modelMap == null) {
            MODEL model = queryContext.getOrmQueryFactory().select().findOne(findByIdQuery(key),
                    qTable, modelClass);
            modelMap = CacheHelper.buildMapFromModel(qTable, model);
            if (MapUtils.isNotEmpty(modelMap) && (softDelete == null || Objects.equals(
                    modelMap.get(softDelete.getColumn()), softDelete.getDeletedValue()))) {
                transactionCache.putToCache(pkCacheKey, (Serializable) modelMap);
            }
        }
        return CacheHelper.buildModel(qTable, modelClass, modelMap);
    }

    @Override
    public List<MODEL> findAllByIds(List<? extends Serializable> keys) {

        List<MODEL> returnedModels = new ArrayList<>(keys.size());

        Map<CompositeKey, MapModel> listOfMapByIds = findAllOfMapByIds(
                PrimaryKeyHelper.getCompositeKeys(keys, qTable));

        for (Serializable key : keys) {
            returnedModels.add(CacheHelper.buildModel(modelClass, listOfMapByIds.get(key)));
        }

        return returnedModels;
    }

    @Override
    public Map<CompositeKey, MapModel> findAllOfMapByIds(List<CompositeKey> keys) {

        Map<CompositeKey, Map<Path<?>, Object>> models = new LinkedHashMap<>();

        List<CompositeKey> skippedList = skippedList(
                PrimaryKeyHelper.getCompositeKeys(keys, qTable), models);

        List<MODEL> modelList = findByIdsQuery(skippedList);

        for (MODEL model : modelList) {
            transactionModel(model, models);
        }
        Map<CompositeKey, MapModel> returnedModels = new LinkedHashMap<>(keys.size());
        for (CompositeKey key : keys) {
            Map<Path<?>, Object> diffModels = models.get(key);
            if (diffModels == null) {
                throw new IllegalStateException(key + " is not found");
            }
            returnedModels.put(key, MapModelFactory.buildMapModel(qTable, diffModels));
        }
        return returnedModels;
    }


    @Override
    public <TYPE extends Serializable> LazyList<MODEL> findAllByColumn(Path<TYPE> column,
                                                                       TYPE columnValue) {
        return findAllByColumn(column, columnValue, false);
    }

    @Override
    public <TYPE extends Serializable> LazyList<MODEL> findAllByColumnIsNotNull(
            Path<TYPE> column) {
        return findAllByColumn(column, null, true);
    }

    @Override
    public LazyList<MODEL> findAll() {
        CachedAllData cachedAllData = CacheHelper.buildAllDataCache(qTable);
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        transactionCache.lock(cachedAllData);
        try {
            List<CompositeKey> primaryKeys =
                    transactionCache.getFromCache(cachedAllData, List.class);

            if (primaryKeys == null) {
                List<RawModel> rawModels = queryContext.getOrmQueryFactory().select()
                        .rawSelect(queryContext.getOrmQueryFactory()
                                .buildQuery().from(qTable)).findAll(PrimaryKeyExpressionHelper
                                .getPrimaryKeyExpressionColumns(qTable));
                primaryKeys = PrimaryKeyHelper.getPrimaryKeyValues(rawModels, qTable);
                transactionCache.putToCache(cachedAllData, (Serializable) primaryKeys);
            }

            return ModelLazyListFactory
                    .buildLazyList(qTable, primaryKeys, modelClass, queryContext);
        } finally {
            transactionCache.unLock(cachedAllData);
        }
    }

    @Override
    public <TYPE extends Serializable> MODEL findOneByColumn(
            Path<TYPE> column, TYPE columnValue) {
        LazyList<MODEL> lazyList = findAllByColumn(column, columnValue, false);
        if (lazyList.size() == 0) {
            return null;
        } else if (lazyList.size() > SIZE) {
            throw new IllegalStateException("found " + lazyList.size() + " but expected 1");
        }

        return lazyList.getModelList().get(0);
    }

    @Override
    public <TYPE extends Serializable> MODEL findOneByColumnIsNotNull(Path<TYPE> column) {
        LazyList<MODEL> lazyList = findAllByColumn(column, null, true);
        if (lazyList.size() == 0) {
            return null;
        } else if (lazyList.size() > SIZE) {
            throw new IllegalStateException("found " + lazyList.size() + " but expected 1");
        }

        return lazyList.getModelList().get(0);
    }


    @Override
    public boolean isPresentInCache(Serializable key) {
        PrimaryKeyCacheKey pkCacheKey = CacheHelper.buildPrimaryKeyCacheKey(
                PrimaryKeyHelper.getCompositeKey(key, qTable));
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        return transactionCache.isInCache(pkCacheKey);
    }
}
