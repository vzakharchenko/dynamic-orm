package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.exception.EmptyBatchException;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContext;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.CrudBuilder;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.CrudBuilderFactory;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionCacheManager;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.TransactionalEventPublisher;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public abstract class CrudOrmQueryFactoryImpl implements OrmQueryFactory, AccessQueryContext {
    private TransactionCacheManager transactionCacheManager;

    private TransactionalEventPublisher transactionalEventPublisher;

    private PlatformTransactionManager transactionManager;

    private final DataSource dataSource;
    private Configuration configuration;
    private QueryContextImpl queryContext;
    private String cacheName = "cache-orm";
    private boolean debugSql;

    protected CrudOrmQueryFactoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected QueryContextImpl getQueryContext() {
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
    public <MODEL extends DMLModel> CrudBuilder<MODEL> modify(RelationalPath<?> qTable,
                                                              Class<MODEL> modelClass) {
        QueryContextImpl queryContextImpl = getQueryContext();
        queryContextImpl.validateModel(qTable, modelClass);
        return CrudBuilderFactory
                .buildCrudBuilder(modelClass, qTable, queryContextImpl);
    }

    @Override
    public CrudBuilder<DynamicTableModel> modify(QDynamicTable dynamicTable) {
        return modify(dynamicTable, DynamicTableModel.class);
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

    protected void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    protected void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    protected void setTransactionalEventPublisher(
            TransactionalEventPublisher transactionalEventPublisher) {
        this.transactionalEventPublisher = transactionalEventPublisher;
    }

    protected void setTransactionCacheManager(
            TransactionCacheManager transactionCacheManager) {
        this.transactionCacheManager = transactionCacheManager;
    }
}
