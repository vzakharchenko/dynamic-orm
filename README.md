# Dynamic Orm
[![CircleCI](https://circleci.com/gh/vzakharchenko/dynamic-orm.svg?style=svg)](https://circleci.com/gh/vzakharchenko/dynamic-orm)
[![Coverage Status](https://coveralls.io/repos/github/vzakharchenko/dynamic-orm/badge.svg?branch=master)](https://coveralls.io/github/vzakharchenko/dynamic-orm?branch=master)
[![Maintainability](https://api.codeclimate.com/v1/badges/5c587a6e77be5e8cbef0/maintainability)](https://codeclimate.com/github/vzakharchenko/dynamic-orm/maintainability)

# Features
  - modify database structure on runtime (use Liquibase)
    - create tables
    - add/modify columns
    - add/remove indexes
    - add/remove foreign keys
    - etc...
  - crud operation on dynamic structures
    - insert
    - update
    - delete (soft delete)
    - support optimistic locking (Version column)
  - quering to dynamic structures
    - select
    - subqueries
    - union
    - join
  - cache operation
    - based on spring cache
    - Transaction and External(ehcache, infinispan, redis, etc) cache
    - cache queries based on Primary Key, Column, and Column and Values
    - synchronization cache with crud operations

# dependencies
 - [querydsl](http://www.querydsl.com/) - crud operation(insert, update, delete),  querying (select, union, with)
 - [Spring transaction manager](https://docs.spring.io/spring/docs/4.2.x/spring-framework-reference/html/transaction.html) - transaction manager
 - [Spring cache abstraction](https://docs.spring.io/spring-framework/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/html/cache.html) - Cache abstraction
 - [Liquibase](https://www.liquibase.org/get_started/index.html) - support dynamic structure
 - [Spring  beans](https://docs.spring.io/spring/docs/3.2.x/spring-framework-reference/html/beans.html) - ioc container to connect all parts together
# Installation
##  1. Maven
```xml
 <dependencies>
        <dependency>
            <groupId>com.github.vzakharchenko</groupId>
            <artifactId>dynamic-orm-core</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
```
## 2. Spring Xml or Annotation

```xml
     <!-- transaction Manager -->
    <bean id="transactionManager" class="com.github.vzakharchenko.dynamic.orm.core.transaction.TransactionNameManager">
        <property name="dataSource" ref="dataSource"/>
        <property name="validateExistingTransaction" value="true"/>
    </bean>
    <!-- enable support annotation  -->
    <tx:annotation-driven transaction-manager="transactionManager"/>

    <bean id="sharedTransactionTemplate" class="org.springframework.transaction.support.TransactionTemplate">
        <constructor-arg name="transactionManager" ref="transactionManager"/>
        <property name="isolationLevelName" value="ISOLATION_READ_COMMITTED"/>
        <property name="timeout" value="30000"/>
    </bean>


    <bean name="springOrmQueryFactory" class="com.github.vzakharchenko.dynamic.orm.core.SpringOrmQueryFactory">
        <property name="dataSource" ref="dataSource"/>
        <property name="transactionCacheManager" ref="transaction-cache"/>
        <property name="transactionalEventPublisher" ref="transaction-publisher"/>
        <property name="transactionManager" ref="transactionManager"/>
    </bean>
    
    <!-- The main factory for building queries and data modification -->
    <bean name="ormQueryFactory" factory-bean="springOrmQueryFactory" factory-method="getInstance"/>

    <!-- Dynamic database supporting -->
    <bean class="com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTableFactoryImpl">
        <constructor-arg ref="dataSource"/>
    </bean>

    <!-- Transaction Event Manager-->
    <bean id="transaction-publisher"
          class="com.github.vzakharchenko.dynamic.orm.core.transaction.event.TransactionAwareApplicationEventPublisher"/>
          
              <!-- Datasource - factory for connections to the physical data source -->
    <bean id="dataSource" class="javax.sql.DataSource"
          ... />
    </bean>
    <!-- Spring Cache Abstraction Manager. You can use ehcache, Infinispan, Redis and etc... -->
    <bean id="cacheManager" class="org.springframework.cache.concurrent.ConcurrentMapCacheManager"/>

    <bean id="transaction-cache"
          class="com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionCacheManagerImpl">
        <constructor-arg name="targetCacheManager" ref="cacheManager"/>
    </bean>
```
Or You can use Annotation:
```java
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
```

## 3. Example to Use

```java
 // suspend the current transaction if one exists.
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testQuery() {
        TransactionBuilder transactionManager = ormQueryFactory.transactionManager();
        transactionManager.startTransactionIfNeeded();
        // build schema
        qDynamicTableFactory.buildTable("firstTable")
                .addPrimaryStringKey("Id", 255)
                .addPrimaryKeyGenerator(UUIDPKGenerator.getInstance())
                .createStringColumn("TestStringColumn", 255, false)
                .createDateTimeColumn("modificationTime", true)
                .addVersionColumn("modificationTime")
                .buildNextTable("secondTable")
                .addPrimaryStringKey("Id", 255)
                .addPrimaryKeyGenerator(UUIDPKGenerator.getInstance())
                .createBooleanColumn("isDeleted", false)
                .addSoftDeleteColumn("isDeleted", false, true)
                .createDateTimeColumn("modificationTime", true)
                .addVersionColumn("modificationTime")
                .createStringColumn("linkToFirstTable", 255, false)
                .createStringColumn("uniqValue", 255, false)
                .addIndex("uniqValue", true)
                .addForeignKey("linkToFirstTable", "firstTable")
                .buildSchema();
        transactionManager.commit();

        QDynamicTable firstTable = qDynamicTableFactory.getQDynamicTableByName("firstTable");
        QDynamicTable secondTable = qDynamicTableFactory.getQDynamicTableByName("secondTable");

        // insert data to the first table
        transactionManager.startTransactionIfNeeded();
        DynamicTableModel firstTableModel1 = new DynamicTableModel(firstTable);
        firstTableModel1.addColumnValue("TestStringColumn", "testValue");
        ormQueryFactory.insert(firstTableModel1);

        // insert data to the second table
        DynamicTableModel secondModel1 = new DynamicTableModel(secondTable);
        secondModel1.addColumnValue("uniqValue", "123");
        secondModel1.addColumnValue("linkToFirstTable", firstTableModel1.getValue("Id"));

        DynamicTableModel secondModel2 = new DynamicTableModel(secondTable);
        secondModel2.addColumnValue("uniqValue", "1234");
        secondModel2.addColumnValue("linkToFirstTable", firstTableModel1.getValue("Id"));

        ormQueryFactory.insert(secondModel1, secondModel2);
        transactionManager.commit();


        // add integer column to table1
        transactionManager.startTransactionIfNeeded();
        qDynamicTableFactory.buildTable("firstTable")
                .createNumberColumn("newColumn", Integer.class, null, null, false)
                .buildSchema();
        transactionManager.commit();


        // modify first table
        transactionManager.startTransactionIfNeeded();
        firstTableModel1.addColumnValue("newColumn", 122);
        ormQueryFactory.updateById(firstTableModel1);

        // select one value from firstTable where newColumn == 122
        DynamicTableModel firstTableFromDatabase = ormQueryFactory.select().findOne(ormQueryFactory
                        .buildQuery()
                        .from(firstTable)
                        .where(firstTable.getNumberColumnByName("newColumn").eq(122)),
                firstTable,
                DynamicTableModel.class);
        // get value of TestStringColumn from firstTable
        String testStringColumnValue = firstTableFromDatabase.getValue("TestStringColumn", String.class);
        assertEquals(testStringColumnValue, "testValue");

        // get value  from secondTable and put it to cache
        List<DynamicTableModel> tableModels = ormQueryFactory.selectCache().findAll(secondTable, DynamicTableModel.class);
        assertEquals(tableModels.size(), 2);
        transactionManager.commit();

        // get value from cache
        ormQueryFactory.selectCache().findAll(secondTable, DynamicTableModel.class);

        //soft delete the second row of the second Table
        transactionManager.startTransactionIfNeeded();
        DynamicTableModel dynamicTableModel = tableModels.get(0);
        ormQueryFactory.softDeleteById(dynamicTableModel);
        transactionManager.commit();

        // get new cache records (soft deleted values are not included)
        tableModels = ormQueryFactory.selectCache().findAll(secondTable, DynamicTableModel.class);
        assertEquals(tableModels.size(), 1);
    }
```