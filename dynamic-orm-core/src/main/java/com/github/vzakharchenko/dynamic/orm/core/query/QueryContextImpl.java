package com.github.vzakharchenko.dynamic.orm.core.query;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheContext;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDelete;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionCacheManager;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.TransactionalEventPublisher;
import com.querydsl.core.types.Path;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLTemplates;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public class QueryContextImpl implements Cloneable, QueryContext {
    private final TransactionCacheManager transactionCacheManager;
    private final TransactionalEventPublisher transactionalEventPublisher;
    private final PlatformTransactionManager transactionManager;
    private final DefaultTransactionDefinition transactionDefinition;
    private final DataSource dataSource;
    private final Configuration configuration;
    private final CacheContext cacheContext = new CacheContext();
    private final ModelMapper modelMapper = new ModelMapper();
    private final String cacheName;
    private final OrmQueryFactory ormQueryFactory;
    private boolean debugSql;

    // CHECKSTYLE:OFF
    public QueryContextImpl(OrmQueryFactory ormQueryFactory, DataSource dataSource,
                            Configuration configuration,
                            TransactionCacheManager transactionCacheManager,
                            TransactionalEventPublisher transactionalEventPublisher,
                            PlatformTransactionManager transactionManager,
                            String cacheName) {
        Assert.notNull(ormQueryFactory, "ormQuery is null");
        Assert.notNull(dataSource, "datasource is null");
        Assert.notNull(configuration, "configuration is null");
        Assert.notNull(transactionCacheManager, "transactionCacheManager is null");
        Assert.notNull(transactionalEventPublisher,
                "transactionalEventPublisher is null");
        Assert.notNull(transactionManager, "transactionManager is null");
        Assert.hasText(cacheName, "cacheName is empty");
        this.dataSource = dataSource;
        this.configuration = configuration;
        this.transactionCacheManager = transactionCacheManager;
        this.transactionalEventPublisher = transactionalEventPublisher;
        this.cacheName = cacheName;
        this.ormQueryFactory = ormQueryFactory;
        this.transactionManager = transactionManager;
        transactionDefinition = new TransactionTemplate(transactionManager);
    }
// CHECKSTYLE:ON

    public DataSource getDataSource() {
        return dataSource;
    }

    public SQLTemplates getDialect() {
        return configuration.getTemplates();
    }

    @Override
    public TransactionalCache getTransactionCache() {
        return transactionCacheManager.getTransactionalCache(cacheName);
    }


    public TransactionalEventPublisher getTransactionalEventPublisher() {
        return transactionalEventPublisher;
    }


    public CacheContext getCacheContext() {
        return cacheContext;
    }

    public OrmQueryFactory getOrmQueryFactory() {
        return ormQueryFactory;
    }

    public DefaultTransactionDefinition
    getTransactionDefinition() {
        return transactionDefinition;
    }

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public QueryContextImpl clone() {
        try {
            return (QueryContextImpl) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public void clearCache() {
        getTransactionCache().clearAll();
        cacheContext.clear();
        modelMapper.clear();
    }

    public void validateModel(RelationalPath<?> qTable, Class<? extends DMLModel> modelClass) {
        modelMapper.validateModel(qTable, modelClass);
    }

    public Path<?> getVersionColumn(RelationalPath<?> qTable,
                                    Class<? extends DMLModel> modelClass) {
        return modelMapper.getVersionColumn(qTable, modelClass);
    }

    public SoftDelete<?> getSoftDeleteColumn(RelationalPath<?> qTable,
                                             Class<? extends DMLModel> modelClass) {
        return modelMapper.getSoftDeleteColumn(qTable, modelClass);
    }

    public void validateVersionColumn(Path<?> versionColumn) {
        modelMapper.validateVersionColumn(versionColumn);
    }

    @Override
    public RelationalPath<?> getQModel(Class<? extends DMLModel> modelClass) {
        return modelMapper.getQModel(modelClass);
    }

    public boolean isDebugSql() {
        return debugSql;
    }

    public void enableDebugSql() {
        debugSql = true;
    }


}
