package com.github.vzakharchenko.dynamic.orm.core;

import com.querydsl.core.types.Expression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;

/**
 * All queries for fetch data and data models from a database
 */
public interface ShowSqlBuilder {

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

}
