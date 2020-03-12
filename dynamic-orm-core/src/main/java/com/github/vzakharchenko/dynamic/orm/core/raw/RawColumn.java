package com.github.vzakharchenko.dynamic.orm.core.raw;

import com.querydsl.core.types.Expression;

public interface RawColumn {

    boolean isValid(Expression expression);

    boolean checkColumn(Expression expression, String columnName);
}
