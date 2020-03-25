# Dynamic Orm
[![CircleCI](https://circleci.com/gh/vzakharchenko/dynamic-orm.svg?style=svg)](https://circleci.com/gh/vzakharchenko/dynamic-orm)
[![Coverage Status](https://coveralls.io/repos/github/vzakharchenko/dynamic-orm/badge.svg?branch=master)](https://coveralls.io/github/vzakharchenko/dynamic-orm?branch=master)
[![Maintainability](https://api.codeclimate.com/v1/badges/5c587a6e77be5e8cbef0/maintainability)](https://codeclimate.com/github/vzakharchenko/dynamic-orm/maintainability)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.vzakharchenko/dynamic-orm/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.vzakharchenko/dynamic-orm)
[![BCH compliance](https://bettercodehub.com/edge/badge/vzakharchenko/dynamic-orm?branch=master)](https://bettercodehub.com/)  
[![Known Vulnerabilities](https://snyk.io/test/github/vzakharchenko/dynamic-orm/badge.svg)](https://snyk.io/test/github/vzakharchenko/dynamic-orm)

# supported database
 - Oracle
 - Postgres
 - MySQL
 - MariaDB
 - Hsql
 - H2
 - Derby
 - Firebird
 - SQLite
 - MSSQL
 - DB2
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
    - CTE
    - subqueries
    - union
    - join
  - cache operation
    - based on spring cache
    - Transaction and External(ehcache, infinispan, redis, etc) cache
    - cache queries based on Primary Key, Column, and Column and Values
    - synchronization cache with crud operations
  - support clustering( if use distributed cache)
  - support create Sql sequence on runtime
  - support create/update View on runtime
  - save/load dynamic structure
  - support Composite Primary key

# dependencies
 - [querydsl](http://www.querydsl.com/) - crud operation(insert, update, delete),  querying (select, union, CTE)
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
            <version>1.3.0</version>
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
                 OrmQueryFactoryInit.create(dataSource())
                .transactionCacheManager(new TransactionCacheManagerImpl(cacheManager()))
                .transactionalEventPublisher(transactionAwareApplicationEventPublisher) // event publisher
                .debug() // show all sql queries in logger
                .cacheRegion("cache-orm") // cache region
                .transactionManager(transactionNameManager)
                .build();
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
 - autowire factories
```java
    @Autowired
    private OrmQueryFactory ormQueryFactory;

    @Autowired
    private QDynamicTableFactory qDynamicTableFactory;
```
 - add @Transactional annotation, or use transaction Manager
```java
    @Transactional()
    public void testQuery() {
     ...
    }
```
 or
```java
    public void testQuery() {
               TransactionBuilder transactionManager = ormQueryFactory.transactionManager();
        transactionManager.startTransactionIfNeeded();
        ...
        transactionManager.commit();
    }
```
 - create schema example
```java
@Transactional()
public void testQuery() {
            qDynamicTableFactory.buildTables("firstTable")
                .columns().addStringColumn("Id")
                .size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("TestStringColumn").size(255).createColumn()
                .addDateColumn("modificationTime").createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(UUIDPKGenerator.getInstance())
                .endPrimaryKey()
                .addVersionColumn("modificationTime")
                .endBuildTables().buildSchema();
}
```
 - load dynamic structure from current connection
```java
        qDynamicTableFactory.loadCurrentSchema();
```

 - save dynamic structure to file
```java
        File file = new File(".", "testSchema.json");
        qDynamicTableFactory.saveSchema(SchemaUtils.getFileSaver(file));
```
 - load dynamic structure from file
```java
        File file = new File(".", "testSchema.json");
        qDynamicTableFactory.loadSchema(SchemaUtils.getFileLoader(file));
```
 - get  table metadata
```java
 QDynamicTable firstTable = qDynamicTableFactory.getQDynamicTableByName("firstTable");
```
   - insert operation
```java
        DynamicTableModel firstTableModel1 = new DynamicTableModel(firstTable);
        firstTableModel1.addColumnValue("TestStringColumn", "testValue");
        ormQueryFactory.insert(firstTableModel1);
```
   - modify table metadata
```java
  // add integer column to table
        qDynamicTableFactory.buildTables("firstTable")
                .columns().addNumberColumn("newColumn", Integer.class).createColumn().endColumns()
                .endBuildTables().buildSchema();
```
 - add custom column type
```java
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .columns()
                    .addCustomColumn("customColumn")
                    .column(Expressions::stringPath)
                    .jdbcType(new NVarcharType())
                    .createColumn()
                .endColumns()
                .endBuildTables().buildSchema();
```
 - update operation
```java
        firstTableModel1.addColumnValue("newColumn", 122);
        ormQueryFactory.updateById(firstTableModel1);
```
   - fetch data
```java
        DynamicTableModel firstTableFromDatabase = ormQueryFactory.select().findOne(ormQueryFactory
                        .buildQuery()
                        .from(firstTable)
                        .where(firstTable.getNumberColumnByName("newColumn").eq(122)),
                firstTable,
                DynamicTableModel.class);
```
   - fetch data with Wildcard
```java
        StringPath testColumn = dynamicTable.getStringColumnByName("TestColumn");

        // fetch all data from all table
        // if you want cache the result you can use selectCache() instead of select() 
        List<RawModel> rawModels = ormQueryFactory.select().rawSelect(
                ormQueryFactory.buildQuery().from(dynamicTable)
                        .orderBy(testColumn.asc())).findAll(Wildcard.all);
        
        RawModel rawModel = rawModels.get(0);
        Object columnValue1 = rawModel.getValueByPosition(0);
        Object columnValue2 = rawModel.getValueByPosition(1);
        Object columnValue3 = rawModel.getValueByPosition(2);
```

   - fetch data and put result to the cache. Cache record will be evicted if any related table is modified (insert/update/delete operartion)
```java
        DynamicTableModel firstTableFromDatabase = ormQueryFactory.selectCache().findOne(ormQueryFactory
                        .buildQuery()
                        .from(firstTable)
                        .where(firstTable.getNumberColumnByName("newColumn").eq(122)),
                firstTable,
                DynamicTableModel.class);
```
how it works:
```java
        // fetch data and put result to cache
        DynamicTableModel firstTableFromDatabase = ormQueryFactory.selectCache().findOne(ormQueryFactory
                        .buildQuery()
                        .from(firstTable)
                        .where(firstTable.getNumberColumnByName("newColumn").eq(122)),
                firstTable,
                DynamicTableModel.class);

        // fetch result from the cache
        firstTableFromDatabase = ormQueryFactory.selectCache().findOne(ormQueryFactory
                        .buildQuery()
                        .from(firstTable)
                        .where(firstTable.getNumberColumnByName("newColumn").eq(122)),
                firstTable,
                DynamicTableModel.class);
        
        // any "firstTable" modification will evict the query result from the cache 
        ormQueryFactory.insert(new DynamicTableModel(firstTable));
        
        // fetch data and put result to the cache
        DynamicTableModel firstTableFromDatabase = ormQueryFactory.selectCache().findOne(ormQueryFactory
                        .buildQuery()
                        .from(firstTable)
                        .where(firstTable.getNumberColumnByName("newColumn").eq(122)),
                firstTable,
                DynamicTableModel.class);
```
 - limit and offset
```java
    ormQueryFactory.selectCache().findOne(ormQueryFactory
                        .buildQuery()
                        .from(firstTable).limit(3).offset(3)
                        .where(firstTable.getNumberColumnByName("newColumn").eq(122)),
                firstTable,
                DynamicTableModel.class);
```

  - get column value from model
```java
               String testStringColumnValue = firstTableFromDatabase.getValue("TestStringColumn", String.class);
```
  - join queries
```java
        // fetch data (if you want cache the result you can use selectCache() instead of select() )
        List<RawModel> rawModels = ormQueryFactory.select().rawSelect(
                ormQueryFactory.buildQuery().from(firstTable)
                        .innerJoin(secondTable).on(
                        secondTable.getStringColumnByName("linkToFirstTable").eq(
                                firstTable.getStringColumnByName("Id")))
                        .where(secondTable.getBooleanColumnByName("isDeleted").eq(false)))
                .findAll(ArrayUtils.addAll(firstTable.all(), secondTable.all()));
        RawModel rawModel = rawModels.get(0);
        DynamicTableModel firstModelFromJoin = rawModel.getDynamicModel(firstTable);
        DynamicTableModel secondModelFromJoin = rawModel.getDynamicModel(secondTable);
```

[Full Example](dynamic-orm-core/src/test/java/com/github/vzakharchenko/dynamic/orm/core/QueryAnnotationTest.java#L20-L126):

```java

    @Autowired
    private OrmQueryFactory ormQueryFactory;

    @Autowired
    private QDynamicTableFactory qDynamicTableFactory;
    
 // suspend the current transaction if one exists.
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testQuery() {
         TransactionBuilder transactionManager = ormQueryFactory.transactionManager();
        transactionManager.startTransactionIfNeeded();
        // build schema
        qDynamicTableFactory.buildTables("firstTable")
                .columns().addStringColumn("Id")
                .size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("TestStringColumn").size(255).createColumn()
                .addDateColumn("modificationTime").createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(UUIDPKGenerator.getInstance())
                .endPrimaryKey()
                .addVersionColumn("modificationTime")
                .buildNextTable("secondTable")
                .columns().addStringColumn("Id")
                .size(255).useAsPrimaryKey().createColumn()
                .addBooleanColumn("isDeleted").notNull().createColumn()
                .addDateTimeColumn("modificationTime").notNull().createColumn()
                .addStringColumn("linkToFirstTable").size(255).createColumn()
                .addStringColumn("uniqValue").size(255).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(UUIDPKGenerator.getInstance()).endPrimaryKey()
                .addSoftDeleteColumn("isDeleted", true, false)
                .addVersionColumn("modificationTime")
                .index("uniqValue").addUniqueIndex()
                .foreignKey("linkToFirstTable").addForeignKey(("firstTable")
                .endBuildTables().buildSchema();
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
        qDynamicTableFactory.buildTables("firstTable")
                .columns().addNumberColumn("newColumn", Integer.class).createColumn().endColumns()
                .endBuildTables().buildSchema();
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
        List<DynamicTableModel> tableModels = ormQueryFactory.selectCache().findAll(secondTable);
        assertEquals(tableModels.size(), 2);
        transactionManager.commit();

        // get value from cache
        ormQueryFactory.selectCache().findAll(secondTable);

        //soft delete the second row of the second Table
        transactionManager.startTransactionIfNeeded();
        ormQueryFactory.softDeleteById(secondModel2);
        transactionManager.commit();

        // get new cache records (soft deleted values are not included)
        tableModels = ormQueryFactory.selectCache().findAll(secondTable);
        assertEquals(tableModels.size(), 1);

        // fetch all data from all table
        // if you want cache the result you can use selectCache() instead of select() 
        List<RawModel> rawModels = ormQueryFactory.select().rawSelect(
                ormQueryFactory.buildQuery().from(firstTable)
                        .innerJoin(secondTable).on(
                        secondTable.getStringColumnByName("linkToFirstTable").eq(
                                firstTable.getStringColumnByName("Id")))
                        .where(secondTable.getBooleanColumnByName("isDeleted").eq(false)))
                .findAll(ArrayUtils.addAll(firstTable.all(), secondTable.all()));

        assertEquals(rawModels.size(), 1);
        RawModel rawModel = rawModels.get(0);
        DynamicTableModel firstModelFromJoin = rawModel.getDynamicModel(firstTable);
        DynamicTableModel secondModelFromJoin = rawModel.getDynamicModel(secondTable);
        assertEquals(firstModelFromJoin.getValue("Id"), firstTableFromDatabase.getValue("Id"));
        assertEquals(secondModelFromJoin.getValue("Id"), secondModel1.getValue("Id"));
    }
```
# SQL INDEX
## create index on runtime
```java
        qDynamicTableFactory.buildTables("firstTable")
                .columns().addStringColumn("Id")
                .size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("column1").size(255).createColumn()
                .addStringColumn("column2").size(255).createColumn()
                .endColumns()
                .index("column1","column2").addIndex()
                .endBuildTables().buildSchema();
```

## create unique index on runtime
```java
        qDynamicTableFactory.buildTables("firstTable")
                .columns().addStringColumn("Id")
                .size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("column1").size(255).createColumn()
                .addStringColumn("column2").size(255).createColumn()
                .endColumns()
                .index("column1","column2").clustered().addUniqueIndex()
                .endBuildTables().buildSchema();
```

## drop index on runtime
```java
  // create schema
        qDynamicTableFactory.buildTables("table1").columns()
                .addStringColumn("Id1").size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("column1").size(255).createColumn()
                .addStringColumn("column2").size(255).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .index("column1", "column2").addIndex()
                .endBuildTables().buildSchema();

        // drop Index
        qDynamicTableFactory.buildTables("table1")
                .index("column1", "column2").drop()
                .endBuildTables().buildSchema();
        
```
# Foreign Key
## create foreign key on runtime
```java
qDynamicTableFactory.buildTables("table1").columns()
                .addStringColumn("Id1").size(255).useAsPrimaryKey().createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .buildNextTable("table2")
                .columns()
                .addStringColumn("Id2").size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("Id1").size(255).notNull().createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .foreignKey("Id1").addForeignKey("table1")
                .endBuildTables().buildSchema();
```
## drop foreign key on runtime
 ```java
    // create table1 and table2
  qDynamicTableFactory.buildTables("table1").columns()
                .addStringColumn("Id1").size(255).useAsPrimaryKey().createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .buildNextTable("table2")
                .columns()
                .addStringColumn("Id2").size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("Id1").size(255).notNull().createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .foreignKey("Id1").addForeignKey("table1")
                .endBuildTables().buildSchema();
        QDynamicTable table1 = qDynamicTableFactory.getQDynamicTableByName("table1");
        QDynamicTable table2 = qDynamicTableFactory.getQDynamicTableByName("table2");

        // insert Table 1
        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(table1);
        ormQueryFactory.insert(dynamicTableModel1);

        // insert to table 2 with foreign Key
        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(table2);
        dynamicTableModel2.addColumnValue("id1", dynamicTableModel1.getValue("Id1"));
        ormQueryFactory.insert(dynamicTableModel2);

        // drop foreign Key

        qDynamicTableFactory.buildTables("table2")
                .foreignKey("Id1").drop()
                .endBuildTables().buildSchema();


        // insert to table 2 with foreign Key
        DynamicTableModel dynamicTableModel2WithoutForeign = new DynamicTableModel(table2);
        dynamicTableModel2WithoutForeign.addColumnValue("id1", "Not Foreign Key Value");
        ormQueryFactory.insert(dynamicTableModel2WithoutForeign);
```
# Static Tables(not Dynamic)
## - QueryDsl Models (Table Metadata)
```java
@Generated("com.querydsl.query.sql.codegen.MetaDataSerializer")
public class QTestTableVersionAnnotation extends RelationalPathBase<QTestTableVersionAnnotation> {

    public static final QTestTableVersionAnnotation qTestTableVersionAnnotation = new QTestTableVersionAnnotation("TEST_TABLE_VERSION_ANNOTATION");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> version = createNumber("version", Integer.class);

    public final PrimaryKey<QTestTableVersionAnnotation> idPk = createPrimaryKey(id);

    public QTestTableVersionAnnotation(String variable) {
        super(QTestTableVersionAnnotation.class, forVariable(variable), "", "TEST_TABLE_VERSION_ANNOTATION");
        addMetadata();
    }

    public QTestTableVersionAnnotation(String variable, String schema, String table) {
        super(QTestTableVersionAnnotation.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QTestTableVersionAnnotation(Path<? extends QTestTableVersionAnnotation> path) {
        super(path.getType(), path.getMetadata(), "", "TEST_TABLE_VERSION_ANNOTATION");
        addMetadata();
    }

    public QTestTableVersionAnnotation(PathMetadata metadata) {
        super(QTestTableVersionAnnotation.class, metadata, "", "TEST_TABLE_VERSION_ANNOTATION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(38).notNull());
        addMetadata(version, ColumnMetadata.named("VERSION").withIndex(2).ofType(Types.INTEGER).withSize(38).notNull());
    }

}
```
## - Static POJO Model
```java
@QueryDslModel(qTableClass = QTestTableVersionAnnotation.class, tableName = "TEST_TABLE_VERSION_ANNOTATION", primaryKeyGenerator = PrimaryKeyGenerators.SEQUENCE)
@SequanceName("TEST_SEQUENCE")
public class TestTableVersionAnnotation implements DMLModel {

    private Integer id;
    @Version
    private Integer version;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
```
Annotations:
- **@QueryDslModel** - related QueryDsl model.
  -  qTableClass - queryDsl class
  -  tableName - Table name
  -  primaryKeyGenerator - Primary Key generator
     -  DEFAULT - does not use PK generator
     -  INTEGER - integer values
     -  LONG - long values
     -  UUID - Universally Unique Identifier values (UUID.randomUUID().toString())
     -  SEQUENCE - Sql Sequence (if database support)
- **@SequanceName** - Sequance annotation
- **@Version** - mark field as Optimistic locking. (Supports only TimeStamp and numeric column)

## Example of Usage

 - insert
```java
        TestTableVersionAnnotation testTableVersion = new TestTableVersionAnnotation();
        ormQueryFactory.insert(testTableVersion);
```
 - update
```java
        testTableVersion.setSomeColumn("testColumn")
        ormQueryFactory.updateById(testTableVersion);
```
 - select Version column and put result to cache
```java
        Integer version = ormQueryFactory.selectCache().findOne(
                ormQueryFactory.buildQuery()
                        .from(QTestTableVersionAnnotation.qTestTableVersionAnnotation)
                        .where(QTestTableVersionAnnotation.qTestTableVersionAnnotation.id.eq(testTableVersion.getId()))
                , QTestTableVersionAnnotation.qTestTableVersionAnnotation.version);
```
 - join with dynamic table
```java
        TestTableVersionAnnotation staticTable = new TestTableVersionAnnotation();
        ormQueryFactory.insert(staticTable);
        // build dynamic Table with foreign Key to Static Table
        qDynamicTableFactory.buildTables("relatedTable")
                .columns().addStringColumn("Id").size(255).useAsPrimaryKey().createColumn()
                .addNumberColumn("StaticId", Integer.class).createColumn()
                .addDateTimeColumn("modificationTime").notNull().createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(UUIDPKGenerator.getInstance()).endPrimaryKey()
                .addVersionColumn("modificationTime")
                .foreignKey("StaticId").addForeignKey((QTestTableVersionAnnotation.qTestTableVersionAnnotation,  QTestTableVersionAnnotation.qTestTableVersionAnnotation.id)
                .endBuildTables().buildSchema();

        // fetch dynamic table metadata
        QDynamicTable relatedTable = qDynamicTableFactory.getQDynamicTableByName("relatedTable");

        // insert to dynamic table
        DynamicTableModel relatedTableData = new DynamicTableModel(relatedTable);
        relatedTableData.addColumnValue("StaticId", staticTable.getId());

        ormQueryFactory.insert(relatedTableData);

        // fetch with join
         // if you want cache the result you can use selectCache() instead of select()
        DynamicTableModel tableModel = ormQueryFactory
                .select()
                .findOne(ormQueryFactory
                                .buildQuery().from(relatedTable)
                                .innerJoin(QTestTableVersionAnnotation.qTestTableVersionAnnotation)
                                .on(relatedTable
                                        .getNumberColumnByName("StaticId", Integer.class)
                                        .eq(QTestTableVersionAnnotation
                                                .qTestTableVersionAnnotation.id))
                                .where(QTestTableVersionAnnotation
                                        .qTestTableVersionAnnotation.id.eq(staticTable.getId())),
                        relatedTable);
        assertNotNull(tableModel);
        assertEquals(tableModel.getValue("Id"), relatedTableData.getValue("Id"));
```

 - drop column
```java
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns()
                    .dropColumns("TestColumn")
                .endColumns()
                .endBuildTables().buildSchema();
```
 - modify column
```java
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns()
                    .modifyColumn()
                    .size("TestColumn", 1)
                    .finish()
                .endColumns()
                .endBuildTables()
                .buildSchema();
```
 - drop table or View
```java
        qDynamicTableFactory
                .dropTableOrView("TABLE_OR_VIEW_NAME").buildSchema();
```
 - drop Sequence
```java
        qDynamicTableFactory
                .dropSequence("sequence_name").buildSchema();
```
## Generate QueryDslModel
[Example](dynamic-orm-examples/example-test-qmodels/pom.xml)

pom.xml
```xml
            <plugin>
                <groupId>com.querydsl</groupId>
                <artifactId>querydsl-maven-plugin</artifactId>
                <version>${querydsl}</version>

                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>export</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <jdbcDriver>${driver}</jdbcDriver>
                    <beanPrefix>Q</beanPrefix>
                    <packageName>${QmodelPackage}</packageName>
                    <targetFolder>${targetFolder}</targetFolder>
                    <jdbcUrl>${jdbcUrl}</jdbcUrl>
                    <jdbcPassword>${jdbcPassword}</jdbcPassword>
                    <jdbcUser>${jdbcUser}</jdbcUser>
                    <sourceFolder />
                </configuration>
            </plugin>
```

## Generate Static POJO Models
[Example](dynamic-orm-examples/example-test-models/pom.xml)

pom.xml

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>com.github.vzakharchenko</groupId>
                <artifactId>dynamic-orm-plugin</artifactId>
                <version>1.3.0</version>
                <configuration>
                    <targetQModelFolder>${targetFolder}</targetQModelFolder>
                    <modelPackage>${ModelPackage}</modelPackage>
                    <qmodelPackage>queryDsl package name</qmodelPackage>
                </configuration>
                <executions>
                    <execution>
                        <phase>process-sources</phase>
                        <goals>
                            <goal>modelGenerator</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

# Audit database changes
[Example: Logging Audit](dynamic-orm-examples/example-test-ehcache/src/main/java/orm/query/examples/ehcache/LogAudit.java)
```java
@Component
public class LogAudit implements ApplicationListener<CacheEvent> {
    @Override
    public void onApplicationEvent(CacheEvent cacheEvent) {
        switch (cacheEvent.cacheEventType()) {
            case INSERT: {
                for (Serializable pk : cacheEvent.getListIds()) {
                    System.out.println("insert table " + cacheEvent.getQTable().getTableName()
                            + " primarykey = " + pk);
                    DiffColumnModel diffModel = cacheEvent.getDiffModel(pk);
                    for (Map.Entry<Path<?>, DiffColumn<?>> entry : diffModel.getDiffModels().entrySet()) {
                        System.out.println(" --- column " + ModelHelper.getColumnRealName(entry.getKey())
                                + " set " + entry.getValue().getNewValue());
                    }
                }
                break;
            }
            case UPDATE: {
                for (Serializable pk : cacheEvent.getListIds()) {
                    System.out.println("update table " + cacheEvent.getQTable().getTableName());
                    DiffColumnModel diffModel = cacheEvent.getDiffModel(pk);
                    for (Map.Entry<Path<?>, DiffColumn<?>> entry : diffModel.getOnlyChangedColumns().entrySet()) {
                        System.out.println(" --- column " + ModelHelper.getColumnRealName(entry.getKey())
                                + " set " + entry.getValue().getNewValue()
                                + " old value "
                                + entry.getValue().getOldValue());
                    }
                }

                break;
            }
            case SOFT_DELETE:
            case DELETE: {
                System.out.println("delete into table " + cacheEvent.getQTable().getTableName() + " ids = " + ToStringBuilder.reflectionToString(cacheEvent.getListIds(), ToStringStyle.JSON_STYLE));
                break;
            }
            case BATCH: {
                List<? extends CacheEvent> transactionHistory = cacheEvent.getTransactionHistory();
                for (CacheEvent event : transactionHistory) {
                    onApplicationEvent(event);
                }
                break;
            }
            default: {
                throw new IllegalStateException(cacheEvent.cacheEventType() + " is not supported");
            }
        }
    }
}
```
# Create Dynamic Table With Sequence Primary Key Generator
```java
        qDynamicTableFactory.
                .createSequence("dynamicTestTableSequance1")
                .initialValue(1000L)
                .increment(10L)
                .min(1000L)
                .max(10000L)
                .addSequence()
                .buildSchema();
```

# Create SQL Sequence on runtime
```java
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .columns().addNumberColumn("ID", Integer.class).useAsPrimaryKey().createColumn()
                .addStringColumn("testColumn").size(100).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence("dynamicTestTableSequance1")).endPrimaryKey()
                .endBuildTables()
                .createSequence("dynamicTestTableSequance1")
                .initialValue(1000L)
                .addSequence()
                .buildSchema();
```
# Create SQL View on runtime
```java
        qDynamicTableFactory
                .createView("testView").resultSet(ormQueryFactory.buildQuery()
                .from(QTestTableVersionAnnotation.qTestTableVersionAnnotation), QTestTableVersionAnnotation.qTestTableVersionAnnotation.id)
                .addView()
                .buildSchema();
```
# use SQL View
if you use selectcache() pay attention to the method "registerRelatedTables"
```java
        qDynamicTableFactory
                .createView("testView").resultSet(ormQueryFactory.buildQuery()
                .from(QTestTableVersionAnnotation.qTestTableVersionAnnotation), QTestTableVersionAnnotation.qTestTableVersionAnnotation.id).addView()
                .buildSchema();

        QDynamicTable testView = qDynamicTableFactory.getQDynamicTableByName("testView");
        assertNotNull(testView);

        TestTableVersionAnnotation testTableVersionAnnotation = new TestTableVersionAnnotation();
        ormQueryFactory.insert(testTableVersionAnnotation);

        // fetch data from table
         // if you want cache the result you can use selectCache() instead of select()
        TestTableVersionAnnotation versionAnnotation = ormQueryFactory.select()
                .findOne(ormQueryFactory.buildQuery(), TestTableVersionAnnotation.class);
        assertNotNull(versionAnnotation);
        
        // fetch data from View
        DynamicTableModel dynamicTableModel = ormQueryFactory.select()
                .findOne(ormQueryFactory.buildQuery().from(testView), testView);
        assertNotNull(dynamicTableModel);
        
          // fetch data from View with cache (need manually register related tables with query)
        DynamicTableModel dynamicTableModel2 = ormQueryFactory.selectCache().registerRelatedTables(
                Collections.singletonList(QTestTableVersionAnnotation.qTestTableVersionAnnotation))
                .findOne(ormQueryFactory.buildQuery().from(testView), testView);
        assertNotNull(dynamicTableModel2);
```


# SQL subquery (SQL query nested inside a larger query.)

```java
        // create database schema
        qDynamicTableFactory.buildTables("UnionTable1")
                .columns().addStringColumn("Id1").size(255).useAsPrimaryKey().createColumn()
                .addDateTimeColumn("modificationTime1").notNull().createColumn()
                .addStringColumn("TestColumn1_1").size(255).createColumn()
                .addStringColumn("TestColumn1_2").size(255).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .addVersionColumn("modificationTime1")
                .buildNextTable("UnionTable2")
                .columns()
                .addStringColumn("Id2").size(255).useAsPrimaryKey().createColumn()
                .addDateTimeColumn("modificationTime2").notNull().createColumn()
                .addStringColumn("TestColumn2_1").size(255).createColumn()
                .addStringColumn("TestColumn2_2").size(255).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .addVersionColumn("modificationTime2")
                .endBuildTables()
                .buildSchema();

        // get unionTable1 Metadata 
        QDynamicTable unionTable1 = qDynamicTableFactory.getQDynamicTableByName("UnionTable1");
        // get unionTable2 Metadata 
        QDynamicTable unionTable2 = qDynamicTableFactory.getQDynamicTableByName("UnionTable2");

        // get column from unionTable1 
        StringPath testColumn11 = unionTable1.getStringColumnByName("TestColumn1_1");
        // get columns from unionTable2 
        StringPath testColumn21 = unionTable2.getStringColumnByName("TestColumn2_1");
        StringPath testColumn22 = unionTable2.getStringColumnByName("TestColumn2_2");

        // create subquery
        SQLQuery<String> query = SQLExpressions
                .select(testColumn21)
                .from(unionTable2).where(testColumn22.eq("data2"));

         // show the final SQL
        String sql = ormQueryFactory.select().showSql(ormQueryFactory.buildQuery().from(unionTable1)
                .where(testColumn11.in(query)), unionTable1);

        assertEquals(sql, "select \"UNIONTABLE1\".\"ID1\", \"UNIONTABLE1\".\"MODIFICATIONTIME1\", \"UNIONTABLE1\".\"TESTCOLUMN1_1\", \"UNIONTABLE1\".\"TESTCOLUMN1_2\"\n" +
                "from \"UNIONTABLE1\" \"UNIONTABLE1\"\n" +
                "where \"UNIONTABLE1\".\"TESTCOLUMN1_1\" in (select \"UNIONTABLE2\".\"TESTCOLUMN2_1\"\n" +
                "from \"UNIONTABLE2\" \"UNIONTABLE2\"\n" +
                "where \"UNIONTABLE2\".\"TESTCOLUMN2_2\" = 'data2')");

        // fetch data
         // if you want cache the result you can use selectCache() instead of select()
        DynamicTableModel tableModel = ormQueryFactory.select().findOne(
                ormQueryFactory.buildQuery().from(unionTable1)
                        .where(testColumn11.in(query)), unionTable1);
```

# Union query with groupBy, orderBy, offset and limit

```java
        // create database schema
        qDynamicTableFactory.buildTables("UnionTable1")
                .columns().addStringColumn("Id1").size(255).useAsPrimaryKey().createColumn()
                .addDateTimeColumn("modificationTime1").notNull().createColumn()
                .addStringColumn("TestColumn1_1").size(255).createColumn()
                .addStringColumn("TestColumn1_2").size(255).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .addVersionColumn("modificationTime1")
                .buildNextTable("UnionTable2")
                .columns()
                .addStringColumn("Id2").size(255).useAsPrimaryKey().createColumn()
                .addDateTimeColumn("modificationTime2").notNull().createColumn()
                .addStringColumn("TestColumn2_1").size(255).createColumn()
                .addStringColumn("TestColumn2_2").size(255).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .addVersionColumn("modificationTime2")
                .endBuildTables()
                .buildSchema();

        // get unionTable1 Metadata
        QDynamicTable unionTable1 = qDynamicTableFactory.getQDynamicTableByName("UnionTable1");
        // get unionTable2 Metadata
        QDynamicTable unionTable2 = qDynamicTableFactory.getQDynamicTableByName("UnionTable2");
        
        // get column from unionTable1
        StringPath testColumn11 = unionTable1.getStringColumnByName("TestColumn1_1");
        StringPath testColumn12 = unionTable1.getStringColumnByName("TestColumn1_2");
        StringPath testColumn21 = unionTable2.getStringColumnByName("TestColumn2_1");
        StringPath testColumn22 = unionTable2.getStringColumnByName("TestColumn2_2");

        // first subquery
        SQLQuery<Tuple> query1 = SQLExpressions
                .select(testColumn11.as("column1"), testColumn12.as("column2"))
                .from(unionTable1).where(testColumn12.eq("data1"));
        
        // second subquery
        SQLQuery<Tuple> query2 = SQLExpressions
                .select(testColumn21.as("column1"), testColumn22.as("column2"))
                .from(unionTable2).where(testColumn22.eq("data2"));

        // create UnionBuilder
         // if you want cache the result you can use selectCache() instead of select()
        UnionBuilder unionBuilder = ormQueryFactory.select()
                .unionAll(ormQueryFactory.buildQuery(), query1, query2);
        
        // result order by
        unionBuilder
                .orderBy("column1").desc().orderBy("column2").asc();
        
        // offset and limit (offset = 0, limit = 2 )
        unionBuilder.limit(new Range(0, 2));
        
        // group by result
        unionBuilder.groupBy("column1", "column2");
        
         // show final SQL
        String sql = unionBuilder.showSql();
        
        assertEquals(sql, "select \"column1\", \"column2\"\n" +
                "from ((select \"UNIONTABLE1\".\"TESTCOLUMN1_1\" as \"column1\", \"UNIONTABLE1\".\"TESTCOLUMN1_2\" as \"column2\"\n" +
                "from \"UNIONTABLE1\" \"UNIONTABLE1\"\n" +
                "where \"UNIONTABLE1\".\"TESTCOLUMN1_2\" = 'data1')\n" +
                "union all\n" +
                "(select \"UNIONTABLE2\".\"TESTCOLUMN2_1\" as \"column1\", \"UNIONTABLE2\".\"TESTCOLUMN2_2\" as \"column2\"\n" +
                "from \"UNIONTABLE2\" \"UNIONTABLE2\"\n" +
                "where \"UNIONTABLE2\".\"TESTCOLUMN2_2\" = 'data2')) as \"union\"\n" +
                "group by \"column1\", \"column2\"\n" +
                "order by \"column1\" desc, \"column2\" asc\n" +
                "limit 2\n" +
                "offset 0");

        // fetch result
        List<RawModel> rawModels = unionBuilder.findAll();
        
        // get first record
        RawModel rawModel = rawModels.get(0);
        
        // get column1 value
        String column1Value = rawModel.getValueByColumnName("column1", String.class);
        
        // get column2 value
        String column2Value = rawModel.getValueByColumnName("column2", String.class);

```
# Union count query with cache

```java
        // create database schema
        qDynamicTableFactory.buildTables("UnionTable1")
                .columns().addStringColumn("Id1").size(255).useAsPrimaryKey().createColumn()
                .addDateTimeColumn("modificationTime1").notNull().createColumn()
                .addStringColumn("TestColumn1_1").size(255).createColumn()
                .addStringColumn("TestColumn1_2").size(255).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .addVersionColumn("modificationTime1")
                .buildNextTable("UnionTable2")
                .columns()
                .addStringColumn("Id2").size(255).useAsPrimaryKey().createColumn()
                .addDateTimeColumn("modificationTime2").notNull().createColumn()
                .addStringColumn("TestColumn2_1").size(255).createColumn()
                .addStringColumn("TestColumn2_2").size(255).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .addVersionColumn("modificationTime2")
                .endBuildTables()
                .buildSchema();

       // get unionTable1 Metadata
        QDynamicTable unionTable1 = qDynamicTableFactory.getQDynamicTableByName("UnionTable1");
        // get unionTable2 Metadata
        QDynamicTable unionTable2 = qDynamicTableFactory.getQDynamicTableByName("UnionTable2");
        // get column from unionTable1
        StringPath id1 = unionTable1.getStringColumnByName("Id1");
        // get column from unionTable2
        StringPath id2 = unionTable2.getStringColumnByName("Id2");


        SQLQuery<String> query1 = SQLExpressions
                .select(id1)
                .from(unionTable1);

        SQLQuery<String> query2 = SQLExpressions
                .select(id2)
                .from(unionTable2);

        // create UnionBuilder
        UnionBuilder unionBuilder = ormQueryFactory.selectCache()
                .unionAll(ormQueryFactory.buildQuery(), query1, query2);

        // unionBuilder.groupBy("column1", "column2");

         // show final SQL
        String sql = unionBuilder.showCountSql();
        assertEquals(sql, "select count(*)\n" +
                "from ((select \"UNIONTABLE1\".\"ID1\"\n" +
                "from \"UNIONTABLE1\" \"UNIONTABLE1\")\n" +
                "union all\n" +
                "(select \"UNIONTABLE2\".\"ID2\"\n" +
                "from \"UNIONTABLE2\" \"UNIONTABLE2\")) as \"union\"");

        // fetch result
        Long count1 = unionBuilder.count();
        // result from cache
        Long count2 = unionBuilder.count();
        // insert to unionTable1
        insert2("someData", "data3"); //   ormQueryFactory.insert(unionTable2);
        // cache is evicted and get a new value
        Long count3 = unionBuilder.count();
```
#  CTE with Union query

```java
        // create database schema
        qDynamicTableFactory.buildTables("UnionTable1")
                .columns().addStringColumn("Id1").size(255).useAsPrimaryKey().createColumn()
                .addDateTimeColumn("modificationTime1").notNull().createColumn()
                .addStringColumn("TestColumn1_1").size(255).createColumn()
                .addStringColumn("TestColumn1_2").size(255).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .addVersionColumn("modificationTime1)
                .endBuildTables()
                .buildSchema();

        // get unionTable1 Metadata
        QDynamicTable unionTable1 = qDynamicTableFactory.getQDynamicTableByName("UnionTable1");
        // get column from unionTable1
        StringPath testColumn11 = unionTable1.getStringColumnByName("TestColumn1_1");
        StringPath testColumn12 = unionTable1.getStringColumnByName("TestColumn1_2");

        SimplePath<String> column1 = Expressions.simplePath(String.class, "column1");
        SimplePath<String> column2 = Expressions.simplePath(String.class, "column2");

        // prepare with operator
        SimplePath<Void> withSubquery = Expressions.path(Void.class, "CTE_SUBQUERY");
        SQLQuery withQuery = (SQLQuery) ormQueryFactory.buildQuery().with(
                withSubquery,
                column1,
                column2
        ).as(SQLExpressions
                .select(testColumn11.as("column1"), testColumn12.as("column2"))
                .from(unionTable1));

        // first union subquery
        SQLQuery<Tuple> query1 = SQLExpressions
                .select(column1, column2)
                .from(withSubquery).where(column2.eq("data1"));
        // second union subquery
        SQLQuery<Tuple> query2 = SQLExpressions
                .select(column1, column2)
                .from(withSubquery).where(column2.eq("data2"));

        // create UnionBuilder
         // if you want cache the result you can use selectCache() instead of select()
        UnionBuilder unionBuilder = ormQueryFactory.select()
                .unionAll(ormQueryFactory.buildQuery(), query1, query2);
        // result order by
        unionBuilder
                .orderBy("column1").desc().orderBy("column2").asc();
        // offset and limit (offset = 0, limit = 2 )
        unionBuilder.limit(new Range(0, 2));
        // group by result
        unionBuilder.groupBy("column1", "column2");

        // build union query with "with" operator

        SQLQuery unionSubQuery = unionBuilder.getUnionSubQuery();
        ProjectableSQLQuery sqlQuery = withQuery.select(column1, column2)
                .from(unionSubQuery.select(column1, column2));

        // show final SQL
        assertEquals(ormQueryFactory.select().rawSelect(sqlQuery).showSql(column1, column2),
                "with \"CTE_SUBQUERY\" (\"column1\", \"column2\") as (select \"UNIONTABLE1\".\"TESTCOLUMN1_1\" as \"column1\", \"UNIONTABLE1\".\"TESTCOLUMN1_2\" as \"column2\"\n" +
                        "from \"UNIONTABLE1\" \"UNIONTABLE1\")\n" +
                        "select \"column1\", \"column2\"\n" +
                        "from (select \"column1\", \"column2\"\n" +
                        "from ((select \"column1\", \"column2\"\n" +
                        "from \"CTE_SUBQUERY\"\n" +
                        "where \"column2\" = 'data1')\n" +
                        "union all\n" +
                        "(select \"column1\", \"column2\"\n" +
                        "from \"CTE_SUBQUERY\"\n" +
                        "where \"column2\" = 'data2')) as \"union\"\n" +
                        "group by \"column1\", \"column2\"\n" +
                        "order by \"column1\" desc, \"column2\" asc\n" +
                        "limit 2\n" +
                        "offset 0)");
        // fetch data (if you want cache the result you can use selectCache() instead of select() )
        List<RawModel> rawModels = ormQueryFactory.select().rawSelect(sqlQuery).findAll(column1, column2);
        RawModel rawModel = rawModels.get(0);
        String column1Value = rawModel.getColumnValue(column1);
        String column2Value = rawModel.getColumnValue(column2);
```
#  count CTE operator (cacheable)

```java
        // create database schema
        qDynamicTableFactory.buildTables("UnionTable1")
                .columns().addStringColumn("Id1").size(255).useAsPrimaryKey().createColumn()
                .addDateTimeColumn("modificationTime1").notNull().createColumn()
                .addStringColumn("TestColumn1_1").size(255).createColumn()
                .addStringColumn("TestColumn1_2").size(255).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .addVersionColumn("modificationTime1)
                .endBuildTables()
                .buildSchema();

       // get unionTable1 Metadata
        QDynamicTable unionTable1 = qDynamicTableFactory.getQDynamicTableByName("UnionTable1");
        // get column from unionTable1
        StringPath testColumn11 = unionTable1.getStringColumnByName("TestColumn1_1");
        StringPath testColumn12 = unionTable1.getStringColumnByName("TestColumn1_2");

        SimplePath<String> column1 = Expressions.simplePath(String.class, "column1");
        SimplePath<String> column2 = Expressions.simplePath(String.class, "column2");


        SimplePath<Void> withSubquery = Expressions.path(Void.class, "CTE_SUBQUERY");

        SQLQuery withQuery = (SQLQuery) ormQueryFactory.buildQuery().with(
                withSubquery,
                column1,
                column2
        ).as(SQLExpressions
                .select(testColumn11.as("column1"), testColumn12.as("column2"))
                .from(unionTable1));

        // first subquery
        SQLQuery<Tuple> query1 = SQLExpressions
                .select(column1, column2)
                .from(withSubquery).where(column2.eq("data1"));
        // second subquery
        SQLQuery<Tuple> query2 = SQLExpressions
                .select(column1, column2)
                .from(withSubquery).where(column2.eq("data2"));

        // create UnionBuilder
        UnionBuilder unionBuilder = ormQueryFactory.select()
                .unionAll(ormQueryFactory.buildQuery(), query1, query2);
        // result order by
        unionBuilder
                .orderBy("column1").desc().orderBy("column2").asc();
        // offset and limit (offset = 0, limit = 2 )
        unionBuilder.limit(new Range(0, 4));
        // group by result
        unionBuilder.groupBy("column1", "column2");

        SQLQuery unionSubQuery = unionBuilder.getUnionSubQuery();
        ProjectableSQLQuery sqlQuery = withQuery.select(column1, column2)
                .from(unionSubQuery.select(column1, column2));

        assertEquals(ormQueryFactory.select().rawSelect(sqlQuery).showSql(Wildcard.count),
                "with \"CTE_SUBQUERY\" (\"column1\", \"column2\") as (select \"UNIONTABLE1\".\"TESTCOLUMN1_1\" as \"column1\", \"UNIONTABLE1\".\"TESTCOLUMN1_2\" as \"column2\"\n" +
                        "from \"UNIONTABLE1\" \"UNIONTABLE1\")\n" +
                        "select count(*)\n" +
                        "from (select \"column1\", \"column2\"\n" +
                        "from ((select \"column1\", \"column2\"\n" +
                        "from \"CTE_SUBQUERY\"\n" +
                        "where \"column2\" = 'data1')\n" +
                        "union all\n" +
                        "(select \"column1\", \"column2\"\n" +
                        "from \"CTE_SUBQUERY\"\n" +
                        "where \"column2\" = 'data2')) as \"union\"\n" +
                        "group by \"column1\", \"column2\"\n" +
                        "order by \"column1\" desc, \"column2\" asc\n" +
                        "limit 4\n" +
                        "offset 0)");

        //fetch data and put result to the cache
        RawModel rawModel = ormQueryFactory.selectCache().rawSelect(sqlQuery).findOne(Wildcard.count);
        Long countValue = rawModel.getAliasValue(Wildcard.count);

        //fetch data from the cache
        RawModel rawModelFromCache = ormQueryFactory.selectCache().rawSelect(sqlQuery).findOne(Wildcard.count);
        Long countValueCache = rawModelFromCache.getAliasValue(Wildcard.count);

        // insert to unionTable1
        insert1("newValue", "data1"); // ormQueryFactory.insert(dynamicTableModel);
        
        // cache is automatically evicted then get a new value and result put to the cache
        RawModel rawModelAndPutNewCache = ormQueryFactory.selectCache().rawSelect(sqlQuery).findOne(Wildcard.count);
        Long newCountValue = rawModelAndPutNewCache.getAliasValue(Wildcard.count);

        
```
#  Composite Primary key

```java
        // create Database schema
        qDynamicTableFactory
                .buildTables("testDynamicTableWithCompositeKey")
                .columns().addNumberColumn("id1", Integer.class)
                .useAsPrimaryKey().createColumn()
                .addStringColumn("id2").size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("testColumn").size(255).createColumn()
                .endColumns().endBuildTables().buildSchema();

        // get dynamic table
        QDynamicTable table = qDynamicTableFactory
                .getQDynamicTableByName("testDynamicTableWithCompositeKey");

        // insert Data
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        dynamicTableModel.addColumnValue("Id1", 1);
        dynamicTableModel.addColumnValue("Id2", "2");
        dynamicTableModel.addColumnValue("testColumn", "test");
        ormQueryFactory.insert(dynamicTableModel);

        List<DynamicTableModel> models = ormQueryFactory.selectCache().findAll(table);
        assertNotNull(models);
        assertEquals(models.size(), 1);
        assertEquals(models.get(0).getValue("Id1", Integer.class), Integer.valueOf(1));
        assertEquals(models.get(0).getValue("Id2", String.class), "2");
        assertEquals(models.get(0).getValue("testColumn", String.class), "test");
```

#  remove column from the Composite key

```java
        // create Database schema
        qDynamicTableFactory
                .buildTables("testDynamicTableWithCompositeKey")
                .columns().addNumberColumn("id1", Integer.class)
                .useAsPrimaryKey().createColumn()
                .addStringColumn("id2").size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("testColumn").size(255).createColumn()
                .endColumns().endBuildTables().buildSchema();

        //  remove Id2 from primary key
        qDynamicTableFactory
                .buildTables(table.getTableName())
                .primaryKey()
                .removePrimaryKey("Id2")
                .endPrimaryKey()
                .endBuildTables().buildSchema();
        //  set column Id2 as nullable
        qDynamicTableFactory
                .buildTables(table.getTableName())
                .columns().modifyColumn().nullable("Id2").finish().endColumns()
                .endBuildTables().buildSchema();

        // insert Data without Id2
        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(table);
        dynamicTableModel1.addColumnValue("Id1", 1);
        dynamicTableModel1.addColumnValue("testColumn", "test");
        ormQueryFactory.insert(dynamicTableModel1);
```
