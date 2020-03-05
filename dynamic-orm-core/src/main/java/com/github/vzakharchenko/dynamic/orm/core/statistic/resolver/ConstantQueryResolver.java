package com.github.vzakharchenko.dynamic.orm.core.statistic.resolver;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticRegistrator;

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
