package com.github.vzakharchenko.dynamic.orm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.annotations.Test;
import com.github.vzakharchenko.dynamic.orm.core.cache.LazyList;
import com.github.vzakharchenko.dynamic.orm.core.exception.IsNotActiveTransaction;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheBuilder;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.CrudBuilder;
import com.github.vzakharchenko.dynamic.orm.model.Testtable;
import com.github.vzakharchenko.dynamic.orm.qModel.QTesttable;

import java.util.List;

import static org.testng.Assert.*;

/**
 *
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class TestCacheBuilderQueryOrm extends OracleTestQueryOrm {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void testInsert() {

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                Testtable testtable = new Testtable();
                testtable.setId(0);
                testtable.setTest2(2);

                CrudBuilder<Testtable> crudBuilder = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);
                crudBuilder.insert(testtable);
            }
        });


        Testtable tt = ormQueryFactory.selectCache().findOne(ormQueryFactory.buildQuery(), QTesttable.testtable, Testtable.class);

        assertNotNull(tt);
        assertEquals(tt.getId().intValue(), 0);
        assertEquals(tt.getTest2().intValue(), 2);
    }

    @Test
    public void testConnectionLeak1() {

    }

    @Test
    public void testConnectionLeak2() {

    }

    @Test
    public void testInsertDeleteInsert() {


        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                Testtable testtable = new Testtable();
                testtable.setId(0);
                testtable.setTest2(2);

                CrudBuilder<Testtable> crudBuilder = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);
                crudBuilder.insert(testtable);

                crudBuilder.delete(testtable).byId().delete();

                crudBuilder.insert(testtable);
            }
        });
    }


    @Test
    public void testTransactionCommit() {


        assertTrue(ormQueryFactory.transactionManager().startTransactionIfNeeded());
        assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
        ormQueryFactory.transactionManager().commit();
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "(Elsewhere is initiated transaction com.github.vzakharchenko.dynamic.orm.TestCacheBuilderQueryOrm.testTransactionCommitFail)")
    @Transactional
    public void testTransactionCommitFail() {
        assertTrue(TransactionSynchronizationManager.isSynchronizationActive());

        assertFalse(ormQueryFactory.transactionManager().startTransactionIfNeeded());
        assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
        ormQueryFactory.transactionManager().commit();
    }


    @Test(expectedExceptions = IsNotActiveTransaction.class, expectedExceptionsMessageRegExp = "(Transaction synchronization is not active)")
    public void testTransactionCommitCommitFail() {


        assertTrue(ormQueryFactory.transactionManager().startTransactionIfNeeded());
        assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
        ormQueryFactory.transactionManager().commit();
        assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        ormQueryFactory.transactionManager().commit();
    }

    @Test
    public void testTransactionRollback() {


        assertTrue(ormQueryFactory.transactionManager().startTransactionIfNeeded());
        assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
        ormQueryFactory.transactionManager().rollback();
    }

    @Test()
    @Transactional
    public void testTransactionalRollback() {

        assertFalse(ormQueryFactory.transactionManager().startTransactionIfNeeded());
        assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
        ormQueryFactory.transactionManager().rollback();
        assertTrue(TestTransaction.isFlaggedForRollback());
    }

    @Test
    public void testTransactionRollbackRollback() {


        assertTrue(ormQueryFactory.transactionManager().startTransactionIfNeeded());
        assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
        ormQueryFactory.transactionManager().rollback();
        assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        ormQueryFactory.transactionManager().rollback();
    }


    @Test
    public void testInsertDeleteInsertTransaction() {


        ormQueryFactory.transactionManager().startTransactionIfNeeded();

        try {
            Testtable testtable = new Testtable();
            testtable.setId(0);
            testtable.setTest2(2);

            CrudBuilder<Testtable> crudBuilder = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);
            crudBuilder.insert(testtable);

            crudBuilder.delete(testtable).byId().delete();

            crudBuilder.insert(testtable);
        } finally {
            ormQueryFactory.transactionManager().commit();
        }
    }

    @Test
    public void testUpdate() {


        CacheBuilder<Testtable> cacheQuery = ormQueryFactory.modelCacheBuilder(QTesttable.testtable, Testtable.class);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                Testtable testtable = new Testtable();
                testtable.setId(0);
                testtable.setTest2(2);

                CrudBuilder<Testtable> crudBuilder = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);
                crudBuilder.insert(testtable);
            }
        });


        LazyList<Testtable> lazyList = cacheQuery.findAllByColumn(QTesttable.testtable.test2, 2);
        assertEquals(lazyList.size(), 1);
        lazyList = cacheQuery.findAllByColumn(QTesttable.testtable.test2, 23423);
        assertEquals(lazyList.size(), 0);
        lazyList = cacheQuery.findAllByColumnIsNotNull(QTesttable.testtable.test2);
        assertEquals(lazyList.size(), 1);
        assertEquals(lazyList.getModelList().get(0).getTest2(), Integer.valueOf(2));


        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                Testtable testtable = new Testtable();
                testtable.setId(0);
                testtable.setTest2(23423);

                CrudBuilder<Testtable> crudBuilder = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);
                crudBuilder.updateBuilder().updateModel(testtable).update();
            }
        });

        lazyList = cacheQuery.findAllByColumn(QTesttable.testtable.test2, 2);
        assertEquals(lazyList.size(), 0);
        lazyList = cacheQuery.findAllByColumn(QTesttable.testtable.test2, 23423);
        assertEquals(lazyList.size(), 1);
        lazyList = cacheQuery.findAllByColumnIsNotNull(QTesttable.testtable.test2);
        LazyList lazyList2 = cacheQuery.findAllByColumnIsNotNull(QTesttable.testtable.test2);
        assertEquals(lazyList.size(), 1);

        assertEquals(lazyList.getModelList().get(0).getTest2(), Integer.valueOf(23423));
    }


    @Test
    @Transactional
    public void testCacheQuering() {


        CrudBuilder<Testtable> crudBuilder = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);

        Testtable testtable1 = new Testtable();
        testtable1.setId(0);
        testtable1.setTest2(2);
        crudBuilder.insert(testtable1);

        List<Testtable> tt = ormQueryFactory.selectCache().findAll(ormQueryFactory.buildQuery(), QTesttable.testtable, Testtable.class);
        List<Testtable> tt2 = ormQueryFactory.selectCache().findAll(ormQueryFactory.buildQuery(), QTesttable.testtable, Testtable.class);
        assertNotNull(tt2);
        assertEquals(tt2.size(), 1);
        assertEquals(tt.get(0).getTest2(), Integer.valueOf(2));

        Testtable testtable2 = new Testtable();
        testtable2.setId(1);
        testtable2.setTest2(4);
        crudBuilder.insert(testtable2);

        List<Testtable> tt3 = ormQueryFactory.selectCache().findAll(ormQueryFactory.buildQuery(), QTesttable.testtable, Testtable.class);
        assertTrue(tt != tt3);
        assertNotNull(tt3);
        assertEquals(tt3.size(), 2);
        assertEquals(tt3.get(0).getTest2(), Integer.valueOf(2));
        assertEquals(tt3.get(1).getTest2(), Integer.valueOf(4));


        testtable1.setTest2(1234);

        assertEquals(crudBuilder.updateBuilder().updateModel(testtable1).update(), Long.valueOf(1));

        List<Testtable> tt4 = ormQueryFactory.selectCache().findAll(ormQueryFactory.buildQuery(), QTesttable.testtable, Testtable.class);
        assertNotNull(tt4);
        assertEquals(tt4.size(), 2);
        assertEquals(tt4.get(0).getTest2(), Integer.valueOf(1234));
        assertEquals(tt4.get(1).getTest2(), Integer.valueOf(4));

    }

    @Test
    @Transactional
    public void testCacheRaw() {


        CrudBuilder<Testtable> crudBuilder = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);

        Testtable testtable1 = new Testtable();
        testtable1.setId(0);
        testtable1.setTest2(2);
        crudBuilder.insert(testtable1);

        List<Integer> all = ormQueryFactory.selectCache().findAll(ormQueryFactory.buildQuery().from(QTesttable.testtable), QTesttable.testtable.id);
        assertNotNull(all);
        assertEquals(all.size(), 1);
        assertEquals(all.get(0), Integer.valueOf(0));

        Testtable testtable2 = new Testtable();
        testtable2.setId(1);
        testtable2.setTest2(4);
        crudBuilder.insert(testtable2);

        List<Integer> all1 = ormQueryFactory.selectCache().findAll(ormQueryFactory.buildQuery().from(QTesttable.testtable), QTesttable.testtable.id);
        assertNotNull(all1);
        assertEquals(all1.size(), 2);
        assertEquals(all1.get(0), Integer.valueOf(0));
        assertEquals(all1.get(1), Integer.valueOf(1));


        testtable1.setTest2(1234);

        assertEquals(crudBuilder.updateBuilder().updateModel(testtable1).update(), Long.valueOf(1));

        List<Testtable> tt4 = ormQueryFactory.selectCache().findAll(ormQueryFactory.buildQuery(), QTesttable.testtable, Testtable.class);
        assertNotNull(tt4);
        assertEquals(tt4.size(), 2);
        assertEquals(tt4.get(0).getTest2(), Integer.valueOf(1234));
        assertEquals(tt4.get(1).getTest2(), Integer.valueOf(4));

    }
}
