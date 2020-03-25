package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.querydsl.core.types.Expression;
import com.querydsl.sql.SQLCommonQuery;

import java.util.List;

/**
 * Sql View builder
 */
public interface QViewBuilder {

    /**
     * Create view Using QueryDsl expressions
     * @param query - QueryDsl subquery  (ormQueryFactory.buildQuery()...)
     * @param columns - columns
     * @return View Builder
     */
    QViewBuilder resultSet(SQLCommonQuery<?> query, Expression<?>... columns);
    /**
     * Create view Using raw sql query
     * @param sql - sql subquery
     * @param columns - columns
     * @return View Builder
     */
    QViewBuilder resultSet(String sql, List<Expression<?>> columns);

    /**
     * add View
     * @return Dynamic Table Factory Builder
     */
    QDynamicTableFactory addView();
}
