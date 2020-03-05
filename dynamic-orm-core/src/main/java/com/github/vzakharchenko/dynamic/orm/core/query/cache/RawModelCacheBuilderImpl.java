package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.querydsl.core.types.Expression;
import com.querydsl.sql.SQLQuery;
import com.github.vzakharchenko.dynamic.orm.core.RawModel;
import com.github.vzakharchenko.dynamic.orm.core.RawModelBuilder;
import com.github.vzakharchenko.dynamic.orm.core.RawModelBuilderImpl;
import com.github.vzakharchenko.dynamic.orm.core.SelectBuilder;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatistic;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticFactory;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public class RawModelCacheBuilderImpl extends RawModelBuilderImpl {

    private final QueryCacheContext queryCacheContext;
    private final RawModelBuilder rawModelBuilder;

    public RawModelCacheBuilderImpl(SQLQuery sqlQuery,
                                    QueryContextImpl queryContext,
                                    SelectBuilder selectBuilder,
                                    QueryCacheContext queryCacheContext) {
        super(sqlQuery, queryContext, selectBuilder);
        this.rawModelBuilder = new RawModelBuilderImpl((SQLQuery) sqlQuery.clone(),
                queryContext, selectBuilder);
        this.queryCacheContext = queryCacheContext;
    }

    @Override
    public List<RawModel> findAll(List<Expression<?>> columns) {
        QueryStatistic queryStatistic = QueryStatisticFactory
                .buildStatistic(sqlQuery, queryCacheContext.getqRelatedTables());
        String sqlString = showSql(columns);
        queryContext.getCacheContext().register(sqlString, queryStatistic);
        TransactionalCache transactionCache = queryContext.getTransactionCache();
        transactionCache.lock(sqlString);
        try {
            List<RawModel> rawModels = transactionCache.getFromCache(sqlString, List.class);
            if (rawModels == null) {
                rawModels = rawModelBuilder.findAll(columns);
                transactionCache.putToCache(sqlString, (Serializable) rawModels);
            }

            return rawModels;
        } finally {
            transactionCache.unLock(sqlString);
        }
    }

}
