package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.DebugAnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;
import org.apache.commons.collections4.ListUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

public class ConcurrentSelectCacheTest extends DebugAnnotationTestQueryOrm {


    public void createSchema() {
        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        qDynamicTableFactory

                .buildTables("DynamicTable")
                .columns().addStringColumn("Id").size(255).useAsPrimaryKey().create()
                .addDateTimeColumn("modificationTime").notNull().create()
                .addStringColumn("TestColumn").size(255).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).finish()
                .addVersionColumn("modificationTime")

                .buildNextTable("DynamicTable2")
                .columns()
                .addStringColumn("Id1").size(255).useAsPrimaryKey().create()
                .addStringColumn("Id2").size(255).useAsPrimaryKey().create()
                .addStringColumn("TestColumn").size(255).create()
                .addDateTimeColumn("modificationTime").notNull().create()
                .finish()
                .addVersionColumn("modificationTime")

                .finish().buildSchema();
        ormQueryFactory.transactionManager().commit();
    }

    public void insertThread(String value) {
        TransactionSynchronizationManager.clear();
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", value);
        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        ormQueryFactory.insert(dynamicTableModel);
        ormQueryFactory.transactionManager().commit();
    }

    public void insertThread2(String value) {
        TransactionSynchronizationManager.clear();
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable2");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("Id1", value);
        dynamicTableModel.addColumnValue("Id2", value);
        dynamicTableModel.addColumnValue("TestColumn", value);
        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        ormQueryFactory.insert(dynamicTableModel);
        ormQueryFactory.transactionManager().commit();
    }


    public void selectThread() throws InterruptedException {
        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        for (int i = 0; i < 400; i++) {

            QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
            List<DynamicTableModel> tableModels = ormQueryFactory.selectCache().findAll(dynamicTable);
        }
        ormQueryFactory.transactionManager().commit();
    }


    public void selectThread12() throws InterruptedException {
        QDynamicTable dynamicTable1 = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        QDynamicTable dynamicTable2 = qDynamicTableFactory.getQDynamicTableByName("DynamicTable2");
        for (int i = 0; i < 400; i++) {
            ormQueryFactory.transactionManager().startTransactionIfNeeded();
            List<RawModel> rawModels = ormQueryFactory.selectCache().rawSelect(
                    ormQueryFactory.buildQuery().from(dynamicTable1).innerJoin(dynamicTable2).on(
                            dynamicTable2.getStringColumnByName("TestColumn").eq(
                                    dynamicTable1.getStringColumnByName("TestColumn")
                            ))).findAll(ListUtils.union(dynamicTable1.getColumns(), dynamicTable2.getColumns()));
            List<DynamicTableModel> tableModels = ormQueryFactory.selectCache().findAll(dynamicTable1);
            ormQueryFactory.transactionManager().commit();
        }
        Thread.sleep(1000);
        for (int i = 0; i < 2; i++) {
            ormQueryFactory.transactionManager().startTransactionIfNeeded();
            List<RawModel> rawModels = ormQueryFactory.selectCache().rawSelect(
                    ormQueryFactory.buildQuery().from(dynamicTable1).innerJoin(dynamicTable2).on(
                            dynamicTable2.getStringColumnByName("TestColumn").eq(
                                    dynamicTable1.getStringColumnByName("TestColumn")
                            ))).findAll(ListUtils.union(dynamicTable1.getColumns(), dynamicTable2.getColumns()));
            List<DynamicTableModel> tableModels = ormQueryFactory.selectCache().findAll(dynamicTable1);
            ormQueryFactory.transactionManager().commit();

        }
    }


    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testConcurrents() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            try {
                testConcurrent();
                qDynamicTableFactory.dropTableOrView("DynamicTable","DynamicTable2").buildSchema();
            } catch (AssertionError e) {
                System.err.println("Iteration : " + i);
                throw e;
            }
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testConcurrent() throws InterruptedException {
        createSchema();
        ExecutorService executorServiceSelect = Executors.newFixedThreadPool(1);
        Future<?> future = executorServiceSelect.submit(() -> {
            try {
                selectThread();
            } catch (InterruptedException e) {
                throw new IllegalStateException();
            }
        });
        executorServiceSelect.shutdown();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            futures.add(executorService.submit(() -> insertThread(String.valueOf(finalI))));
        }
        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);
        executorServiceSelect.awaitTermination(90, TimeUnit.SECONDS);
        assertEquals(futures.size(), 100);
        for (Future f : futures) {
            assertTrue(f.isDone());
            assertFalse(f.isCancelled());
        }
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        List<DynamicTableModel> tableModels2 = ormQueryFactory.select().findAll(dynamicTable);
        List<DynamicTableModel> tableModels = ormQueryFactory.selectCache().findAll(dynamicTable);
        assertEquals(tableModels.size(), tableModels2.size());
        assertEquals(tableModels.size(), 100);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testConcurrents2() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            try {
                testConcurrent2Tables();
                qDynamicTableFactory.dropTableOrView("DynamicTable",
                        "DynamicTable2").buildSchema();
            } catch (AssertionError e) {
                System.err.println("Iteration : " + i);
                throw e;
            }
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testConcurrent2Tables() throws InterruptedException {
        createSchema();
        ExecutorService executorServiceSelect = Executors.newFixedThreadPool(1);
        Future<?> future1 = executorServiceSelect.submit(() -> {
            try {
                selectThread12();
            } catch (InterruptedException e) {
                throw new IllegalStateException();
            }
        });
        executorServiceSelect.shutdown();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            futures.add(executorService.submit(() -> insertThread(String.valueOf(finalI))));
        }
        for (int i = 0; i < 150; i++) {
            int finalI = i;
            futures.add(executorService.submit(() -> insertThread2(String.valueOf(finalI))));
        }
        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.MINUTES);
        executorServiceSelect.awaitTermination(90, TimeUnit.MINUTES);
        assertEquals(futures.size(), 250);
        for (Future f : futures) {
            assertTrue(f.isDone());
            assertFalse(f.isCancelled());
        }
        QDynamicTable dynamicTable1 = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        QDynamicTable dynamicTable2 = qDynamicTableFactory.getQDynamicTableByName("DynamicTable2");

        List<RawModel> rawModels1 = ormQueryFactory.selectCache().rawSelect(
                ormQueryFactory.buildQuery().from(dynamicTable1).innerJoin(dynamicTable2).on(
                        dynamicTable2.getStringColumnByName("TestColumn").eq(
                                dynamicTable1.getStringColumnByName("TestColumn")
                        ))).findAll(ListUtils.union(dynamicTable1.getColumns(), dynamicTable2.getColumns()));

        List<RawModel> rawModels2 = ormQueryFactory.selectCache().rawSelect(
                ormQueryFactory.buildQuery().from(dynamicTable1).innerJoin(dynamicTable2).on(
                        dynamicTable2.getStringColumnByName("TestColumn").eq(
                                dynamicTable1.getStringColumnByName("TestColumn")
                        ))).findAll(ListUtils.union(dynamicTable1.getColumns(), dynamicTable2.getColumns()));
        rawModels2 = ormQueryFactory.selectCache().rawSelect(
                ormQueryFactory.buildQuery().from(dynamicTable1).innerJoin(dynamicTable2).on(
                        dynamicTable2.getStringColumnByName("TestColumn").eq(
                                dynamicTable1.getStringColumnByName("TestColumn")
                        ))).findAll(ListUtils.union(dynamicTable1.getColumns(), dynamicTable2.getColumns()));

        assertEquals(rawModels1.size(), rawModels2.size());
        assertEquals(rawModels2.size(), 100);
    }
}
