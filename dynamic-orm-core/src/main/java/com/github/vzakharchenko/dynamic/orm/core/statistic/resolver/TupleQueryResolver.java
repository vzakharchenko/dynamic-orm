package com.github.vzakharchenko.dynamic.orm.core.statistic.resolver;

import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticRegistrator;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.QTuple;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class TupleQueryResolver implements QueryResolver<QTuple> {
    @Override
    public void resolve(QueryStatisticRegistrator queryStatisticRegistrator, QTuple tuple) {
        List<Expression<?>> expressions = tuple.getArgs();
        if (CollectionUtils.isNotEmpty(expressions)) {
            expressions.forEach(expression ->
                    QueryResolverFactory.fillStatistic(queryStatisticRegistrator, expression));
        }
    }
}
