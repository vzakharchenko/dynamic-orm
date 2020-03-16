package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.*;
import com.github.vzakharchenko.dynamic.orm.core.cache.LazyList;
import com.github.vzakharchenko.dynamic.orm.core.cache.ModelLazyListFactory;
import com.github.vzakharchenko.dynamic.orm.core.helper.*;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.UnionBuilder;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatistic;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticFactory;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
public class SelectCacheBuilderImpl extends SelectBuilderImpl implements SelectCacheBuilder {

    private final SelectBuilder selectBuilder;
    private final QueryCacheContext queryCacheContext = new QueryCacheContext();

    public SelectCacheBuilderImpl(QueryContextImpl queryContext) {
        super(queryContext);
        this.selectBuilder = new SelectBuilderImpl(queryContext);
    }

    @Override
    public <MODEL extends DMLModel> List<MODEL> findAll(SQLCommonQuery<?> sqlQuery,
                                                        RelationalPath<?> qTable,
                                                        Class<MODEL> modelClass) {
        SQLCommonQuery<?> sqlQuery0 = sqlQuery;
        sqlQuery0 = validateQuery(sqlQuery0, qTable, modelClass);
        QueryStatistic queryStatistic = QueryStatisticFactory
                .buildStatistic(DBHelper.castProjectionQueryToSqlQuery(sqlQuery0),
                        queryCacheContext.getqRelatedTables());
        List<? extends Path<?>> primaryKeyColumns = PrimaryKeyHelper.getPrimaryKeyColumns(qTable);
        String sqlString = showListSql(sqlQuery0, primaryKeyColumns);
        queryContext.getCacheContext().register(sqlString, queryStatistic);
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        transactionCache.lock(sqlString);
        try {
            List<CompositeKey> primaryKeys = transactionCache.getFromCache(sqlString, List.class);

            if (primaryKeys == null) {
                primaryKeys = selectPrimaryKeys(sqlQuery0, qTable, primaryKeyColumns);
                transactionCache.putToCache(sqlString, (Serializable) primaryKeys);
            }
            LazyList<MODEL> lazyList = ModelLazyListFactory.buildLazyList(qTable,
                    primaryKeys, modelClass, queryContext);
            return lazyList.getModelList();
        } finally {
            transactionCache.unLock(sqlString);
        }
    }

    private List<CompositeKey> selectPrimaryKeys(SQLCommonQuery<?> sqlQuery,
                                                 RelationalPath<?> qTable,
                                                 List<? extends Path<?>> primaryKeyColumns) {
        List<RawModel> rawModels = selectBuilder.rawSelect(sqlQuery)
                .findAll(primaryKeyColumns.stream().map((Function<Path<?>, Expression<?>>)
                        path -> path).collect(Collectors.toList()));
        return rawModels.stream().map(rawModel -> {
            CompositeKeyBuilder builder = CompositeKeyBuilder.create(qTable);
            primaryKeyColumns.forEach((Consumer<Path<?>>) path -> {
                Object columnValue = rawModel.getColumnValue(path);
                builder.addPrimaryKey(path, columnValue);
            });
            return builder.build();
        }).collect(Collectors.toList());
    }

    @Override
    public <TYPE> List<TYPE> findAll(SQLCommonQuery<?> sqlQuery,
                                     Expression<TYPE> expression) {
        QueryStatistic queryStatistic = QueryStatisticFactory
                .buildStatistic(DBHelper.castProjectionQueryToSqlQuery(sqlQuery),
                        queryCacheContext.getqRelatedTables());
        String key = showSql(sqlQuery, expression);
        queryContext.getCacheContext().register(key, queryStatistic);
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        transactionCache.lock(key);
        try {
            List<TYPE> cached = transactionCache.getFromCache(key, List.class);

            if (cached == null) {
                cached = selectBuilder.findAll(sqlQuery, expression);
                transactionCache.putToCache(key, (Serializable) cached);
            }

            return cached;
        } finally {
            transactionCache.unLock(key);
        }
    }

    @Override
    public SelectCacheBuilder registerRelatedTables(Collection<RelationalPath> qTables) {
        queryCacheContext.registerRelatedTables(qTables);
        return this;
    }


    @Override
    public UnionBuilder union(SQLCommonQuery<?> sqlQuery,
                              List<SubQueryExpression<?>> subQueries) {
        SQLBuilderHelper.subQueryWrapper(subQueries);

        return new UnionCacheBuilderImpl(DBHelper
                .castProjectionQueryToSqlQuery(sqlQuery).clone(), subQueries,
                false, queryContext, queryCacheContext);
    }

    @Override
    public UnionBuilder unionAll(SQLCommonQuery<?> sqlQuery,
                                 List<SubQueryExpression<?>> subQueries) {
        SQLBuilderHelper.subQueryWrapper(subQueries);

        return new UnionCacheBuilderImpl(DBHelper
                .castProjectionQueryToSqlQuery(sqlQuery).clone(),
                subQueries, true, queryContext, queryCacheContext);
    }

    @Override
    public RawModelBuilder rawSelect(SQLCommonQuery<?> sqlQuery) {
        return new RawModelCacheBuilderImpl(DBHelper
                .castProjectionQueryToSqlQuery(sqlQuery).clone(),
                queryContext, this, queryCacheContext);
    }
}
