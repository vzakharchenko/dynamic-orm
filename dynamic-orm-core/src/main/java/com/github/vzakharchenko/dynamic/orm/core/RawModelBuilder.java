package com.github.vzakharchenko.dynamic.orm.core;

import com.querydsl.core.types.Expression;

import java.util.List;

/**
 * Allows to query specific columns, as well as Aggregate Functions
 */
public interface RawModelBuilder {

    /**
     * fetch all
     *
     * @param columns any columns or aggregate functions or subqueries
     * @return list of Map Column-Object
     */
    List<RawModel> findAll(Expression<?>... columns);

    /**
     * fetch all
     *
     * @param columns any columns or aggregate functions or subqueries
     * @return list of Map Column-Object
     */
    List<RawModel> findAll(List<Expression<?>> columns);

    /**
     * fetch one rawModel
     *
     * @param columns any columns or aggregate functions or subqueries
     * @return Map Column-Object
     */
    RawModel findOne(Expression<?>... columns);

    /**
     * fetch one rawModel
     *
     * @param columns any columns or aggregate functions or subqueries
     * @return Map Column-Object
     */
    RawModel findOne(List<Expression<?>> columns);

    String showSql(Expression<?>... columns);

    String showSql(List<Expression<?>> columns);
}
