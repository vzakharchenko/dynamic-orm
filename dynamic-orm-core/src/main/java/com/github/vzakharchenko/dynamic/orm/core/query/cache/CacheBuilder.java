package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.SoftDelete;
import com.github.vzakharchenko.dynamic.orm.core.cache.LazyList;
import com.querydsl.core.types.Path;

import java.io.Serializable;
import java.util.List;

/**
 * model cache builder
 * <p>
 * table should have a primary key
 */
public interface CacheBuilder<MODEL extends DMLModel> {

    /**
     * get value by primary key
     *
     * @param key primary Key Value
     * @return data model
     */
    MODEL findOneById(Serializable key);

    /**
     * get list models by primary key List
     *
     * @param keys primary Key Values
     * @return lazyList with Data models
     */
    List<MODEL> findAllByIds(List<? extends Serializable> keys);


    /**
     * query for one column of the table
     * <p>
     * In the cache is put to the column name and the value of this column
     * <p>
     * the cache is evicted only for this value of the column
     * <p>
     * soft deleted models are not cached
     *
     * @param column      column from QTable
     * @param columnValue column value
     * @param <TYPE>
     * @return lazyList with Data models
     * @see SoftDelete
     */
    <TYPE extends Serializable> LazyList<MODEL> findAllByColumn(
            Path<TYPE> column, TYPE columnValue);

    /**
     * query for one column of the table
     * <p>
     * <p>
     * the cache is evicted only when a column changes from NULL to NOT NULL
     * or from NOT NULL to  NULL
     * <p>
     * soft deleted models are not cacheable
     *
     * @param column column from QTable
     * @param <TYPE>
     * @return lazyList with Data models
     * @see SoftDelete
     */
    <TYPE extends Serializable> LazyList<MODEL> findAllByColumnIsNotNull(
            Path<TYPE> column);

    /**
     * fetch all data models from the table
     * <p>
     * Attention! soft deleted models are not cached
     *
     * @return lazyList with Data models
     */
    LazyList<MODEL> findAll();

    /**
     * query for one column of the table
     * <p>
     * In the cache is put to the column name and the value of this column
     * <p>
     * The cache is evicted only for this value of the column
     * <p>
     * Attention! soft deleted models are not cached
     *
     * @param column      column from QTable
     * @param columnValue column value
     * @param <TYPE>
     * @return Data Model
     * @see SoftDelete
     */
    <TYPE extends Serializable> MODEL findOneByColumn(
            Path<TYPE> column, TYPE columnValue);

    /**
     * query for one column of the table
     * <p>
     * <p>
     * The cache is evicted only when a column changes from NULL to NOT NULL
     * or from NOT NULL to  NULL
     * <p>
     * Attention! soft deleted models are not cached
     *
     * @param column column from QTable
     * @param <TYPE>
     * @return Data Model
     * @see SoftDelete
     */
    <TYPE extends Serializable> MODEL findOneByColumnIsNotNull(Path<TYPE> column);


}
