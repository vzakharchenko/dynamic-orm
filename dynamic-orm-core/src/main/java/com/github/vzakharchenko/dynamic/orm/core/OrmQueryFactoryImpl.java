package com.github.vzakharchenko.dynamic.orm.core;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.PlatformTransactionManager;
import com.github.vzakharchenko.dynamic.orm.core.exception.EmptyBatchException;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContext;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheBuilder;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheBuilderFactory;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.SelectCacheBuilderImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.CrudBuilder;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.CrudBuilderFactory;
import com.github.vzakharchenko.dynamic.orm.core.transaction.TransactionBuilder;
import com.github.vzakharchenko.dynamic.orm.core.transaction.TransactionBuilderImpl;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionCacheManager;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.TransactionalEventPublisher;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class OrmQueryFactoryImpl implements OrmQueryFactory, AccessQueryContext {
    private final TransactionCacheManager transactionCacheManager;

    private final TransactionalEventPublisher transactionalEventPublisher;

    private final PlatformTransactionManager transactionManager;

    private final DataSource dataSource;
    private final Configuration configuration;
    private TransactionBuilder transactionBuilder;
    private QueryContextImpl queryContext;
    private String cacheName = "cache-orm";
    private boolean debugSql = false;


    public OrmQueryFactoryImpl(DataSource dataSource,
                               TransactionCacheManager transactionCacheManager,
                               TransactionalEventPublisher transactionalEventPublisher,
                               PlatformTransactionManager transactionManager) {
        this(dataSource, transactionCacheManager, transactionalEventPublisher,
                transactionManager, true);
    }

    public OrmQueryFactoryImpl(DataSource dataSource,
                               TransactionCacheManager transactionCacheManager,
                               TransactionalEventPublisher transactionalEventPublisher,
                               PlatformTransactionManager transactionManager, boolean quote) {
        this(dataSource, transactionCacheManager, transactionalEventPublisher, transactionManager,
                new Configuration(QueryDslJdbcTemplateFactory
                        .getDialect(dataSource, quote)));
    }

    public OrmQueryFactoryImpl(DataSource dataSource,
                               TransactionCacheManager transactionCacheManager,
                               TransactionalEventPublisher transactionalEventPublisher,
                               PlatformTransactionManager transactionManager,
                               Configuration configuration) {
        this.dataSource = dataSource;
        this.transactionCacheManager = transactionCacheManager;
        this.transactionalEventPublisher = transactionalEventPublisher;
        this.transactionManager = transactionManager;
        this.configuration = configuration;
    }


    private QueryContextImpl getQueryContext() {
        if (queryContext == null) {
            queryContext = new QueryContextImpl(this, dataSource,
                    configuration, transactionCacheManager,
                    transactionalEventPublisher, transactionManager, cacheName);
            if (debugSql) {
                queryContext.enableDebugSql();
            }
        }
        return queryContext.clone();
    }

    @Override
    public QueryContext getContext() {
        return getQueryContext();
    }

    @Override
    public void clearCache() {
        if (queryContext != null) {
            queryContext.clearCache();
        }
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public void setDebugSql(boolean debugSql) {
        this.debugSql = debugSql;
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
    public <MODEL extends DMLModel> CrudBuilder<MODEL> modify(RelationalPath<?> qTable,
                                                              Class<MODEL> modelClass) {
        QueryContextImpl queryContextImpl = getQueryContext();
        queryContextImpl.validateModel(qTable, modelClass);
        return CrudBuilderFactory
                .buildCrudBuilder(modelClass, qTable, queryContextImpl);
    }

    @Override
    public <MODEL extends DMLModel> CrudBuilder<MODEL> modify(Class<MODEL> modelClass) {
        RelationalPath<?> qTableFromModel = getQueryContext().getQModel(modelClass);
        return modify(qTableFromModel, modelClass);
    }

    @Override
    public <MODEL extends DMLModel> Long insert(MODEL... models) {
        return insert(Arrays.asList(models));
    }

    @Override
    public <MODEL extends DMLModel> Long insert(List<MODEL> models) {
        if (CollectionUtils.isEmpty(models)) {
            throw new EmptyBatchException();
        }
        MODEL model = models.get(0);
        PKGenerator<?> pkGenerator = ModelHelper.getPrimaryKeyGeneratorFromModel(model);
        RelationalPath<?> qTableFromModel = ModelHelper.getQTableFromModel(model);
        CrudBuilder<MODEL> modify = modify(qTableFromModel, (Class<MODEL>) model.getClass());
        return modify.primaryKeyGenerator(pkGenerator).insert(models);
    }

    @Override
    public <MODEL extends DMLModel> Long updateById(MODEL... models) {
        return updateById(Arrays.asList(models));
    }

    @Override
    public <MODEL extends DMLModel> Long updateById(List<MODEL> models) {
        if (CollectionUtils.isEmpty(models)) {
            throw new EmptyBatchException();
        }
        RelationalPath<?> qTableFromModel = ModelHelper.getQTableFromModel(models.get(0));
        CrudBuilder<MODEL> modify = modify(qTableFromModel,
                (Class<MODEL>) models.get(0).getClass());
        return modify.updateBuilder().updateModelsById(models);
    }

    @Override
    public <MODEL extends DMLModel> Long deleteById(MODEL... models) {
        return deleteById(Arrays.asList(models));
    }

    @Override
    public <MODEL extends DMLModel> Long deleteById(List<MODEL> models) {
        if (CollectionUtils.isEmpty(models)) {
            throw new EmptyBatchException();
        }
        RelationalPath<?> qTableFromModel = ModelHelper.getQTableFromModel(models.get(0));
        CrudBuilder<MODEL> modify = modify(qTableFromModel,
                (Class<MODEL>) models.get(0).getClass());
        return modify.deleteModelByIds(models);
    }

    @Override
    public <MODEL extends DMLModel> Long softDeleteById(MODEL... models) {
        return softDeleteById(Arrays.asList(models));
    }

    @Override
    public <MODEL extends DMLModel> Long softDeleteById(List<MODEL> models) {
        if (CollectionUtils.isEmpty(models)) {
            throw new EmptyBatchException();
        }
        RelationalPath<?> qTableFromModel = ModelHelper.getQTableFromModel(models.get(0));
        CrudBuilder<MODEL> modify = modify(qTableFromModel,
                (Class<MODEL>) models.get(0).getClass());
        return modify.softDeleteModelByIds(models);
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
