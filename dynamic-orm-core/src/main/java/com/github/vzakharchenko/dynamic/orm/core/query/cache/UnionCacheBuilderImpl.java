package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.SQLQuery;
import com.github.vzakharchenko.dynamic.orm.core.RawModel;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.UnionBuilderImpl;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatistic;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticFactory;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public class UnionCacheBuilderImpl extends UnionBuilderImpl {
    private final QueryCacheContext queryCacheContext;

    public UnionCacheBuilderImpl(SQLQuery sqlQuery, List<SubQueryExpression<?>> listSubQueries,
                                 boolean unionAll,
                                 QueryContextImpl queryContext,
                                 QueryCacheContext queryCacheContext) {
        super(sqlQuery, listSubQueries, unionAll, queryContext);
        this.queryCacheContext = queryCacheContext;
    }

    @Override
    public List<RawModel> findAll() {
        QueryStatistic queryStatistic = QueryStatisticFactory
                .buildStatistic(sqlQuery, queryCacheContext.getqRelatedTables());
        String sqlString = showSql();
        queryContext.getCacheContext().register(sqlString, queryStatistic);
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        transactionCache.lock(sqlString);
        try {
            List<RawModel> list = transactionCache.getFromCache(sqlString, List.class);

            if (list == null) {
                list = super.findAll();
                transactionCache.putToCache(sqlString, (Serializable) list);
            }
            return list;
        } finally {
            transactionCache.unLock(sqlString);
        }
    }

    @Override
    public Long count() {
        QueryStatistic queryStatistic = QueryStatisticFactory
                .buildStatistic(sqlQuery, queryCacheContext.getqRelatedTables());
        String sqlString = showCountSql();
        queryContext.getCacheContext().register(sqlString, queryStatistic);
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        transactionCache.lock(sqlString);
        try {
            Long count = transactionCache.getFromCache(sqlString, Long.class);

            if (count == null) {
                count = super.count();
                transactionCache.putToCache(sqlString, count);
            }
            return count;
        } finally {
            transactionCache.unLock(sqlString);
        }
    }
}
