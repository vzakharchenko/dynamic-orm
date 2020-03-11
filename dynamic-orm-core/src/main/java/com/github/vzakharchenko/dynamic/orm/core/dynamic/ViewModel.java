package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.querydsl.core.types.Expression;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class ViewModel implements Serializable {
    private final String name;

    private List<Expression<?>> expressions;

    private String sql;

    public ViewModel(String name) {
        this.name = StringUtils.upperCase(name, Locale.US);
    }

    public String getName() {
        return name;
    }

    public List<Expression<?>> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<Expression<?>> expressions) {
        this.expressions = expressions;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
