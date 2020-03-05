package com.github.vzakharchenko.dynamic.orm.core.statistic.resolver;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import org.apache.commons.collections4.CollectionUtils;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticRegistrator;

import java.util.List;

/**
 *
 */
public class OperationResolver implements QueryResolver<Operation> {


    @Override
    public void resolve(QueryStatisticRegistrator queryStatistic, Operation operation) {
        List<Expression<?>> args = operation.getArgs();
        if (CollectionUtils.isNotEmpty(args)) {
            for (Expression<?> arg : args) {
                QueryResolverFactory.fillStatistic(queryStatistic, arg);
            }
        }
    }
}
