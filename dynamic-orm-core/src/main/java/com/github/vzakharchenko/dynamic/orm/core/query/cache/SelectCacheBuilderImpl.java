package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.*;
import com.github.vzakharchenko.dynamic.orm.core.cache.LazyList;
import com.github.vzakharchenko.dynamic.orm.core.cache.ModelLazyListFactory;
import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.SQLBuilderHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.UnionBuilder;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatistic;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticFactory;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

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
        ComparableExpressionBase primaryKey = ModelHelper.getPrimaryKey(qTable);
        String sqlString = showSql(sqlQuery0, primaryKey);
        queryContext.getCacheContext().register(sqlString, queryStatistic);
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        transactionCache.lock(sqlString);
        try {
            List<Serializable> primaryKeys = transactionCache.getFromCache(sqlString, List.class);

            if (primaryKeys == null) {
                primaryKeys = selectBuilder.findAll(sqlQuery0, primaryKey);
                transactionCache.putToCache(sqlString, (Serializable) primaryKeys);
            }
            LazyList<MODEL> lazyList = ModelLazyListFactory.buildLazyList(qTable,
                    primaryKeys, modelClass, queryContext);
            return lazyList.getModelList();
        } finally {
            transactionCache.unLock(sqlString);
        }
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
