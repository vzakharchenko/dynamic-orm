package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.RawModel;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.UnionBuilderImpl;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatistic;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticFactory;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.SQLQuery;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class UnionCacheBuilderImpl extends UnionBuilderImpl {
    private final QueryCacheContext queryCacheContext;

    // CHECKSTYLE:OFF
    public UnionCacheBuilderImpl(SQLQuery sqlQuery, List<SubQueryExpression<?>> listSubQueries,
                                 boolean unionAll,
                                 QueryContextImpl queryContext,
                                 QueryCacheContext queryCacheContext) {
        super(sqlQuery, listSubQueries, unionAll, queryContext);
        this.queryCacheContext = queryCacheContext;
    }
    // CHECKSTYLE:ON

    @Override
    public List<RawModel> findAll() {
        QueryStatistic queryStatistic = QueryStatisticFactory
                .buildStatistic(sqlQuery, queryCacheContext.getqRelatedTables());
        String sqlString = showSql();
        StatisticCacheManagerImpl<RawModel> manager = new StatisticCacheManagerImpl<>(
                queryContext.getTransactionCache());
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        transactionCache.lock(sqlString);
        try {
            return manager.get(sqlString, queryStatistic, super::findAll);
        } finally {
            transactionCache.unLock(sqlString);
        }
    }

    private Long count0() {
        return super.count();
    }

    @Override
    public Long count() {
        QueryStatistic queryStatistic = QueryStatisticFactory
                .buildStatistic(getUnionSubQuery(), queryCacheContext.getqRelatedTables());
        String sqlString = showCountSql();
        StatisticCacheManagerImpl<Long> manager = new StatisticCacheManagerImpl<>(
                queryContext.getTransactionCache());
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        transactionCache.lock(sqlString);
        try {
            List<Long> longs = manager.get(sqlString, queryStatistic,
                    () -> Collections.singletonList(count0()));
            return longs.get(0);
        } finally {
            transactionCache.unLock(sqlString);
        }
    }
}
