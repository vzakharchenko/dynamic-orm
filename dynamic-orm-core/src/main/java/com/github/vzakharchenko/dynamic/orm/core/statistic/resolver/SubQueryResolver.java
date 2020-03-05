package com.github.vzakharchenko.dynamic.orm.core.statistic.resolver;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.SubQueryExpression;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticRegistrator;

/**
 *
 */
public class SubQueryResolver implements QueryResolver<SubQueryExpression> {
    @Override
    public void resolve(QueryStatisticRegistrator queryStatisticRegistrator,
                        SubQueryExpression subQueryExpression) {
        QueryMetadata metadata = subQueryExpression.getMetadata();
        QueryResolverFactory.fillStatistic(queryStatisticRegistrator, metadata);
    }
}
