package com.github.vzakharchenko.dynamic.orm.core;

import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;
import com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.SequanceName;
import com.github.vzakharchenko.dynamic.orm.core.annotations.SoftDelete;
import com.github.vzakharchenko.dynamic.orm.core.annotations.Version;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContext;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheBuilder;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.CrudBuilder;
import com.github.vzakharchenko.dynamic.orm.core.transaction.TransactionBuilder;

import java.util.List;

/**
 * The main factory for building queries and data modification
 */
public interface OrmQueryFactory {

    /**
     * transaction Manager. can start a transaction. Commit changes or rollback them.
     * Instead transactionManager (), you can use
     * Spring transaction Manager
     * {@link "http://docs.spring.io/spring/docs/current/
     * spring-framework-reference/html/transaction.html"}
     * (Using annotation @Transaction or
     * org.springframework.transaction.support.TransactionTemplate)
     * <p>
     * Attention!  All modification Queries should be in the transaction
     *
     * @return TransactionBuilder
     * @see TransactionBuilder
     * @see org.springframework.transaction.annotation.Transactional
     * @see org.springframework.transaction.support.TransactionTemplate
     * @see org.springframework.transaction.support.TransactionSynchronizationManager
     */
    TransactionBuilder transactionManager();

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
     * Modification data for the corresponding qtable(the Metadata Model) class and data model
     * <p>
     * Attention!  All modification Queries should be in the transaction Synchronisation
     *
     * @param qTable     Metadata Model
     * @param modelClass class of data model
     * @param <MODEL>    type of data model
     * @return modification builder
     * @see CrudBuilder
     */
    <MODEL extends DMLModel> CrudBuilder<MODEL> modify(RelationalPath<?> qTable,
                                                       Class<MODEL> modelClass);


    /**
     * Modification data for the corresponding qtable(the Metadata Model) class and data model
     * <p>
     * Attention!  All modification Queries should be in the transaction Synchronisation
     * Attention! qTable(the Metadata Model) is taken either from the annotation @QueryDslModel,
     * either from Dynamic model
     *
     * @param modelClass class of data model
     * @param <MODEL>    type of data model
     * @return modification builder
     * @see CrudBuilder
     * @see QueryDslModel
     */
    <MODEL extends DMLModel> CrudBuilder<MODEL> modify(Class<MODEL> modelClass);

    /**
     * insert into table.
     * <p>
     * Attention! qTable(the Metadata Model) is taken either from the annotation @QueryDslModel,
     * either from Dynamic model
     *
     * @param models  data model(dml model) with annotation @QueryDslModel
     * @param <MODEL> data model
     * @return affected rows
     * @see QueryDslModel
     * @see Version
     * @see SoftDelete
     * @see SequanceName
     * @see PKGenerator
     * <p>
     * Instead of this method, you can use  modify( qTable,modelClass ).insert(...)
     * or modify(modelClass).insert(...)
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version is it checks
     * the equality of the number of affected rows and the number of models
     */
    <MODEL extends DMLModel> Long insert(MODEL... models);

    /**
     * insert into table.
     * <p>
     * Attention! qTable(the Metadata Model) is taken either from the annotation @QueryDslModel,
     * either from Dynamic model
     *
     * @param models  data model(dml model) with annotation @QueryDslModel
     * @param <MODEL> data Model type
     * @return affected rows
     * @see QueryDslModel
     * @see Version
     * @see SoftDelete
     * @see SequanceName
     * @see PKGenerator
     * <p>
     * Instead of this method, you can use  modify( qTable,modelClass ).insert(...)
     * or modify(modelClass).insert(...)
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     */
    <MODEL extends DMLModel> Long insert(List<MODEL> models);


    /**
     * update rows in table.
     * <p>
     * Attention! qTable(the Metadata Model) is taken either from the annotation @QueryDslModel,
     * either from Dynamic model
     * Attention! the table should have a Primary Key
     * <p>
     * Example: update set(...)  where id=?
     *
     * @param models  data model(dml model) with annotation @QueryDslModel
     * @param <MODEL> data Model type
     * @return affected rows
     * @see QueryDslModel
     * @see Version
     * <p>
     * Instead of this method, you can use  modify( qTable,modelClass ).insert(...)
     * or modify(modelClass).insert(...)
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     */
    <MODEL extends DMLModel> Long updateById(MODEL... models);


