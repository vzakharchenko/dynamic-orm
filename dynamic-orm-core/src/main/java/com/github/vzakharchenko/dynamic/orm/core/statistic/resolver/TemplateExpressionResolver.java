package com.github.vzakharchenko.dynamic.orm.core.statistic.resolver;

import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticRegistrator;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.TemplateExpression;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 *
 */
public class TemplateExpressionResolver implements QueryResolver<TemplateExpression> {
    @Override
    public void resolve(QueryStatisticRegistrator queryStatisticRegistrator,
                        TemplateExpression templateExpression) {
        List<Expression<?>> args = templateExpression.getArgs();
        if (CollectionUtils.isNotEmpty(args)) {
            for (Expression<?> arg : args) {
                QueryResolverFactory.fillStatistic(queryStatisticRegistrator, arg);
            }
        }
    }
}
