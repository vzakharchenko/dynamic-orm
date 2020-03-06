package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.mapper.StaticTableMappingProjection;
import com.github.vzakharchenko.dynamic.orm.core.mapper.TableMappingProjectionFactory;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.querydsl.core.QueryException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;
import com.querydsl.sql.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.sql.Connection;
import java.util.List;

/**
 *
 */
public class SelectBuilderImpl extends AbstractShowSqlBuilder implements SelectBuilder {

    private static final int SIZE = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(SelectBuilderImpl.class);

    public SelectBuilderImpl(QueryContextImpl queryContext) {
        super(queryContext);
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

    @Override
    public <TYPE> List<TYPE> findAll(SQLCommonQuery<?> sqlQuery, Expression<TYPE> expression) {

        try {
            Connection connection = DataSourceUtils.getConnection(queryContext.getDataSource());
            try {

                SQLQuery<?> cloneSQLQuery = DBHelper.castProjectionQueryToSqlQuery(sqlQuery)
                        .clone(connection);
                if (queryContext.isDebugSql()) {
                    LOGGER.info("execute: " + showSql(sqlQuery, expression));
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
        } else if (list.size() > SIZE) {
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
            } else if (list.size() > SIZE) {
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
    public RawModelBuilder rawSelect(SQLCommonQuery<?> sqlQuery) {
        return new RawModelBuilderImpl(DBHelper.castProjectionQueryToSqlQuery(sqlQuery),

                queryContext, this);
    }

}
