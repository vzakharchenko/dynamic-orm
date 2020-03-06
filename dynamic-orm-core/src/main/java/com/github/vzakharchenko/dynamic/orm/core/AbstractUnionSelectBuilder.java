package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.SQLBuilderHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.UnionBuilder;
import com.github.vzakharchenko.dynamic.orm.core.query.UnionBuilderImpl;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.SQLCommonQuery;

import java.util.Arrays;
import java.util.List;


public abstract class AbstractUnionSelectBuilder implements UnionSelectBuilder {

    protected final QueryContextImpl queryContext;

    public AbstractUnionSelectBuilder(QueryContextImpl queryContext) {
        this.queryContext = queryContext;
    }

    @Override
    public UnionBuilder union(SQLCommonQuery<?> sqlQuery, SubQueryExpression<?>... subQueries) {
        return union(sqlQuery, Arrays.asList(subQueries));
    }

    @Override
    public UnionBuilder union(SQLCommonQuery<?> sqlQuery,
                              List<SubQueryExpression<?>> subQueries) {
        SQLBuilderHelper.subQueryWrapper(subQueries);
        return new UnionBuilderImpl(DBHelper
                .castProjectionQueryToSqlQuery(sqlQuery).clone(),
                subQueries,
                false, queryContext);
    }

    @Override
    public UnionBuilder unionAll(SQLCommonQuery<?> sqlQuery,
                                 SubQueryExpression<?>... subQueries) {
        return unionAll(sqlQuery, Arrays.asList(subQueries));
    }

    @Override
    public UnionBuilder unionAll(SQLCommonQuery<?> sqlQuery,
                                 List<SubQueryExpression<?>> subQueries) {
        SQLBuilderHelper.subQueryWrapper(subQueries);
        return new UnionBuilderImpl(DBHelper.castProjectionQueryToSqlQuery(sqlQuery).clone(),
                subQueries, true, queryContext);
    }

}
