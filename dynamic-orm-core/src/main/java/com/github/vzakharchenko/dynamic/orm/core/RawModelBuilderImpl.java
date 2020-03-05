package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.mapper.expression.RawModelExpression;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticImpl;
import com.github.vzakharchenko.dynamic.orm.core.statistic.resolver.QueryResolverFactory;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.QueryException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.MappingProjection;
import com.querydsl.sql.SQLQuery;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class RawModelBuilderImpl implements RawModelBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RawModelBuilderImpl.class);

    protected final SQLQuery sqlQuery;

    protected final QueryContextImpl queryContext;
    protected final SelectBuilder selectBuilder;
    protected MappingProjection<RawModel> mappingProjection;

    public RawModelBuilderImpl(SQLQuery sqlQuery, QueryContextImpl queryContext,
                               SelectBuilder selectBuilder) {
        this.sqlQuery = sqlQuery;
        this.queryContext = queryContext;
        this.selectBuilder = selectBuilder;
    }

    @Override
    public RawModel findOne(Expression<?>... columns) {
        return findOne(Arrays.asList(columns));
    }

    @Override
    public RawModel findOne(List<Expression<?>> columns) {
        try {

            List<RawModel> list = findAll(columns);
            if (list.isEmpty()) {
                return null;
            } else if (list.size() > 1) {
                throw new IncorrectResultSizeDataAccessException(1, list.size());
            } else {
                return list.get(0);
            }
        } catch (QueryException qe) {
            MappingProjection<RawModel> rawModelExpression = createRawModelExpression(sqlQuery,
                    columns);
            throw new QueryException("Sql error: " + selectBuilder.showSql(sqlQuery,
                    rawModelExpression), qe);
        }
    }

    @Override
    public String showSql(Expression<?>... columns) {
        return showSql(Arrays.asList(columns));
    }

    @Override
    public String showSql(List<Expression<?>> columns) {
        MappingProjection<RawModel> rawModelExpression = createRawModelExpression(sqlQuery,
                columns);
        return selectBuilder.showSql(sqlQuery, rawModelExpression);
    }

    @Override
    public List<RawModel> findAll(Expression<?>... columns) {
        return findAll(Arrays.asList(columns));
    }

    @Override
    public List<RawModel> findAll(List<Expression<?>> columns) {
        MappingProjection<RawModel> rawModelExpression = createRawModelExpression(sqlQuery,
                columns);
        try {
            Connection connection = DataSourceUtils
                    .getConnection(queryContext.getDataSource());
            try {
                if (queryContext.isDebugSql()) {
                    LOGGER.info("execute: " + selectBuilder
                            .showSql(sqlQuery, rawModelExpression));
                }
                return sqlQuery.clone(connection).select(rawModelExpression).fetch();

            } finally {
                DataSourceUtils.releaseConnection(connection, queryContext.getDataSource());
            }
        } catch (QueryException qe) {
            throw new QueryException("Sql error: " + selectBuilder
                    .showSql(sqlQuery, rawModelExpression), qe);
        }
    }

    protected MappingProjection<RawModel> createRawModelExpression(SQLQuery query,
                                                                   List<Expression<?>> columns) {

        if (mappingProjection == null) {
            if (CollectionUtils.isEmpty(columns)) {
                List<JoinExpression> joins = query.getMetadata().getJoins();
                QueryStatisticImpl queryStatistic = new QueryStatisticImpl();
                for (JoinExpression join : joins) {
                    QueryResolverFactory.fillStatistic(queryStatistic, join);
                }
                mappingProjection = RawModelExpression.createFromTables(queryStatistic.getTables());
            } else {
                mappingProjection = new RawModelExpression(columns);
            }
        }

        return mappingProjection;
    }
}
