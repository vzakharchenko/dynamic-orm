package com.github.vzakharchenko.dynamic.orm;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.pk.UUIDPKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.CrudBuilder;
import com.github.vzakharchenko.dynamic.orm.model.TestTableVersion;
import com.github.vzakharchenko.dynamic.orm.model.TestTableVersionAnnotation;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersion;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersionAnnotation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.testng.Assert.*;

/**
 *
 */
public class VersionTest extends OracleTestQueryOrm {

    @Test
    public void testInsert() {
        TestTableVersion testTableVersion = new TestTableVersion();
        ormQueryFactory.modify(TestTableVersion.class)
                .versionColumn(QTestTableVersion.qTestTableVersion.version).insert(testTableVersion);
        assertNotNull(testTableVersion.getId());
        assertNotNull(testTableVersion.getVersion());
        assertEquals(testTableVersion.getVersion(), Integer.valueOf(0));
    }

    @Test
    public void testAnnotationInsert() {
        TestTableVersionAnnotation testTableVersion = new TestTableVersionAnnotation();
        ormQueryFactory.insert(testTableVersion);
        assertNotNull(testTableVersion.getId());
        assertNotNull(testTableVersion.getVersion());
        assertEquals(testTableVersion.getVersion(), Integer.valueOf(0));
    }

    @Test
    public void testDynamicInsert() {
        qDynamicTableFactory.buildTable("TEST_DYNAMIC_TABLE")
                .addPrimaryStringKey("ID", 100)
                .addPrimaryKeyGenerator(UUIDPKGenerator.getInstance())
                .createNumberColumn("VERSION", Integer.class, 38, 0, true).buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("TEST_DYNAMIC_TABLE");
        DynamicTableModel testTableVersion = new DynamicTableModel(qDynamicTable);
        ormQueryFactory.modify(qDynamicTable, DynamicTableModel.class)
                .versionColumn(qDynamicTable.getNumberColumnByName("VERSION")).insert(testTableVersion);

        assertNotNull(testTableVersion.getValue("ID"));
        assertNotNull(testTableVersion.getValue("VERSION"));
        assertEquals(testTableVersion.getValue("VERSION", Integer.class), Integer.valueOf(0));
    }

    @Test
    public void testDynamicPresetInsert() {
        qDynamicTableFactory.buildTable("TEST_DYNAMIC_TABLE")
                .addPrimaryStringKey("ID", 100)
                .addPrimaryKeyGenerator(UUIDPKGenerator.getInstance())
                .createNumberColumn("VERSION", Integer.class, 38, 0, true)
                .addVersionColumn("VERSION")
                .buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("TEST_DYNAMIC_TABLE");
        DynamicTableModel testTableVersion = new DynamicTableModel(qDynamicTable);
        ormQueryFactory.insert(testTableVersion);

        assertNotNull(testTableVersion.getValue("ID"));
        assertNotNull(testTableVersion.getValue("VERSION"));
        assertEquals(testTableVersion.getValue("VERSION", Integer.class), Integer.valueOf(0));
    }

    @Test
    public void testUpdate() {
        TestTableVersion testTableVersion = new TestTableVersion();
        CrudBuilder<TestTableVersion> builder = ormQueryFactory.modify(TestTableVersion.class)
                .versionColumn(QTestTableVersion.qTestTableVersion.version);
        builder.insert(testTableVersion);
        assertNotNull(testTableVersion.getId());
        assertNotNull(testTableVersion.getVersion());
        assertEquals(testTableVersion.getVersion(), Integer.valueOf(0));
        builder.updateBuilder().set(QTestTableVersion.qTestTableVersion.id, testTableVersion.getId()).update();
        Integer version = ormQueryFactory.select().findOne(
                ormQueryFactory.buildQuery()
                        .from(QTestTableVersion.qTestTableVersion)
                        .where(QTestTableVersion.qTestTableVersion.id.eq(testTableVersion.getId()))
                , QTestTableVersion.qTestTableVersion.version);

        assertNotEquals(testTableVersion.getVersion(), version);
        Integer oldVersion = testTableVersion.getVersion();
        assertEquals(version, ++oldVersion);

    }

    @Test()
    public void testAnnotationUpdate() {
        TestTableVersionAnnotation testTableVersion = new TestTableVersionAnnotation();

        ormQueryFactory.insert(testTableVersion);
        assertNotNull(testTableVersion.getId());
        assertNotNull(testTableVersion.getVersion());
        assertEquals(testTableVersion.getVersion(), Integer.valueOf(0));
        ormQueryFactory.updateById(testTableVersion);
        Integer version = ormQueryFactory.select().findOne(
                ormQueryFactory.buildQuery()
                        .from(QTestTableVersionAnnotation.qTestTableVersionAnnotation)
                        .where(QTestTableVersionAnnotation.qTestTableVersionAnnotation.id.eq(testTableVersion.getId()))
                , QTestTableVersionAnnotation.qTestTableVersionAnnotation.version);

        assertNotEquals(testTableVersion.getVersion(), version);
        Integer oldVersion = testTableVersion.getVersion();
        assertEquals(version, ++oldVersion);

    }

