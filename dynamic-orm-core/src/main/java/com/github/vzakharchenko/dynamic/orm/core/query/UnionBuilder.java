package com.github.vzakharchenko.dynamic.orm.core.query;

import com.github.vzakharchenko.dynamic.orm.core.Range;
import com.github.vzakharchenko.dynamic.orm.core.RawModel;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.sql.SQLQuery;

import java.util.List;

/**
 *
 */
public interface UnionBuilder {
    /**
     * fetch one response
     *
     * @return result map expression->value
     * @see RawModel
     */
    RawModel findOne();

    /**
     * fetch all data from database
     *
     * @return result map expression->value
     * @see RawModel
     */
    List<RawModel> findAll();

    /**
     * returns amount of data
     *
     * @return
     */
    Long count();

    /**
     * group by
     *
     * @param columns
     * @return
     */
    UnionBuilder groupBy(Expression... columns);

    UnionBuilder groupBy(String... columns);

    UnionBuilder groupBy(List<Expression> columns);

    UnionBuilder orderBy(List<OrderSpecifier> orderSpecifiers);

    UnionBuilder orderBy(OrderSpecifier... orderSpecifiers);

    OrderByBuilder orderBy(String columnName);

    /**
     * limit
     *
     * @param range
     * @return
     */
    UnionBuilder limit(Range range);

    /**
     * get result as subquery
     *
     * @return subquery
     */
    SQLQuery getUnionSubQuery();

    /**
     * create sql query string
     *
     * @return
     */
    String showSql();


    /**
     * build sql query string for amount query
     *
     * @return
     */
    String showCountSql();


}
