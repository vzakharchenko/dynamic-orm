package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheBuilder;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheBuilderFactory;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.SelectCacheBuilderImpl;
import com.github.vzakharchenko.dynamic.orm.core.transaction.TransactionBuilder;
import com.github.vzakharchenko.dynamic.orm.core.transaction.TransactionBuilderImpl;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;

import javax.sql.DataSource;

/**
 *
 */
public class OrmQueryFactoryImpl extends CrudOrmQueryFactoryImpl {

    private TransactionBuilder transactionBuilder;

    protected OrmQueryFactoryImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public TransactionBuilder transactionManager() {
        if (transactionBuilder == null) {
            transactionBuilder = new TransactionBuilderImpl(getQueryContext());
        }
        return transactionBuilder;
    }

    @Override
    public SelectBuilder select() {

        QueryContextImpl queryContextImpl = getQueryContext();
        return new SelectBuilderImpl(queryContextImpl);
    }

    @Override
    public SQLQuery<Object> buildQuery() {
        return new SQLQuery(getQueryContext()
                .getDialect());
    }

    @Override
    public SelectCacheBuilder selectCache() {
        QueryContextImpl queryContextImpl = getQueryContext();
        if (queryContextImpl == null) {
            throw new IllegalStateException("query Context is null");
        }
        return new SelectCacheBuilderImpl(queryContextImpl);
    }

    @Override
    public <MODEL extends DMLModel> CacheBuilder<MODEL> modelCacheBuilder(RelationalPath<?> qTable,
                                                                          Class<MODEL> modelClass) {
        return CacheBuilderFactory.build(modelClass, qTable, getQueryContext());
    }

    @Override
    public <MODEL extends DMLModel> CacheBuilder<MODEL> modelCacheBuilder(Class<MODEL> modelClass) {
        RelationalPath<?> qTableFromModel = getQueryContext().getQModel(modelClass);
        return modelCacheBuilder(qTableFromModel, modelClass);
    }
}
