package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionCacheManager;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.TransactionalEventPublisher;
import com.querydsl.sql.Configuration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


public class SpringOrmQueryFactory implements ISpringOrmQueryFactory, InitializingBean {

    private OrmQueryFactory ormQueryFactory;

    private DataSource dataSource;

    private TransactionCacheManager transactionCacheManager;

    private TransactionalEventPublisher transactionalEventPublisher;

    private PlatformTransactionManager transactionManager;

    private Configuration configuration;


    @Override
    public OrmQueryFactory getInstance() {
        return ormQueryFactory;
    }

    @Override
    public void afterPropertiesSet() {
        OrmQueryFactoryBuilder queryFactoryBuilder = OrmQueryFactoryInit.create(dataSource);
        if (configuration != null) {
            queryFactoryBuilder.configuration(configuration);
        }
        ormQueryFactory = queryFactoryBuilder
                .transactionalEventPublisher(transactionalEventPublisher)
                .transactionManager(transactionManager)
                .transactionCacheManager(transactionCacheManager).build();
    }

    public void setTransactionCacheManager(TransactionCacheManager transactionCacheManager) {
        this.transactionCacheManager = transactionCacheManager;
    }

    public void setTransactionalEventPublisher(
            TransactionalEventPublisher transactionalEventPublisher) {
        this.transactionalEventPublisher = transactionalEventPublisher;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
