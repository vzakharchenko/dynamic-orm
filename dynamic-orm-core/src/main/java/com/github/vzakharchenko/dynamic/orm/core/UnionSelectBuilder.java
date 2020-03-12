package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.query.UnionBuilder;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.SQLCommonQuery;

import java.util.List;

/**
 * All queries for fetch data and data models from a database
 */
public interface UnionSelectBuilder {

    /**
     * union query
     *
     * @param sqlQuery   common querydsl query(for example  CTE)
     * @param subQueries union subqueries
     * @return union query builder
     */
    UnionBuilder union(SQLCommonQuery<?> sqlQuery,
                       SubQueryExpression<?>... subQueries);

    /**
     * union query
     *
     * @param sqlQuery   common querydsl query(for example  CTE)
     * @param subQueries union subqueries
     * @return union query builder
     */
    UnionBuilder union(SQLCommonQuery<?> sqlQuery,
                       List<SubQueryExpression<?>> subQueries);

    /**
     * unionAll query
     *
     * @param sqlQuery   common querydsl query(for example  CTE)
     * @param subQueries union subqueries
     * @return union query builder
     */
    UnionBuilder unionAll(SQLCommonQuery<?> sqlQuery,
                          SubQueryExpression<?>... subQueries);

    /**
     * unionAll query
     *
     * @param sqlQuery   common querydsl query(for example  CTE)
     * @param subQueries union subqueries
     * @return union query builder
     */
    UnionBuilder unionAll(SQLCommonQuery<?> sqlQuery,
                          List<SubQueryExpression<?>> subQueries);


}
