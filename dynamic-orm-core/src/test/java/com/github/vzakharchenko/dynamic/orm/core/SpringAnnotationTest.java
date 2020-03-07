package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTableFactory;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTableFactoryImpl;
import com.github.vzakharchenko.dynamic.orm.core.transaction.TransactionNameManager;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionCacheManagerImpl;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.TransactionAwareApplicationEventPublisher;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.TransactionalEventPublisher;
import com.github.vzakharchenko.dynamic.orm.structure.DataSourceHelper;
import com.github.vzakharchenko.dynamic.orm.structure.DbStructureService;
import com.github.vzakharchenko.dynamic.orm.structure.DbStructureServiceImpl;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

import static org.springframework.transaction.TransactionDefinition.ISOLATION_READ_COMMITTED;

@Configuration
@EnableTransactionManagement
@EnableCaching
public class SpringAnnotationTest extends CachingConfigurerSupport {

    private TransactionNameManager transactionNameManager = new TransactionNameManager();
    private DbStructureServiceImpl dbStructureService = new DbStructureServiceImpl();

    TransactionAwareApplicationEventPublisher transactionAwareApplicationEventPublisher =
            new TransactionAwareApplicationEventPublisher();

    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        transactionNameManager.setDataSource(dataSource());
        transactionNameManager.setValidateExistingTransaction(true);
        return transactionNameManager;
    }

    @Bean
    public DataSource dataSource() {
        try {
            return DataSourceHelper.getDataSourceHsqldbCreateSchema("jdbc:hsqldb:mem:DATABASE_MYSQL;sql.mys=true");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Bean
    public TransactionTemplate sharedTransactionTemplate() {
        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(transactionNameManager);
        transactionTemplate.setTimeout(3000);
        transactionTemplate.setIsolationLevel(ISOLATION_READ_COMMITTED);
        return transactionTemplate;
    }

    @Bean
    public DbStructureService staticStructure() {
        dbStructureService.setDataSource(dataSource());
        dbStructureService.setPathToChangeSets("classpath:/changeSets/");
        return dbStructureService;
    }

    @Bean()
    public OrmQueryFactory ormQueryFactory() {
        OrmQueryFactory ormQueryFactory = OrmQueryFactoryInit.create(dataSource())
                .transactionCacheManager(new TransactionCacheManagerImpl(cacheManager()))
                .transactionalEventPublisher(transactionAwareApplicationEventPublisher)
                .transactionManager(transactionNameManager).build();
        return ormQueryFactory;
    }

    @Bean()
    public QDynamicTableFactory dynamicTableFactory() {
        return new QDynamicTableFactoryImpl(ormQueryFactory(), dataSource());
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Bean
    public TransactionalEventPublisher transactionalEventPublisher() {
        return transactionAwareApplicationEventPublisher;
    }
}
