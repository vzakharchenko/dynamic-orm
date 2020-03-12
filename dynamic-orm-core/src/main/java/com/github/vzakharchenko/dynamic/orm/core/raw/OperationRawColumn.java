package com.github.vzakharchenko.dynamic.orm.core.raw;

import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Path;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class OperationRawColumn implements RawColumn {

    @Override
    public boolean isValid(Expression expression) {
        return expression instanceof Operation;
    }


    @Override
    public boolean checkColumn(Expression expression, String columnName) {
        Operation operation = (Operation) expression;
        List<Expression<?>> expressions = operation.getArgs();
        return expressions.stream().filter(exp -> exp instanceof Path)
                .map(e -> (Path) e)
                .anyMatch(column -> StringUtils
                        .equalsIgnoreCase(ModelHelper
                                .getColumnName(column), columnName));
    }
}
