package com.github.vzakharchenko.dynamic.orm.core.mapper.expression;


import com.querydsl.core.types.Expression;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by vzakharchenko on 11.03.15.
 */
public abstract class RawModelExpressionHelper {

    protected static Expression<?>[] getColumns(Iterable<? extends Expression<?>> columns) {
        Set<Expression<?>> columnsSet = new LinkedHashSet<>();
        for (Expression column : columns) {
            columnsSet.add(column);
        }
        return columnsSet.toArray(new Expression[columnsSet.size()]);
    }


}
