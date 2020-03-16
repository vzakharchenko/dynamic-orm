package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.RawModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.*;
import com.github.vzakharchenko.dynamic.orm.core.helper.CacheHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDelete;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
public class CacheBuilderImpl<MODEL extends DMLModel>
        implements CacheBuilder<MODEL>, RawCacheBuilder {

    public static final int SIZE = 1;
    private final Class<MODEL> modelClass;

    private final QueryContextImpl queryContext;

    private final RelationalPath<?> qTable;

    private final SoftDelete<?> softDelete;


    protected CacheBuilderImpl(RelationalPath<?> qTable, Class<MODEL> modelClass,
                               QueryContextImpl queryContext) {
        this.qTable = qTable;
        Assert.isTrue(PrimaryKeyHelper.hasPrimaryKey(qTable),
                "Table " + qTable.getTableName() + " does not have primary key");
        this.modelClass = modelClass;
        this.queryContext = queryContext;
        this.softDelete = queryContext.getSoftDeleteColumn(qTable, modelClass);
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

    private SQLCommonQuery<?> findByIdQuery0(CompositeKey key) {
        return queryContext.getOrmQueryFactory().buildQuery().from(qTable)
                .where(key.getWherePart());
    }

    private SQLCommonQuery<?> findByIdQuery(Serializable key) {
        CompositeKey compositeKey = (key instanceof CompositeKey) ? (CompositeKey) key :
                PrimaryKeyHelper.getOnePrimaryKey(qTable, key);
        return findByIdQuery0(compositeKey);
    }

    private BooleanExpression buildWhereWithSoftDelete(BooleanExpression where) {
        return softDelete != null ? softDelete.getActiveExpression().and(where) : where;
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


    private List<CompositeKey> skippedList(List<CompositeKey> keys,
                                           Map<CompositeKey, Map<Path<?>, Object>> models) {

        List<CompositeKey> skippedList = new ArrayList<>();
        for (CompositeKey key : keys) {
            PrimaryKeyCacheKey pkCacheKey = CacheHelper.buildPrimaryKeyCacheKey(
                    PrimaryKeyHelper.getCompositeKey(key, qTable));
            Map<Path<?>, Object> modelMap = queryContext.getTransactionCache()
                    .getFromCache(pkCacheKey, Map.class);
            if (modelMap != null) {
                models.put(key, modelMap);
            } else {
                skippedList.add(key);
            }
        }
        return skippedList;
    }

    private void transactionModel(MODEL model, Map<CompositeKey, Map<Path<?>, Object>> models) {
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        CompositeKey primaryKeyValue = PrimaryKeyHelper
                .getPrimaryKeyValues(model, qTable);
        Map<Path<?>, Object> modelMap = CacheHelper.buildMapFromModel(qTable, model);
        models.put(primaryKeyValue, modelMap);
        PrimaryKeyCacheKey pkCacheKey = CacheHelper
                .buildPrimaryKeyCacheKey(primaryKeyValue);
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

    private List<MODEL> findByIdsQuery(List<CompositeKey> keys) {
        if (PrimaryKeyHelper.hasCompositePrimaryKey(qTable)) {
            return findByIdsQueryCompositePk(keys);
        } else {
            return findByIdsQueryPk(keys);
        }
    }

    private List<MODEL> findByIdsQueryCompositePk(List<CompositeKey> keys) {
        List<MODEL> models = new ArrayList<>();
        keys.forEach(key -> models.add(findByIdsQueryComposite(key)));
        return models;
    }

    private List<MODEL> findByIdsQueryPk(List<CompositeKey> keys) {
        Path<?> column = PrimaryKeyHelper.getPrimaryKeyColumns(qTable)
                .get(0);
        ComparableExpressionBase columnExpression = (ComparableExpressionBase) column;
        return queryContext.getOrmQueryFactory().select()
                .findAll(queryContext.getOrmQueryFactory().buildQuery().from(qTable)
                        .where(columnExpression.in(keys.stream()
                                .map((Function<CompositeKey, Object>)
                                        compositeKey -> compositeKey.getColumn(column))
                                .collect(Collectors.toList()))), qTable, modelClass);
    }

    //
    private MODEL findByIdsQueryComposite(CompositeKey column) {
        return queryContext.getOrmQueryFactory().select()
                .findOne(queryContext.getOrmQueryFactory().buildQuery().from(qTable)
                        .where(column.getWherePart()), qTable, modelClass);
    }


    // CHECKSTYLE:OFF
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
            List<CompositeKey> primaryKeys =
                    transactionCache.getFromCache(cachedColumn, List.class);

            if (primaryKeys == null) {
                ComparableExpressionBase columnExpression = (ComparableExpressionBase) column;
                BooleanExpression booleanExpression =
                        isNotNull ? columnExpression.isNotNull() : columnValue == null
                                ? columnExpression.isNull()
                                : columnExpression.eq(columnValue);
                List<RawModel> rawModels = queryContext.getOrmQueryFactory().select()
                        .rawSelect(expressionQuery(booleanExpression)).findAll(
                                PrimaryKeyHelper.getPrimaryKeyExpressionColumns(qTable0));
                primaryKeys = PrimaryKeyHelper.getPrimaryKeyValues(rawModels, qTable0);
                transactionCache.putToCache(cachedColumn, (Serializable) primaryKeys);
            }

            return ModelLazyListFactory.buildLazyList(qTable0, primaryKeys,
                    modelClass, queryContext);
        } finally {
            transactionCache.unLock(cachedColumn);
        }
    }

    // CHECKSTYLE:ON
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
            List<CompositeKey> primaryKeys =
                    transactionCache.getFromCache(cachedAllData, List.class);

            if (primaryKeys == null) {
                List<RawModel> rawModels = queryContext.getOrmQueryFactory().select()
                        .rawSelect(queryContext.getOrmQueryFactory()
                                .buildQuery().from(qTable)).findAll(PrimaryKeyHelper
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
