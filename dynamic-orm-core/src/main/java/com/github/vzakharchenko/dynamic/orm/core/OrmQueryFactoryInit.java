package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionCacheManager;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.TransactionalEventPublisher;
import com.querydsl.sql.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

import javax.sql.DataSource;

public final class OrmQueryFactoryInit implements OrmQueryFactoryBuilder {

    private final OrmQueryFactoryImpl ormQueryFactory;
    private final DataSource dataSource;
    private boolean useDefaultConfiguration = true;
    private boolean useTransactionManager;
    private boolean useTransactionalEventPublisher;
    private boolean useTransactionCacheManager;


    private OrmQueryFactoryInit(DataSource dataSource) {
        this.dataSource = dataSource;
        this.ormQueryFactory = new OrmQueryFactoryImpl(dataSource);
    }

    public static OrmQueryFactoryBuilder create(DataSource dataSource) {
        return new OrmQueryFactoryInit(dataSource);
    }

    @Override
    public OrmQueryFactory build() {
        if (useDefaultConfiguration) {
            configuration(new Configuration(QueryDslJdbcTemplateFactory
                    .getDialect(dataSource, true)));
        }
        return ormQueryFactory;
    }

    @Override
    public OrmQueryFactoryBuilder configuration(Configuration configuration) {
        Assert.notNull(configuration);
        Assert.isTrue(useTransactionManager
                && useTransactionalEventPublisher
                && useTransactionCacheManager, "Orm Query Factory does not initialized");
        ormQueryFactory.setConfiguration(configuration);
        useDefaultConfiguration = false;
        return this;
    }

    @Override
    public OrmQueryFactoryBuilder debug() {
        ormQueryFactory.setDebugSql(true);
        return this;
    }

    @Override
    public OrmQueryFactoryBuilder cacheRegion(String cacheName) {
        ormQueryFactory.setCacheName(cacheName);
        return this;
    }

    @Override
    public OrmQueryFactoryBuilder transactionManager(
            PlatformTransactionManager transactionManager) {
        Assert.notNull(transactionManager);
        ormQueryFactory.setTransactionManager(transactionManager);
        useTransactionManager = true;
        return this;
    }

    @Override
    public OrmQueryFactoryBuilder transactionalEventPublisher(
            TransactionalEventPublisher transactionalEventPublisher) {
        Assert.notNull(transactionalEventPublisher);
        ormQueryFactory.setTransactionalEventPublisher(transactionalEventPublisher);
        useTransactionalEventPublisher = true;
        return this;
    }

    @Override
    public OrmQueryFactoryBuilder transactionCacheManager(
            TransactionCacheManager transactionCacheManager) {
        Assert.notNull(transactionCacheManager);
        ormQueryFactory.setTransactionCacheManager(transactionCacheManager);
        useTransactionCacheManager = true;
        return this;
    }
}
