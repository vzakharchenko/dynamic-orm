package com.github.vzakharchenko.dynamic.orm.core.raw;

import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import org.apache.commons.lang3.StringUtils;

public class PathRawColumn implements RawColumn {

    @Override
    public boolean isValid(Expression expression) {
        return expression instanceof Path;
    }

    @Override
    public boolean checkColumn(Expression expression, String columnName) {
        Path column = (Path) expression;
        return StringUtils.equalsIgnoreCase(ModelHelper.getColumnName(column), columnName);
    }
}
