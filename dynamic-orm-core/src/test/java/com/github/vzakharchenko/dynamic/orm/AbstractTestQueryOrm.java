package com.github.vzakharchenko.dynamic.orm;

import com.github.vzakharchenko.dynamic.orm.core.AccessQueryContext;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.AccessDynamicContext;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTableFactory;
import com.github.vzakharchenko.dynamic.orm.dataSource.DBFacade;
import com.github.vzakharchenko.dynamic.orm.structure.DbStructureService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 *
 */
@Commit
public abstract class AbstractTestQueryOrm
        extends AbstractTransactionalTestNGSpringContextTests {


    public static final String UTF_8 = "UTF-8";
    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected CacheManager cacheManager;
//
//    @Autowired
//    protected TransactionCacheManager transactionCacheManager;

    @Autowired
    protected TransactionTemplate transactionTemplate;

    @Autowired
    protected OrmQueryFactory ormQueryFactory;

    @Autowired
    protected AccessQueryContext accessQueryContext;

    @Autowired(required = false)
    protected AccessDynamicContext accessDynamicContext;

    @Autowired(required = false)
    protected QDynamicTableFactory qDynamicTableFactory;

    @BeforeMethod
    public void loadSchema() {
        dropSchema();


        Collection<DbStructureService> dbStructureServices =
                getBeans(DbStructureService.class);
        for (DbStructureService dbStructureService : dbStructureServices) {
            String sql = dbStructureService.generateSql();
            System.out.println(sql);
            Resource resource = new ByteArrayResource(sql
                    .getBytes(StandardCharsets.UTF_8), UTF_8);
            new ResourceDatabasePopulator(
                    false, false, UTF_8, resource)
                    .execute(dataSource);

        }

        for (DbStructureService dbStructureService : dbStructureServices) {
            dbStructureService.unlock();
        }
    }

    @AfterMethod
    public void dropSchema() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            cache.clear();
        }
        accessQueryContext.clearCache();
        if (accessDynamicContext != null) {
            accessDynamicContext.clearCache();
        }

        Collection<DbStructureService> dbStructureServices =
                getBeans(DbStructureService.class);

        for (DbStructureService dbStructureService : dbStructureServices) {
            dbStructureService.clear();
        }
        checkConnectionLeaks();

    }


    public void checkConnectionLeaks() {
        if (DBFacade.getDBConnectionsInUse() > 0) {
            if (!TransactionSynchronizationManager.isSynchronizationActive()) {
                DBFacade.printUsedConnections();
                throw new IllegalStateException(
                        "Found DB Connection Leak (" +
                                DBFacade.getDBConnectionsInUse() + ")"
                );
            } else {
                if (DBFacade.getDBConnectionsInUse() > 1) {
                    DBFacade.printUsedConnections();
                    throw new IllegalStateException(
                            "Found DB Connection Leak{" +
                                    DBFacade.getDBConnectionsInUse() + ")");
                }
            }
        }
    }

    public Object getBean(String beanName) {
        if (applicationContext == null) {
            throw new IllegalArgumentException("applicationContext is not found");
        } else {
            return applicationContext.getBean(beanName);
        }
    }

    public <T> T getBean(Class<T> requiredType) throws BeansException {
        if (applicationContext == null) {
            throw new IllegalArgumentException("applicationContext is not found");
        } else {
            return applicationContext.getBean(requiredType);
        }
    }

    public <T> Collection<T> getBeans(Class<T> requiredType) throws BeansException {
        HashSet beans = new HashSet();
        if (applicationContext == null) {
            throw new IllegalArgumentException("applicationContext is not found");
        } else {
            Map beanMap = applicationContext.getBeansOfType(requiredType);
            if (MapUtils.isNotEmpty(beanMap)) {
                beans.addAll(beanMap.values());
            }

            return beans;
        }
    }

}
