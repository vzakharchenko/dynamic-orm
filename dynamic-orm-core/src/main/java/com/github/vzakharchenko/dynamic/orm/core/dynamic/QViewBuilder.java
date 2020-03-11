package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.querydsl.core.types.Expression;
import com.querydsl.sql.SQLCommonQuery;

import java.util.List;


public interface QViewBuilder {

    QViewBuilder resultSet(SQLCommonQuery<?> query, Expression<?>... columns);

    QViewBuilder resultSet(String sql, List<Expression<?>> columns);

    QDynamicTableFactory finish();
}
