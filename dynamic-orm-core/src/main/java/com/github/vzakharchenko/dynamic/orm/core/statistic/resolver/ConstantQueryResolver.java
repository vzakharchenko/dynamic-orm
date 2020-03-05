package com.github.vzakharchenko.dynamic.orm.core.statistic.resolver;

import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticRegistrator;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;

/**
 *
 */
public class ConstantQueryResolver implements QueryResolver<Constant> {
    @Override
    public void resolve(QueryStatisticRegistrator queryStatisticRegistrator,
                        Constant constant) {
        Object constantConstant = constant.getConstant();
        if (constantConstant instanceof Expression) {
            QueryResolverFactory.fillStatistic(queryStatisticRegistrator, constant);
        }
    }
}