    /**
     * update rows in table.
     * <p>
     * Attention! qTable(the Metadata Model) is taken either from the annotation @QueryDslModel
     * , either from Dynamic model
     * Attention! the table should have a Primary Key
     * <p>
     * Example: update set(...)  where id=?
     *
     * @param models  data model(dml model) with annotation @QueryDslModel
     * @param <MODEL> data Model type
     * @return affected rows
     * @see QueryDslModel
     * @see Version
     * <p>
     * Instead of this method, you can use  modify( qTable,modelClass ).insert(...)
     * or modify(modelClass).insert(...)
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     */
    <MODEL extends DMLModel> Long updateById(List<MODEL> models);

    /**
     * delete rows in table.
     * <p>
     * Attention! qTable(the Metadata Model) is taken either from the annotation @QueryDslModel,
     * either from Dynamic model
     * Attention! the table should have a Primary Key
     * <p>
     * Example: delete from table  where id=?
     *
     * @param models  data model(dml model) with annotation @QueryDslModel
     * @param <MODEL> data Model type
     * @return affected rows
     * @see QueryDslModel
     * @see Version
     * @see CrudBuilder
     * <p>
     * Instead of this method, you can use  modify( qTable,modelClass ).insert(...)
     * or modify(modelClass).insert(...)
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     */
    <MODEL extends DMLModel> Long deleteById(MODEL... models);

    /**
     * delete rows from table.
     * <p>
     * Attention! qTable(the Metadata Model) is taken either from the annotation @QueryDslModel,
     * either from Dynamic model
     * Attention! the table should have a Primary Key
     * <p>
     * Example: delete from table  where id=?
     *
     * @param models  data model(dml model) with annotation @QueryDslModel
     * @param <MODEL> data Model type
     * @return affected rows
     * @see QueryDslModel
     * @see Version
     * @see CrudBuilder
     * <p>
     * Instead of this method, you can use  modify( qTable,modelClass ).insert(...)
     * or modify(modelClass).insert(...)
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     */
    <MODEL extends DMLModel> Long deleteById(List<MODEL> models);

    /**
     * It is used if you want to not remove the data physically.
     *
     * @param models  data model(dml model) with annotation @QueryDslModel
     * @param <MODEL> data Model type
     * @return affected rows
     * <p>
     * Instead of this method, you can use  modify( qTable,modelClass ).insert(...)
     * or modify(modelClass).insert(...)
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     * @see SoftDelete
     * Attention! qTable(the Metadata Model) is taken either from the annotation @QueryDslModel,
     * either from Dynamic model
     * Attention! the table should have a Primary Key
     * Attention! soft deleted rows are not cached
     * <p>
     * Example:  uptade Table set(status =-1) where id=?
     */
    <MODEL extends DMLModel> Long softDeleteById(MODEL... models);

    /**
     * It is used if you want to not remove the data physically.
     *
     * @param models  data model(dml model) with annotation @QueryDslModel
     * @param <MODEL> data Model type
     * @return affected rows
     * <p>
     * Instead of this method, you can use  modify( qTable,modelClass ).insert(...)
     * or modify(modelClass).insert(...)
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     * @see SoftDelete
     * Attention! qTable(the Metadata Model) is taken either from the annotation @QueryDslModel,
     * either from Dynamic model
     * Attention! the table should have a Primary Key
     * Attention! soft deleted rows are not cached
     * <p>
     * Example:  uptade Table set(status =-1) where id=?
     */
    <MODEL extends DMLModel> Long softDeleteById(List<MODEL> models);


    //Cached operations

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
     * IT IS BETTER TO USE OUTSIDE OF A TRANSACTION
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
     * IT IS BETTER TO USE OUTSIDE OF A TRANSACTION
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
     * IT IS BETTER TO USE OUTSIDE OF A TRANSACTION
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


    /**
     * get Context
     *
     * @return context
     */
    QueryContext getContext();
}
