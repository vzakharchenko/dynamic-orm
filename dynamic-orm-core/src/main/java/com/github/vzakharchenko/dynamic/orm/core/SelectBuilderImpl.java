package com.github.vzakharchenko.dynamic.orm.core;

import com.querydsl.core.JoinExpression;
import com.querydsl.core.QueryException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;
import com.querydsl.sql.SQLQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.SQLBuilderHelper;
import com.github.vzakharchenko.dynamic.orm.core.mapper.StaticTableMappingProjection;
import com.github.vzakharchenko.dynamic.orm.core.mapper.TableMappingProjectionFactory;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.UnionBuilder;
import com.github.vzakharchenko.dynamic.orm.core.query.UnionBuilderImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDelete;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class SelectBuilderImpl implements SelectBuilder {

    private static Logger logger = LoggerFactory.getLogger(SelectBuilderImpl.class);

    protected final QueryContextImpl queryContext;

    public SelectBuilderImpl(QueryContextImpl queryContext) {
        this.queryContext = queryContext;
    }


    @Override
    public <MODEL extends DMLModel> List<MODEL> findAll(SQLCommonQuery<?> sqlQuery,
                                                        RelationalPath<?> qTable,
                                                        Class<MODEL> modelClass) {
        queryContext.validateModel(qTable, modelClass);
        StaticTableMappingProjection<MODEL> expression = TableMappingProjectionFactory
                .buildMapper(qTable, modelClass);
        return findAll(validateQuery(sqlQuery, qTable, modelClass), expression);
    }

    @Override
    public <MODEL extends DMLModel> List<MODEL> findAll(SQLCommonQuery<?> sqlQuery,
                                                        Class<MODEL> modelClass) {
        RelationalPath<?> qTableFromModel = queryContext.getQModel(modelClass);
        return findAll(sqlQuery, qTableFromModel, modelClass);
    }


    protected SQLCommonQuery<?> validateQuery(SQLCommonQuery<?> sqlQuery, RelationalPath<?> qTable,
                                              Class<? extends DMLModel> modelClass) {
        SQLCommonQuery<?> sqlQuery0 = sqlQuery;
        if (!searchQModel(sqlQuery0, qTable)) {
            if (CollectionUtils.isEmpty(DBHelper.castProjectionQueryToSqlQuery(sqlQuery0)
                    .getMetadata().getJoins())) {
                SoftDelete<?> softDeleteColumn = queryContext.getSoftDeleteColumn(qTable,
                        modelClass);
                sqlQuery0 = sqlQuery0.from(qTable);
                return softDeleteColumn != null ? sqlQuery0.where(softDeleteColumn
                        .getActiveExpression()) : sqlQuery0;
            } else {
                throw new IllegalStateException(qTable + " is not found in projection");
            }
        }
        return sqlQuery0;
    }

    private boolean searchQModel(SQLCommonQuery<?> sqlQuery, RelationalPath<?> qTable) {
        List<JoinExpression> joinExpressions = DBHelper.castProjectionQueryToSqlQuery(sqlQuery)
                .getMetadata().getJoins();
        for (JoinExpression joinExpression : joinExpressions) {
            if (joinExpression.getTarget() instanceof RelationalPath) {
                RelationalPath<?> relationalPathBase = (RelationalPath) joinExpression.getTarget();
                if (StringUtils.equals(relationalPathBase.getTableName(), qTable.getTableName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public <TYPE> List<TYPE> findAll(SQLCommonQuery<?> sqlQuery, Expression<TYPE> expression) {

        try {
            Connection connection = DataSourceUtils.getConnection(queryContext.getDataSource());
            try {

                SQLQuery<?> cloneSQLQuery = DBHelper.castProjectionQueryToSqlQuery(sqlQuery)
                        .clone(connection);
                if (queryContext.isDebugSql()) {
                    logger.info("execute: " + showSql(sqlQuery, expression));
                }
                return cloneSQLQuery.select(expression).fetch();
            } finally {
                DataSourceUtils.releaseConnection(connection, queryContext.getDataSource());
            }
        } catch (QueryException qe) {
            throw new QueryException("Sql error: " + showSql(sqlQuery, expression), qe);
        }
    }

    @Override
    public <MODEL extends DMLModel> MODEL findOne(SQLCommonQuery<?> sqlQuery,
                                                  RelationalPath<?> qTable,
                                                  Class<MODEL> modelClass) {
        List<MODEL> list = findAll(sqlQuery, qTable, modelClass);
        if (list.isEmpty()) {
            return null;
        } else if (list.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, list.size());
        } else {
            return list.get(0);
        }
    }

    @Override
    public <MODEL extends DMLModel> MODEL findOne(SQLCommonQuery<?> sqlQuery,
                                                  Class<MODEL> modelClass) {
        RelationalPath<?> qTableFromModel = queryContext.getQModel(modelClass);
        return findOne(sqlQuery, qTableFromModel, modelClass);
    }

    @Override
    public <TYPE> TYPE findOne(SQLCommonQuery<?> sqlQuery, Expression<TYPE> expression) {
        try {
            List<TYPE> list = findAll(sqlQuery, expression);
            if (list.isEmpty()) {
                return null;
            } else if (list.size() > 1) {
                throw new IncorrectResultSizeDataAccessException(1, list.size());
            } else {
                return list.get(0);
            }
        } catch (QueryException qe) {
            throw new QueryException("Sql error: " + showSql(sqlQuery, expression), qe);
        }
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
    public String showSql(SQLCommonQuery<?> sqlQuery, Expression expression) {
        SQLQuery<?> clone = DBHelper.castProjectionQueryToSqlQuery(sqlQuery).clone();
        clone.setUseLiterals(true);
        return clone.select(expression).getSQL().getSQL();
    }

    @Override
    public <MODEL extends DMLModel> String showSql(SQLCommonQuery<?> sqlQuery,
                                                   RelationalPath<?> qTable,
                                                   Class<MODEL> modelClass) {
        queryContext.validateModel(qTable, modelClass);
        return showSql(validateQuery(DBHelper.castProjectionQueryToSqlQuery(sqlQuery).clone(),
                qTable, modelClass), TableMappingProjectionFactory.buildMapper(qTable, modelClass));
    }

    @Override
    public <MODEL extends DMLModel> String showSql(SQLCommonQuery<?> sqlQuery,
                                                   Class<MODEL> modelClass) {
        RelationalPath<?> qTableFromModel = queryContext.getQModel(modelClass);
        return showSql(sqlQuery, qTableFromModel, modelClass);
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

    @Override
    public RawModelBuilder rawSelect(SQLCommonQuery<?> sqlQuery) {
        return new RawModelBuilderImpl(DBHelper.castProjectionQueryToSqlQuery(sqlQuery),

                queryContext, this);
    }

}
