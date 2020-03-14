package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheBuilder;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;

/**
 * The main factory for building queries and data modification
 */
public interface OrmQueryFactory extends CrudOrmQueryFactory {


    /**
     * All queries for fetch data and data models from a database
     * Attention! To fetch pojo models  is necessary to add to "Join query"
     * a relevant QueryDsl Model (QTable)
     * examples:
     * List \<Table\>
     * ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(Qtable.qTable)
     * ,Table.class)
     * result:
     * select id from TABLE;
     * <p>
     * or
     * <p>
     * Long count = ormQueryFactory.select().count(ormQueryFactory.buildQuery()
     * .from(Qtable.qTable))
     * result:
     * select count(*) from TABLE;
     *
     * @return queryBuilder
     */
    SelectBuilder select();

    /**
     * Querydsl Builder for  select () and selectCache().
     * Attention! To fetch pojo models  is necessary to add to "Join query"
     * a relevant QueryDsl Model (QTable). For Example:
     * <p>
     * List \<Table\>
     * ormQueryFactory.select().findAll(ormQueryFactory.buildQuery()
     * .from(Qtable.qTable),Table.class)
     * <p>
     * результат
     * select id from TABLE;
     *
     * @return SQLCommonQuery
     * @see SQLCommonQuery
     * @see SelectBuilder
     * @see SelectCacheBuilder
     */
    SQLCommonQuery<?> buildQuery();


    /**
     * All queries for fetch data and data models from database
     * Same as select(). all the queries are cached.
     * Attention! To save the relevance of the cache. All data modifications
     * should be carried out through OrmQueryFactory using the methods:
     * modify(...)
     * insert(...)
     * updateById(...)
     * deleteById(...)
     * softDeleteById(...)
     * <p>
     * Attention!  If the transaction synchronisation is active, the cache is evicted after commit.
     * <p>
     *
     * @return SelectCacheBuilder
     * @see SelectCacheBuilder
     * @see org.springframework.transaction.annotation.Transactional
     * @see org.springframework.transaction.support.TransactionTemplate
     * @see org.springframework.transaction.support.TransactionSynchronizationManager
     * <p>
     * CLEANING THE CACHE  OCCURS AFTER  ANY MODIFICATION THE TABLES INVOLVED IN THE QUERY
     */
    SelectCacheBuilder selectCache();

    /**
     * Builder cache query for one model type. Cleaned  only the obsolete data
     * Attention! To save the relevance of the cache. All data modifications
     * should be carried out through OrmQueryFactory using the methods:
     * modify(...)
     * insert(...)
     * updateById(...)
     * deleteById(...)
     * softDeleteById(...)
     * <p>
     * Attention!  If the transaction synchronisation is active, the cache is evicted after commit.
     * <p>
     *
     * @param qTable     Metadata Model
     * @param modelClass data Model class
     * @param <MODEL>    data Model type
     * @return Builder cache queries for a data model
     * @see CacheBuilder
     * <p>
     * CLEANING THE CACHE  OCCURS AFTER  ANY MODIFICATION THE TABLES INVOLVED IN THE QUERY
     * <p>
     * THE CACHE IS CLEARED ONLY WITH OUTDATED DATA
     * <p>
     * THIS METHOD ALLOWS YOU TO FULLY SYNCHRONIZE THE DATABASE AND THE CACHE
     */
    <MODEL extends DMLModel> CacheBuilder<MODEL> modelCacheBuilder(RelationalPath<?> qTable,
                                                                   Class<MODEL> modelClass);

    /**
     * Builder cache query for one model type. Cleaned  only the obsolete data
     * Attention! To save the relevance of the cache. All data modifications
     * should be carried out through OrmQueryFactory using the methods:
     * modify(...)
     * insert(...)
     * updateById(...)
     * deleteById(...)
     * softDeleteById(...)
     * <p>
     * Attention!  If the transaction synchronisation is active, the cache is evicted after commit.
     * <p>
     *
     * @param dynamicTable Dynamic Metadata Model
     * @return Builder cache queries for a data model
     * @see CacheBuilder
     * <p>
     * CLEANING THE CACHE  OCCURS AFTER  ANY MODIFICATION THE TABLES INVOLVED IN THE QUERY
     * <p>
     * THE CACHE IS CLEARED ONLY WITH OUTDATED DATA
     * <p>
     * THIS METHOD ALLOWS YOU TO FULLY SYNCHRONIZE THE DATABASE AND THE CACHE
     */
    CacheBuilder<DynamicTableModel> modelCacheBuilder(QDynamicTable dynamicTable);

    /**
     * Builder cache query for one model type. Cleaned  only the obsolete data
     * Attention! To save the relevance of the cache. All data modifications should be carried out
     * through OrmQueryFactory using the methods:
     * Attention! qTable(the Metadata Model) is taken either from the annotation @QueryDslModel,
     * either from Dynamic mode
     * modify(...)
     * insert(...)
     * updateById(...)
     * deleteById(...)
     * softDeleteById(...)
     * <p>
     * Attention!  If the transaction synchronisation is active, the cache is evicted after commit.
     * <p>
     *
     * @param modelClass data Model class
     * @param <MODEL>    data Model type
     * @return Builder cache queries for a data model
     * @see CacheBuilder
     * @see QueryDslModel
     * <p>
     * CLEANING THE CACHE  OCCURS AFTER  ANY MODIFICATION THE TABLES INVOLVED IN THE QUERY
     * <p>
     * THE CACHE IS CLEARED ONLY WITH OUTDATED DATA
     * <p>
     * THIS METHOD ALLOWS YOU TO FULLY SYNCHRONIZE THE DATABASE AND THE CACHE
     */
    <MODEL extends DMLModel> CacheBuilder<MODEL> modelCacheBuilder(Class<MODEL> modelClass);

}
