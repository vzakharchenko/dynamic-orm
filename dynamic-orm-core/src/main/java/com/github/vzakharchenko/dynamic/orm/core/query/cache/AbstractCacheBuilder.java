package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.RawModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.CachedColumn;
import com.github.vzakharchenko.dynamic.orm.core.cache.LazyList;
import com.github.vzakharchenko.dynamic.orm.core.cache.ModelLazyListFactory;
import com.github.vzakharchenko.dynamic.orm.core.cache.PrimaryKeyCacheKey;
import com.github.vzakharchenko.dynamic.orm.core.helper.*;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDelete;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
public abstract class AbstractCacheBuilder<MODEL extends DMLModel>
        implements CacheBuilder<MODEL>, RawCacheBuilder {

    public static final int SIZE = 1;
    protected final Class<MODEL> modelClass;

    protected final QueryContextImpl queryContext;

    protected final RelationalPath<?> qTable;

    public final SoftDelete<?> softDelete;


    protected AbstractCacheBuilder(RelationalPath<?> qTable, Class<MODEL> modelClass,
                                   QueryContextImpl queryContext) {
        this.qTable = qTable;
        Assert.isTrue(PrimaryKeyHelper.hasPrimaryKey(qTable),
                "Table " + qTable.getTableName() + " does not have primary key");
        this.modelClass = modelClass;
        this.queryContext = queryContext;
        this.softDelete = queryContext.getSoftDeleteColumn(qTable, modelClass);
    }

    private SQLCommonQuery<?> findByIdQuery0(CompositeKey key) {
        return queryContext.getOrmQueryFactory().buildQuery().from(qTable)
                .where(key.getWherePart());
    }

    protected SQLCommonQuery<?> findByIdQuery(Serializable key) {
        CompositeKey compositeKey = (key instanceof CompositeKey) ? (CompositeKey) key :
                PrimaryKeyHelper.getOnePrimaryKey(qTable, key);
        return findByIdQuery0(compositeKey);
    }

    private BooleanExpression buildWhereWithSoftDelete(BooleanExpression where) {
        return softDelete != null ? softDelete.getActiveExpression().and(where) : where;
    }


    protected List<CompositeKey> skippedList(List<CompositeKey> keys,
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

    protected void transactionModel(MODEL model, Map<CompositeKey, Map<Path<?>, Object>> models) {
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

    protected List<MODEL> findByIdsQuery(List<CompositeKey> keys) {
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


    protected <TYPE extends Serializable> LazyList<MODEL> findAllByColumn(
            CacheFindAllByColumn<TYPE> cacheByColumn,
            TYPE columnValue,
            boolean isNotNull) {
        TransactionalCache transactionCache = cacheByColumn.getTransactionCache();
        List<CompositeKey> primaryKeys =
                transactionCache
                        .getFromCache(cacheByColumn.getCachedColumn(), List.class);
        if (primaryKeys == null) {
            ComparableExpressionBase columnExpression = (ComparableExpressionBase)
                    cacheByColumn.getColumn();
            BooleanExpression booleanExpression =
                    isNotNull ? columnExpression.isNotNull() : columnValue == null
                            ? columnExpression.isNull()
                            : columnExpression.eq(columnValue);
            List<RawModel> rawModels = queryContext.getOrmQueryFactory().select()
                    .rawSelect(expressionQuery(booleanExpression)).findAll(
                            PrimaryKeyExpressionHelper.getPrimaryKeyExpressionColumns(qTable));
            primaryKeys = PrimaryKeyHelper.getPrimaryKeyValues(rawModels, qTable);
            transactionCache.putToCache(cacheByColumn.getCachedColumn(), (Serializable) primaryKeys);
        }

        return ModelLazyListFactory.buildLazyList(qTable, primaryKeys,
                modelClass, queryContext);
    }

    protected <TYPE extends Serializable> LazyList<MODEL> findAllByColumn(Path<TYPE> column,
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
            return findAllByColumn(
                    new CacheFindAllByColumn<>(transactionCache,
                            cachedColumn, column), columnValue, isNotNull);
        } finally {
            transactionCache.unLock(cachedColumn);
        }
    }


    private SQLCommonQuery<?> expressionQuery(BooleanExpression expression) {
        return queryContext.getOrmQueryFactory().buildQuery().from(qTable)
                .where(buildWhereWithSoftDelete(expression));
    }
}
