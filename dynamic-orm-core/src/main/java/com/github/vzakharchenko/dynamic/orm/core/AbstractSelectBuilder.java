package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.SQLCommonQuery;

/**
 *
 */
public abstract class AbstractSelectBuilder extends AbstractShowSqlBuilder implements SelectBuilder {


    public AbstractSelectBuilder(QueryContextImpl queryContext) {
        super(queryContext);
    }


    @Override
    public Long count(SQLCommonQuery<?> sqlQuery) {
        return findOne(sqlQuery, Wildcard.count);
    }

    @Override
    public boolean exist(SQLCommonQuery<?> sqlQuery) {
        return count(sqlQuery) > 0;
    }

    @Override
    public boolean notExist(SQLCommonQuery<?> sqlQuery) {
        return count(sqlQuery) == 0;
    }

    @Override
    public RawModelBuilder rawSelect(SQLCommonQuery<?> sqlQuery) {
        return new RawModelBuilderImpl(DBHelper.castProjectionQueryToSqlQuery(sqlQuery),
                queryContext, this);
    }

}
