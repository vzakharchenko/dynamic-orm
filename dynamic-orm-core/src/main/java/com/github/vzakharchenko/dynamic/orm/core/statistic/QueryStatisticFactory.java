package com.github.vzakharchenko.dynamic.orm.core.statistic;

import com.github.vzakharchenko.dynamic.orm.core.statistic.resolver.QueryResolverFactory;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;

import java.util.Collection;

/**
 *
 */
public abstract class QueryStatisticFactory {

    public static QueryStatistic buildStatistic(SQLQuery sqlQuery,
                                                Collection<RelationalPath> qTables) {
        QueryStatisticImpl queryStatistic = new QueryStatisticImpl();
        queryStatistic.register(qTables);
        QueryResolverFactory.fillSQLQueryStatistic(queryStatistic, sqlQuery);
        return queryStatistic;
    }


}
