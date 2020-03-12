package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionCacheManager;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.TransactionalEventPublisher;
import com.querydsl.sql.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

public interface OrmQueryFactoryBuilder {

    OrmQueryFactory build();

    OrmQueryFactoryBuilder configuration(Configuration configuration);
    OrmQueryFactoryBuilder debug();
    OrmQueryFactoryBuilder cacheRegion(String cacheName);

    OrmQueryFactoryBuilder transactionManager(
            PlatformTransactionManager transactionManager);

    OrmQueryFactoryBuilder transactionalEventPublisher(
            TransactionalEventPublisher transactionalEventPublisher);

    OrmQueryFactoryBuilder transactionCacheManager(
            TransactionCacheManager transactionCacheManager);
}
