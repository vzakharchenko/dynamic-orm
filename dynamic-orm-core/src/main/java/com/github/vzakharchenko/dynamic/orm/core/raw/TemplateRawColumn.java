package com.github.vzakharchenko.dynamic.orm.core.raw;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.TemplateExpression;
import org.apache.commons.lang3.StringUtils;

public class TemplateRawColumn implements RawColumn {

    @Override
    public boolean isValid(Expression expression) {
        return expression instanceof TemplateExpression;
    }


    @Override
    public boolean checkColumn(Expression expression, String columnName) {
        TemplateExpression operation = (TemplateExpression) expression;
        return
                StringUtils.equalsIgnoreCase(columnName,
                        operation.getTemplate().toString())
                        ||
                        StringUtils.equalsIgnoreCase("\"" + columnName + "\"",
                                operation.getTemplate().toString()) ||
                        StringUtils.equalsIgnoreCase("'" + columnName + "'",
                                operation.getTemplate().toString());
    }
}
