package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.query.UnionBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;

import java.util.List;

/**
 * All queries for fetch data and data models from a database
 */
public interface SelectBuilder {

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


    /**
     * Allows to query specific columns, as well as Aggregate Functions
     * <p>
     * example:
     * List<RawModel> rawModels = ormQueryFactory.select().rawSelect(ormQueryFactory.buildQuery()
     * .from(QTable.table).groupBy(QTable.table.name)).findAll(QTable.table.name,WildCard.count);
     * for(RawModel rawModel: rawModels){
     * String name = rawModel.getColumnValue(Table.table.name);
     * Long cnt =  rawModel.getColumnValue(WildCard.count);
     * }
     *
     * @param sqlQuery querydsl query
     * @return Builder
     * @see RawModelBuilder
     */
    RawModelBuilder rawSelect(SQLCommonQuery<?> sqlQuery);

    /**
     * creates the sql query from querydsl object query.
     *
     * @param sqlQuery   querydsl query
     * @param expression column or any other expression
     * @return sql query string
     */

    String showSql(SQLCommonQuery<?> sqlQuery, Expression expression);

    /**
     * creates the sql query from querydsl object query.
     *
     * @param sqlQuery   querydsl query
     * @param qTable     querydsl model
     * @param modelClass model class
     * @param <MODEL>    data Model Class
     * @return sql query string
     */
    <MODEL extends DMLModel> String showSql(SQLCommonQuery<?> sqlQuery,
                                            RelationalPath<?> qTable,
                                            Class<MODEL> modelClass);

    /**
     * creates the sql query from querydsl object query.
     *
     * @param sqlQuery   querydsl query
     * @param modelClass model class with annotation com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel
     * @param <MODEL>    data Model Class
     * @return sql query string
     * @see com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel
     */
    <MODEL extends DMLModel> String showSql(SQLCommonQuery<?> sqlQuery,
                                            Class<MODEL> modelClass);

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
