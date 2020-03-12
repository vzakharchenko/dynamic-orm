package com.github.vzakharchenko.dynamic.orm.core.query;

import com.github.vzakharchenko.dynamic.orm.core.Range;
import com.github.vzakharchenko.dynamic.orm.core.RawModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.mapper.expression.RawModelExpression;
import com.querydsl.core.QueryException;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class UnionBuilderImpl implements UnionBuilder {

    public static final int SIZE = 1;
    protected final QueryContextImpl queryContext;
    protected final List<SubQueryExpression<?>> listSubQueries;
    private final Logger logger = LoggerFactory.getLogger(UnionBuilderImpl.class);
    protected SQLQuery sqlQuery;
    protected boolean unionAll;
    private MappingProjection<RawModel> mappingProjection;

    public UnionBuilderImpl(SQLQuery sqlQuery, List<SubQueryExpression<?>> listSubQueries,
                            boolean unionAll,
                            QueryContextImpl queryContext) {
        this.sqlQuery = sqlQuery;
        this.queryContext = queryContext;
        this.unionAll = unionAll;
        this.listSubQueries = listSubQueries;
    }


    @Override
    public RawModel findOne() {
        try {
            List<RawModel> list = findAll();
            if (list.isEmpty()) {
                return null;
            } else if (list.size() > SIZE) {
                throw new IncorrectResultSizeDataAccessException(1, list.size());
            } else {
                return list.get(0);
            }
        } catch (QueryException qe) {
            throw new QueryException("Sql error: " + showSql(), qe);
        }
    }


    @Override
    public List<RawModel> findAll() {
        Connection connection = DataSourceUtils.getConnection(queryContext.getDataSource());
        SQLQuery query = getUnionQuery().clone(connection);
        MappingProjection<RawModel> simpleRawMapper = createRawModelExpression();
        try {
            if (queryContext.isDebugSql()) {
                logger.info("execute: " + showSql());
            }
            return query.select(simpleRawMapper).fetch();
        } finally {
            DataSourceUtils.releaseConnection(connection, queryContext.getDataSource());
        }
    }


    @Override
    public Long count() {
        Connection connection = DataSourceUtils.getConnection(queryContext.getDataSource());
        try {
            SQLQuery query = getUnionQuery().clone(connection);
            return query.fetchCount();
        } finally {
            DataSourceUtils.releaseConnection(connection, queryContext.getDataSource());
        }
    }


    @Override
    public UnionBuilder groupBy(Expression... columns) {
        sqlQuery = (SQLQuery) sqlQuery.groupBy(columns);
        return this;
    }

    @Override
    public UnionBuilder groupBy(List<Expression> columns) {
        return groupBy(columns.toArray(new Expression[columns.size()]));
    }

    @Override
    public UnionBuilder orderBy(List<OrderSpecifier> orderSpecifiers) {
        return orderBy(orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]));
    }

    @Override
    public UnionBuilder orderBy(OrderSpecifier... orderSpecifiers) {
        sqlQuery = (SQLQuery) sqlQuery.orderBy(orderSpecifiers);
        return this;
    }

    @Override
    public UnionBuilder limit(Range range) {
        sqlQuery.limit(range.getLimit());
        if (range.getOffset() != null) {
            sqlQuery.offset(range.getOffset());
        }
        return this;
    }

    protected SQLQuery getUnionQuery() {
        AbstractSQLQuery query = this.sqlQuery.clone();
        SimplePath<Void> alias = Expressions.path(Void.class, "union");
        SubQueryExpression[] listSubQueries0 = this.listSubQueries.toArray(
                new SubQueryExpression[this.listSubQueries.size()]);
        if (unionAll) {
            query = (AbstractSQLQuery) query.unionAll(alias, listSubQueries0);
        } else {
            query = (AbstractSQLQuery) query.union(alias, listSubQueries0);
        }
        return (SQLQuery) query;
    }

    @Override
    public String showSql() {
        SQLQuery clone = getUnionQuery();
        clone.setUseLiterals(true);
        MappingProjection<RawModel> simpleRawMapper = createRawModelExpression();
        return clone.select(simpleRawMapper).getSQL().getSQL();
    }

    // CHECKSTYLE:OFF
    private List<Expression<?>> getExpressions() {
        List<Expression<?>> columns = new ArrayList<>();
        SubQueryExpression listSubQuery = listSubQueries.get(0);
        Expression<?> projection = listSubQuery.getMetadata().getProjection();
        if (projection instanceof QTuple) {
            QTuple qTuple = (QTuple) projection;
            List<Expression<?>> args = qTuple.getArgs();
            for (Expression<?> arg : args) {
                if (arg instanceof Operation) {
                    Operation operation = (Operation) arg;
                    columns.add(Expressions.template(operation.getType(),
                            "\"" + ModelHelper
                                    .getAliasFromExpression(operation) + "\""));
                } else if (arg instanceof Path) {
                    Path column = (Path) arg;
                    columns.add(Expressions.template(column.getType(),
                            "\"" + ModelHelper.getColumnRealName(column) + "\""));
                } else {
                    columns.add(Expressions.template(arg.getType(),
                            "\"" + arg + "\""));
                }
            }
        } else if (projection instanceof Path) {
            Path column = (Path) projection;
            columns.add(Expressions.template(column.getType(),
                    "\"" + ModelHelper.getColumnRealName(column) + "\""));
        } else {
            columns.add(Expressions.template(projection.getType(),
                    "\"" + projection + "\""));
        }
        return columns;
    }

    // CHECKSTYLE:ON
    @Override
    public String showCountSql() {
        SQLQuery clone = getUnionQuery();
        clone.setUseLiterals(true);
        return clone.select(Wildcard.count).getSQL().getSQL();
    }


    protected MappingProjection<RawModel> createRawModelExpression() {

        if (mappingProjection == null) {

            mappingProjection = new RawModelExpression(getExpressions());
        }

        return mappingProjection;
    }

}
