package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.LazyList;

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
     * fetch all data models from the table
     * <p>
     * Attention! soft deleted models are not cached
     *
     * @return lazyList with Data models
     */
    LazyList<MODEL> findAll();


}
