package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.Assert;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.*;
import com.github.vzakharchenko.dynamic.orm.core.helper.CacheHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDelete;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;

import java.io.Serializable;
import java.util.*;

/**
 *
 */
public class CacheBuilderImpl<MODEL extends DMLModel>
        implements CacheBuilder<MODEL>, RawCacheBuilder {

    private final Class<MODEL> modelClass;

    private final QueryContextImpl queryContext;

    private final RelationalPath<?> qTable;

    private final SoftDelete<?> softDelete;


    protected CacheBuilderImpl(RelationalPath<?> qTable, Class<MODEL> modelClass,
                               QueryContextImpl queryContext) {
        this.qTable = qTable;
        Assert.isTrue(ModelHelper.hasPrimaryKey(qTable),
                "Table " + qTable.getTableName() + " does not have primary key");
        this.modelClass = modelClass;
        this.queryContext = queryContext;
        this.softDelete = queryContext.getSoftDeleteColumn(qTable, modelClass);
    }

    @Override
    public MODEL findOneById(Serializable key) {
        PrimaryKeyCacheKey pkCacheKey = CacheHelper.buildPrimaryKeyCacheKey(key, qTable);
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

    private SQLCommonQuery<?> findByIdQuery(Serializable key) {
        return queryContext.getOrmQueryFactory().buildQuery().from(qTable)
                .where(ModelHelper.getPrimaryKey(qTable).eq(key));
    }

    private BooleanExpression buildWhereWithSoftDelete(BooleanExpression where) {
        return softDelete != null ? softDelete.getActiveExpression().and(where) : where;
    }

    @Override
    public List<MODEL> findAllByIds(List<? extends Serializable> keys) {

        List<MODEL> returnedModels = new ArrayList<>(keys.size());

        Map<Serializable, MapModel> listOfMapByIds = findAllOfMapByIds(keys);

        for (Serializable key : keys) {
            returnedModels.add(CacheHelper.buildModel(modelClass, listOfMapByIds.get(key)));
        }

        return returnedModels;
    }


    @Override
    public Map<Serializable, MapModel> findAllOfMapByIds(List<? extends Serializable> keys) {
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        Map<Serializable, Map<Path<?>, Object>> models = new LinkedHashMap<>();

        List<Serializable> skippedList = new ArrayList<>();

        for (Serializable key : keys) {
            PrimaryKeyCacheKey pkCacheKey = CacheHelper.buildPrimaryKeyCacheKey(key, qTable);
            Map<Path<?>, Object> modelMap = transactionCache
                    .getFromCache(pkCacheKey, HashMap.class);
            if (modelMap != null) {
                models.put(key, modelMap);
            } else {
                skippedList.add(key);
            }
        }

        List<MODEL> modelList = queryContext.getOrmQueryFactory().select()
                .findAll(findByIdsQuery(skippedList), qTable, modelClass);

        for (MODEL model : modelList) {
            Serializable primaryKeyValue = ModelHelper
                    .getPrimaryKeyValue(model, qTable, Serializable.class);
            Map<Path<?>, Object> modelMap = CacheHelper.buildMapFromModel(qTable, model);
            models.put(primaryKeyValue, modelMap);
            PrimaryKeyCacheKey pkCacheKey = CacheHelper
                    .buildPrimaryKeyCacheKey(primaryKeyValue, qTable);
            transactionCache.lock(pkCacheKey);
            try {
                if (softDelete == null || Objects.equals(
                        modelMap.get(softDelete.getColumn()), softDelete.getDeletedValue())) {
                    transactionCache.putToCache(pkCacheKey, (Serializable) modelMap);
                }
            } finally {
                transactionCache.unLock(pkCacheKey);
            }
        }

        Map<Serializable, MapModel> returnedModels = new LinkedHashMap<>(keys.size());

        for (Serializable key : keys) {
            Map<Path<?>, Object> diffModels = models.get(key);
            if (diffModels == null) {
                throw new IllegalStateException(key + " is not found");
            }
            returnedModels.put(key, MapModelFactory.buildMapModel(qTable, diffModels));
        }

        return returnedModels;
    }

    private SQLCommonQuery<?> findByIdsQuery(List<Serializable> keys) {
        return queryContext.getOrmQueryFactory().buildQuery().from(qTable)
                .where(buildWhereWithSoftDelete(ModelHelper.getPrimaryKey(qTable).in(keys)));
    }


    private <TYPE extends Serializable> LazyList<MODEL> findAllByColumn(Path<TYPE> column,
                                                                        TYPE columnValue,
                                                                        boolean isNotNull) {
        RelationalPath<?> qTable0 = ModelHelper.getQTable(column);
        Assert.isTrue(Objects.equals(qTable0, this.qTable),
                "Column should be from " + this.qTable);
        queryContext.getCacheContext().registerColumn(column);
        CachedColumn cachedColumn = isNotNull ?
                CacheHelper.buildCachedColumn(column) :
                CacheHelper.buildCachedColumnWithValue(column, columnValue);
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        transactionCache.lock(cachedColumn);
        try {
            List<Serializable> primaryKeys =
                    transactionCache.getFromCache(cachedColumn, List.class);

            if (primaryKeys == null) {
                ComparableExpressionBase columnExpression = (ComparableExpressionBase) column;
                BooleanExpression booleanExpression =
                        isNotNull
                                ? columnExpression.isNotNull()
                                : columnValue == null
                                ? columnExpression.isNull()
                                : columnExpression.eq(columnValue);
                primaryKeys = queryContext.getOrmQueryFactory().select()
                        .findAll(expressionQuery(booleanExpression),
                                ModelHelper.getPrimaryKeyColumn(qTable0));
                transactionCache.putToCache(cachedColumn, (Serializable) primaryKeys);
            }

            return ModelLazyListFactory.buildLazyList(qTable0, primaryKeys,
                    modelClass, queryContext);
        } finally {
            transactionCache.unLock(cachedColumn);
        }
    }

    private SQLCommonQuery<?> expressionQuery(BooleanExpression expression) {
        return queryContext.getOrmQueryFactory().buildQuery().from(qTable)
                .where(buildWhereWithSoftDelete(expression));
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
            List<Serializable> primaryKeys =
                    transactionCache.getFromCache(cachedAllData, List.class);

            if (primaryKeys == null) {
                primaryKeys = queryContext.getOrmQueryFactory().select()
                        .findAll(queryContext.getOrmQueryFactory()
                                .buildQuery().from(qTable), ModelHelper
                                .getPrimaryKeyColumn(qTable));
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
        } else if (lazyList.size() > 1) {
            throw new IllegalStateException("found " + lazyList.size() + " but expected 1");
        }

        return lazyList.getModelList().get(0);
    }

    @Override
    public <TYPE extends Serializable> MODEL findOneByColumnIsNotNull(Path<TYPE> column) {
        LazyList<MODEL> lazyList = findAllByColumn(column, null, true);
        if (lazyList.size() == 0) {
            return null;
        } else if (lazyList.size() > 1) {
            throw new IllegalStateException("found " + lazyList.size() + " but expected 1");
        }

        return lazyList.getModelList().get(0);
    }


    @Override
    public boolean isPresentInCache(Serializable key) {
        PrimaryKeyCacheKey pkCacheKey = CacheHelper.buildPrimaryKeyCacheKey(key, qTable);
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        return transactionCache.isInCache(pkCacheKey);
    }
}
