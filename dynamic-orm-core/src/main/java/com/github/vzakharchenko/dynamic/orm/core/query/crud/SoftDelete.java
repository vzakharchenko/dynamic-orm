package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;

import java.io.Serializable;

/**
 *
 */
public class SoftDelete<TYPE extends Serializable> implements Serializable {
    private final Path<TYPE> column;
    private final TYPE value;
    private final TYPE defaultValue;

    protected SoftDelete(Path<TYPE> column, TYPE value, TYPE defaultValue) {
        this.column = column;
        this.value = value;
        this.defaultValue = defaultValue;
    }


    public BooleanExpression getActiveExpression() {
        SimpleExpression<TYPE> expression = (SimpleExpression<TYPE>) column;
        if (value == null) {
            return expression.isNotNull();
        }
        return expression.ne(value);
    }

    public BooleanExpression getDeleteExpression() {
        SimpleExpression<TYPE> expression = (SimpleExpression<TYPE>) column;
        if (value == null) {
            return expression.isNull();
        }
        return expression.eq(value);
    }

    public Path<TYPE> getColumn() {
        return column;
    }

    public TYPE getDeletedValue() {
        return value;
    }

    public TYPE getDefaultValue() {
        return defaultValue;
    }
}
