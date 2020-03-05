package com.github.vzakharchenko.dynamic.orm.core.statistic.resolver;

import com.querydsl.core.JoinExpression;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticRegistrator;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class QueryMetadataResolver implements QueryResolver<QueryMetadata> {

    @Override
    public void resolve(QueryStatisticRegistrator queryStatisticRegistrator,
                        QueryMetadata metadata) {
        List<JoinExpression> joins = metadata.getJoins();
        if (CollectionUtils.isNotEmpty(joins)) {
            for (JoinExpression join : joins) {
                Expression<?> target = join.getTarget();
                QueryResolverFactory.fillStatistic(queryStatisticRegistrator, target);
            }
        }
        Set<QueryFlag> flags = metadata.getFlags();
        if (CollectionUtils.isNotEmpty(flags)) {
            for (QueryFlag flag : flags) {
                QueryResolverFactory.fillStatistic(queryStatisticRegistrator, flag.getFlag());
            }
        }
        List<Expression<?>> groupBy = metadata.getGroupBy();

        if (CollectionUtils.isNotEmpty(groupBy)) {
            for (Expression<?> gB : groupBy) {
                QueryResolverFactory.fillStatistic(queryStatisticRegistrator, gB);
            }
        }

        Map<ParamExpression<?>, Object> params = metadata.getParams();
        if (MapUtils.isNotEmpty(params)) {
            for (ParamExpression<?> paramExpression : params.keySet()) {
                QueryResolverFactory.fillStatistic(queryStatisticRegistrator, paramExpression);
            }
        }
        Expression<?> projection = metadata.getProjection();
        if (projection != null) {
            QueryResolverFactory.fillStatistic(queryStatisticRegistrator, projection);
        }

        Predicate where = metadata.getWhere();
        if (where != null) {
            QueryResolverFactory.fillStatistic(queryStatisticRegistrator, where);
        }

        Predicate having = metadata.getHaving();

        if (having != null) {
            QueryResolverFactory.fillStatistic(queryStatisticRegistrator, having);
        }
    }
}
