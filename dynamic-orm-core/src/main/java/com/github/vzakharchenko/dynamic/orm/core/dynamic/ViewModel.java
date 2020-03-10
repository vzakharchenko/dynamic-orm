package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.querydsl.core.types.Expression;
import com.querydsl.sql.SQLCommonQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;

public class ViewModel {
    private final String name;

    private SQLCommonQuery<?> query;
    private List<Expression<?>> expressions;

    private String sql;

    public ViewModel(String name) {
        this.name = StringUtils.upperCase(name, Locale.US);
    }

    public String getName() {
        return name;
    }

    public SQLCommonQuery<?> getQuery() {
        return query;
    }

    public void setQuery(SQLCommonQuery<?> query) {
        this.query = query;
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