    @Test(expectedExceptions = ExecutionException.class)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testConcurrentlyUpdateFail() throws InterruptedException, ExecutionException {

        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        TestTableVersion testTableVersion = new TestTableVersion();
        CrudBuilder<TestTableVersion> builder = ormQueryFactory.modify(TestTableVersion.class)
                .versionColumn(QTestTableVersion.qTestTableVersion.version);
        builder.insert(testTableVersion);
        assertNotNull(testTableVersion.getId());
        assertNotNull(testTableVersion.getVersion());
        assertEquals(testTableVersion.getVersion(), Integer.valueOf(0));
        ormQueryFactory.transactionManager().commit();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<Long>> futures = new ArrayList<>();
        int updateCount = 100;
        for (int i = 0; i < updateCount; i++) {
            Future<Long> submit = executorService.submit(() -> {
                ormQueryFactory.transactionManager().startTransactionIfNeeded();
                try {
                    Long aLong = builder.updateBuilder().set(QTestTableVersion.qTestTableVersion.id, testTableVersion.getId()).update();
                    ormQueryFactory.transactionManager().commit();
                    return aLong;
                } catch (RuntimeException ex) {
                    ormQueryFactory.transactionManager().rollback();
                    throw ex;
                } catch (Exception ex) {
                    ormQueryFactory.transactionManager().rollback();
                    throw new IllegalStateException(ex);
                }
            });
            futures.add(submit);
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        Integer version = ormQueryFactory.select().findOne(
                ormQueryFactory.buildQuery()
                        .from(QTestTableVersion.qTestTableVersion)
                        .where(QTestTableVersion.qTestTableVersion.id.eq(testTableVersion.getId()))
                , QTestTableVersion.qTestTableVersion.version);
        assertNotEquals(version, updateCount);
        assertTrue(updateCount > version);


        for (Future<Long> future : futures) {
            future.get();
        }
    }

    @Test
    public void testDynamicUpdate() {
        qDynamicTableFactory.buildTable("TEST_DYNAMIC_TABLE")
                .addPrimaryStringKey("ID", 100)
                .addPrimaryKeyGenerator(UUIDPKGenerator.getInstance())
                .createNumberColumn("VERSION", Integer.class, 38, 0, true).buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("TEST_DYNAMIC_TABLE");
        DynamicTableModel testTableVersion = new DynamicTableModel(qDynamicTable);
        CrudBuilder<DynamicTableModel> builder = ormQueryFactory.modify(qDynamicTable, DynamicTableModel.class)
                .versionColumn(qDynamicTable.getNumberColumnByName("VERSION"));
        builder.insert(testTableVersion);

        assertNotNull(testTableVersion.getValue("ID"));
        assertNotNull(testTableVersion.getValue("VERSION"));
        assertEquals(testTableVersion.getValue("VERSION", Integer.class), Integer.valueOf(0));
        builder.updateBuilder().set(qDynamicTable.getStringColumnByName("ID"), testTableVersion.getValue("ID", String.class)).update();

        Integer version = ormQueryFactory.select().findOne(
                ormQueryFactory.buildQuery()
                        .from(qDynamicTable)
                        .where(qDynamicTable.getStringColumnByName("ID").eq(testTableVersion.getValue("ID", String.class)))
                , qDynamicTable.getNumberColumnByName("VERSION", Integer.class));

        assertNotEquals(testTableVersion.getValue("VERSION"), version);
        Integer oldVersion = testTableVersion.getValue("VERSION", Integer.class);
        assertEquals(version, ++oldVersion);
    }


    @Test(expectedExceptions = ExecutionException.class)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testConcurrentlyDynamicUpdateFail() throws InterruptedException, ExecutionException {

        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        qDynamicTableFactory.buildTable("TEST_DYNAMIC_TABLE")
                .addPrimaryStringKey("ID", 100)
                .addPrimaryKeyGenerator(UUIDPKGenerator.getInstance())
                .createNumberColumn("VERSION", Integer.class, 38, 0, true).buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("TEST_DYNAMIC_TABLE");
        DynamicTableModel testTableVersion = new DynamicTableModel(qDynamicTable);
        CrudBuilder<DynamicTableModel> builder = ormQueryFactory.modify(qDynamicTable, DynamicTableModel.class)
                .versionColumn(qDynamicTable.getNumberColumnByName("VERSION"));
        builder.insert(testTableVersion);
        ormQueryFactory.transactionManager().commit();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<Long>> futures = new ArrayList<>();
        int updateCount = 100;
        for (int i = 0; i < updateCount; i++) {
            Future<Long> submit = executorService.submit(() -> {
                ormQueryFactory.transactionManager().startTransactionIfNeeded();
                try {
                    Long aLong = builder.updateBuilder().set(qDynamicTable.getStringColumnByName("ID"), testTableVersion.getValue("ID", String.class)).update();
                    ;
                    ormQueryFactory.transactionManager().commit();
                    return aLong;
                } catch (RuntimeException ex) {
                    ormQueryFactory.transactionManager().rollback();
                    throw ex;
                } catch (Exception ex) {
                    ormQueryFactory.transactionManager().rollback();
                    throw new IllegalStateException(ex);
                }
            });
            futures.add(submit);
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        Integer version = ormQueryFactory.select().findOne(
                ormQueryFactory.buildQuery()
                        .from(qDynamicTable)
                        .where(qDynamicTable.getStringColumnByName("ID").eq(testTableVersion.getValue("ID", String.class)))
                , qDynamicTable.getNumberColumnByName("VERSION", Integer.class));
        assertNotEquals(version, updateCount);
        assertTrue(updateCount > version);


        for (Future<Long> future : futures) {
            future.get();
        }
    }
}
