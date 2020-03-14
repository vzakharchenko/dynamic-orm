package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;

import java.util.List;

/**
 * All queries for fetch data and data models from a database
 */
public interface SelectBuilder
        extends UnionSelectBuilder, RawSelectBuilder, ShowSqlBuilder {

    /**
     * The fetch all  data models from a database using sqlQuery query.
     * the result is mapped to the data models
     *
     * @param qTable     QueryDsl Model (QTable)
     * @param modelClass data MOdel Class
     * @param <MODEL>    data MOdel Class
     * @return List of Models
     */
    <MODEL extends DMLModel> List<MODEL> findAll(RelationalPath<?> qTable,
                                                 Class<MODEL> modelClass);

    /**
     * The fetch all  data models from a database using sqlQuery query.
     * the result is mapped to the data models
     *
     * @param dynamicTable Dynamic metadata
     * @return List of Models
     */
    List<DynamicTableModel> findAll(QDynamicTable dynamicTable);

    /**
     * The fetch all  data models from a database using sqlQuery query.
     * the result is mapped to the data models
     *
     * @param sqlQuery   queryDsl query
     * @param qTable     QueryDsl Model (QTable)
     * @param modelClass data MOdel Class
     * @param <MODEL>    data MOdel Class
     * @return List of Models
     */
    <MODEL extends DMLModel> List<MODEL> findAll(SQLCommonQuery<?> sqlQuery,
                                                 RelationalPath<?> qTable,
                                                 Class<MODEL> modelClass);

    /**
     * The fetch all  data models from a database using sqlQuery query.
     * the result is mapped to the data models
     *
     * @param sqlQuery     queryDsl query
     * @param dynamicTable dynamic metadata
     * @return List of Models
     */
    List<DynamicTableModel> findAll(SQLCommonQuery<?> sqlQuery,
                                    QDynamicTable dynamicTable);

    /**
     * The fetch all  data models from a database using sqlQuery query.
     * the result is mapped to the data models
     * Attention! qTable(the Metadata Model) is taken either from the annotation @QueryDslModel
     * , either from Dynamic model
     *
     * @param sqlQuery   queryDsl query
     * @param modelClass data MOdel Class
     * @param <MODEL>    data MOdel Class
     * @return List of Models
     */
    <MODEL extends DMLModel> List<MODEL> findAll(SQLCommonQuery<?> sqlQuery,
                                                 Class<MODEL> modelClass);


    /**
     * The fetch all data  from a database using sqlQuery query.
     *
     * @param sqlQuery   queryDsl query
     * @param expression any QueryDsl Expression
     * @return List of results
     */
    <TYPE> List<TYPE> findAll(SQLCommonQuery<?> sqlQuery, Expression<TYPE> expression);

    /**
     * The fetch a data model from a database using sqlQuery query.
     * the result is mapped to the data models
     *
     * @param sqlQuery   queryDsl query
     * @param qTable     QueryDsl Model (QTable)
     * @param modelClass data MOdel Class
     * @param <MODEL>    data MOdel Class
     * @return MODEL
     */
    <MODEL extends DMLModel> MODEL findOne(SQLCommonQuery<?> sqlQuery,
                                           RelationalPath<?> qTable,
                                           Class<MODEL> modelClass);
    /**
     * The fetch a data model from a database using sqlQuery query.
     * the result is mapped to the data models
     *
     * @param sqlQuery   queryDsl query
     * @param dynamicTable Dynamic Table Metadata
     * @return MODEL
     */
    DynamicTableModel findOne(SQLCommonQuery<?> sqlQuery,
                                           QDynamicTable dynamicTable);

    /**
     * The fetch a data model from a database using sqlQuery query.
     * the result is mapped to the data models
     * Attention! qTable(the Metadata Model) is taken either from the annotation @QueryDslModel
     * , either from Dynamic model
     *
     * @param sqlQuery   queryDsl query
     * @param modelClass data MOdel Class
     * @param <MODEL>    data MOdel Class
     * @return MODEL
     */
    <MODEL extends DMLModel> MODEL findOne(SQLCommonQuery<?> sqlQuery,
                                           Class<MODEL> modelClass);


    /**
     * The fetch a data  from a database using sqlQuery query.
     *
     * @param sqlQuery   queryDsl query
     * @param expression any QueryDsl Expression
     * @return typed result
     */
    <TYPE> TYPE findOne(SQLCommonQuery<?> sqlQuery,
                        Expression<TYPE> expression);


    /**
     * The count of rows in a database
     *
     * @param sqlQuery queryDsl query
     * @return The number of rows
     */
    Long count(SQLCommonQuery<?> sqlQuery);

    /**
     * exists
     *
     * @param sqlQuery queryDsl query
     * @return true if exists rows
     */
    boolean exist(SQLCommonQuery<?> sqlQuery);

    /**
     * not exists
     *
     * @param sqlQuery queryDsl query
     * @return true if not exists rows
     */
    boolean notExist(SQLCommonQuery<?> sqlQuery);
}
