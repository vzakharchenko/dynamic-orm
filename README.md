# Dynamic Orm
[![CircleCI](https://circleci.com/gh/vzakharchenko/dynamic-orm.svg?style=svg)](https://circleci.com/gh/vzakharchenko/dynamic-orm)
[![Coverage Status](https://coveralls.io/repos/github/vzakharchenko/dynamic-orm/badge.svg?branch=master)](https://coveralls.io/github/vzakharchenko/dynamic-orm?branch=master)
[![Maintainability](https://api.codeclimate.com/v1/badges/5c587a6e77be5e8cbef0/maintainability)](https://codeclimate.com/github/vzakharchenko/dynamic-orm/maintainability)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.vzakharchenko/dynamic-orm/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.vzakharchenko/dynamic-orm)

# supported database
 - Oracle
 - Postgres
 - MySQL
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
            <version>1.1.1</version>
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
                .addColumns().addStringColumn("Id")
                .size(255).useAsPrimaryKey().create()
                .addStringColumn("TestStringColumn").size(255).create()
                .addDateColumn("modificationTime").create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(UUIDPKGenerator.getInstance())
                .finish()
                .addVersionColumn("modificationTime")
                .finish().buildSchema();
}
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
                .addColumns().addNumberColumn("newColumn", Integer.class).create().finish()
                .finish().buildSchema();
```
   - update operation
```java
        firstTableModel1.addColumnValue("newColumn", 122);
        ormQueryFactory.updateById(firstTableModel1);
```
   - select operation
```java
        DynamicTableModel firstTableFromDatabase = ormQueryFactory.select().findOne(ormQueryFactory
                        .buildQuery()
                        .from(firstTable)
                        .where(firstTable.getNumberColumnByName("newColumn").eq(122)),
                firstTable,
                DynamicTableModel.class);
```
   - select operation and put result to cache. Cache record will be evicted if any related table is modified (insert/update/delete operartion)
```java
        DynamicTableModel firstTableFromDatabase = ormQueryFactory.selectCache().findOne(ormQueryFactory
                        .buildQuery()
                        .from(firstTable)
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
                .addColumns().addStringColumn("Id")
                .size(255).useAsPrimaryKey().create()
                .addStringColumn("TestStringColumn").size(255).create()
                .addDateColumn("modificationTime").create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(UUIDPKGenerator.getInstance())
                .finish()
                .addVersionColumn("modificationTime")
                .buildNextTable("secondTable")
                .addColumns().addStringColumn("Id")
                .size(255).useAsPrimaryKey().create()
                .addBooleanColumn("isDeleted").notNull().create()
                .addDateTimeColumn("modificationTime").notNull().create()
                .addStringColumn("linkToFirstTable").size(255).create()
                .addStringColumn("uniqValue").size(255).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(UUIDPKGenerator.getInstance()).finish()
                .addSoftDeleteColumn("isDeleted", true, false)
                .addVersionColumn("modificationTime")
                .addIndex().buildIndex("uniqValue", true)
                .addForeignKey().buildForeignKey("linkToFirstTable", "firstTable")
                .finish().buildSchema();
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
                .addColumns().addNumberColumn("newColumn", Integer.class).create().finish()
                .finish().buildSchema();
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
        ormQueryFactory.softDeleteById(secondModel2);
        transactionManager.commit();

        // get new cache records (soft deleted values are not included)
        tableModels = ormQueryFactory.selectCache().findAll(secondTable, DynamicTableModel.class);
        assertEquals(tableModels.size(), 1);

        // select all data from all table
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
                .addColumns().addStringColumn("Id").size(255).useAsPrimaryKey().create()
                .addNumberColumn("StaticId", Integer.class).create()
                .addDateTimeColumn("modificationTime").notNull().create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(UUIDPKGenerator.getInstance()).finish()
                .addVersionColumn("modificationTime")
                .addForeignKey().buildForeignKey("StaticId", QTestTableVersionAnnotation.qTestTableVersionAnnotation,  QTestTableVersionAnnotation.qTestTableVersionAnnotation.id)
                .finish().buildSchema();

        // get dynamic table metadata
        QDynamicTable relatedTable = qDynamicTableFactory.getQDynamicTableByName("relatedTable");

        // insert to dynamic table
        DynamicTableModel relatedTableData = new DynamicTableModel(relatedTable);
        relatedTableData.addColumnValue("StaticId", staticTable.getId());

        ormQueryFactory.insert(relatedTableData);

        // select with join
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
                        relatedTable, DynamicTableModel.class);
        assertNotNull(tableModel);
        assertEquals(tableModel.getValue("Id"), relatedTableData.getValue("Id"));
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
                <version>1.1.1</version>
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
                .finish()
                .buildSchema();
```

# Create SQL Sequence on runtime
```java
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .addColumns().addNumberColumn("ID", Integer.class).useAsPrimaryKey().create()
                .addStringColumn("testColumn").size(100).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence("dynamicTestTableSequance1")).finish()
                .finish()
                .createSequence("dynamicTestTableSequance1")
                .initialValue(1000L)
                .finish()
                .buildSchema();
```
# Create SQL View on runtime
```java
        qDynamicTableFactory
                .createView("testView").resultSet(ormQueryFactory.buildQuery()
                .from(QTestTableVersionAnnotation.qTestTableVersionAnnotation), QTestTableVersionAnnotation.qTestTableVersionAnnotation.id)
                .finish()
                .buildSchema();
```
# use SQL View

```java
        qDynamicTableFactory
                .createView("testView").resultSet(ormQueryFactory.buildQuery()
                .from(QTestTableVersionAnnotation.qTestTableVersionAnnotation), QTestTableVersionAnnotation.qTestTableVersionAnnotation.id).finish()
                .buildSchema();

        QDynamicTable testView = qDynamicTableFactory.getQDynamicTableByName("testView");
        assertNotNull(testView);

        TestTableVersionAnnotation testTableVersionAnnotation = new TestTableVersionAnnotation();
        ormQueryFactory.insert(testTableVersionAnnotation);

        // select from table
        TestTableVersionAnnotation versionAnnotation = ormQueryFactory.select()
                .findOne(ormQueryFactory.buildQuery(), TestTableVersionAnnotation.class);
        assertNotNull(versionAnnotation);
        
        // select from View
        DynamicTableModel dynamicTableModel = ormQueryFactory.select()
                .findOne(ormQueryFactory.buildQuery().from(testView), testView, DynamicTableModel.class);
        assertNotNull(dynamicTableModel);
```