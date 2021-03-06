package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.mapper.StaticTableMappingProjection;
import com.github.vzakharchenko.dynamic.orm.core.mapper.TableMappingProjectionFactory;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.querydsl.core.QueryException;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;
import com.querydsl.sql.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import static com.github.vzakharchenko.dynamic.orm.core.RawModelBuilderImpl.SIZE;

/**
 *
 */
public class SelectBuilderImpl extends AbstractSelectBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectBuilderImpl.class);

    public SelectBuilderImpl(QueryContextImpl queryContext) {
        super(queryContext);
    }


    @Override
    public <MODEL extends DMLModel> List<MODEL> findAll(
            RelationalPath<?> qTable, Class<MODEL> modelClass) {
        return findAll(queryContext.getOrmQueryFactory().buildQuery(), qTable, modelClass);
    }

    @Override
    public <MODEL extends DMLModel> List<MODEL> findAll(Class<MODEL> modelClass) {
        return findAll(ModelHelper.getQTableFromModel(modelClass), modelClass);
    }

    @Override
    public List<DynamicTableModel> findAll(QDynamicTable dynamicTable) {
        return findAll(dynamicTable, DynamicTableModel.class);
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
    public List<DynamicTableModel> findAll(SQLCommonQuery<?> sqlQuery, QDynamicTable dynamicTable) {
        return findAll(sqlQuery, dynamicTable, DynamicTableModel.class);
    }

    @Override
    public <MODEL extends DMLModel> List<MODEL> findAll(SQLCommonQuery<?> sqlQuery,
                                                        Class<MODEL> modelClass) {
        RelationalPath<?> qTableFromModel = queryContext.getQModel(modelClass);
        return findAll(sqlQuery, qTableFromModel, modelClass);
    }

    @Override
    public <TYPE extends Serializable>
    List<TYPE> findAll(SQLCommonQuery<?> sqlQuery, Expression<TYPE> expression) {

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
    public DynamicTableModel findOne(SQLCommonQuery<?> sqlQuery, QDynamicTable dynamicTable) {
        return findOne(sqlQuery, dynamicTable, DynamicTableModel.class);
    }

    @Override
    public <MODEL extends DMLModel> MODEL findOne(SQLCommonQuery<?> sqlQuery,
                                                  Class<MODEL> modelClass) {
        RelationalPath<?> qTableFromModel = queryContext.getQModel(modelClass);
        return findOne(sqlQuery, qTableFromModel, modelClass);
    }

    @Override
    public <TYPE extends Serializable> TYPE findOne(SQLCommonQuery<?> sqlQuery,
                                                    Expression<TYPE> expression) {
        List<TYPE> list = findAll(sqlQuery, expression);
        if (list.isEmpty()) {
            return null;
        } else if (list.size() > SIZE) {
            throw new IncorrectResultSizeDataAccessException(1, list.size());
        } else {
            return list.get(0);
        }
    }
}
