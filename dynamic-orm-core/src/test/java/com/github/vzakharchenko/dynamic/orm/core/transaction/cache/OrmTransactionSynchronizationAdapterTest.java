package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersion;
import org.springframework.cache.Cache;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.transaction.support.TransactionSynchronization.STATUS_COMMITTED;
import static org.springframework.transaction.support.TransactionSynchronization.STATUS_ROLLED_BACK;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class OrmTransactionSynchronizationAdapterTest {

    private Cache cache = mock(Cache.class);
    private TransactionalCache transactionalCache = mock(TransactionalCache.class);

    private OrmTransactionSynchronizationAdapter ormTransactionSynchronizationAdapter;

    @BeforeMethod
    public void beforeMethods() {
        reset(cache);
        reset(transactionalCache);
        TransactionSynchronizationManager.bindResource("test", transactionalCache);
        ormTransactionSynchronizationAdapter = new OrmTransactionSynchronizationAdapter(
                "test",
                "test", cache);

        when(transactionalCache.getEvictObjects()).thenReturn(Set.of("1"));
        when(transactionalCache.getDeletedObjects()).thenReturn(Set.of("1"));
        when(transactionalCache.getInsertedObjects()).thenReturn(Set.of("1"));
        when(transactionalCache.getUpdatedObjects()).thenReturn(Set.of("1"));
        when(transactionalCache.getUpdatedObjects()).thenReturn(Set.of("1"));
        when(cache.get("test")).thenReturn(() -> "testValue");
        when(cache.getName()).thenReturn("test");
    }


    @AfterMethod
    public void afterMethods() {
        TransactionSynchronizationManager.unbindResourceIfPossible("test");
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
            TransactionSynchronizationManager.clear();
        }
    }

    @Test
    public void testRollback() {
        ormTransactionSynchronizationAdapter.afterCompletion(STATUS_ROLLED_BACK);
        verify(cache, never()).evict(any());
    }

    @Test
    public void testCommited() {
        ormTransactionSynchronizationAdapter.afterCompletion(STATUS_COMMITTED);
        verify(cache, times(4)).evict(any());
    }

    @Test
    public void testTransactionalCacheDecorator() {
        CompositeKey primaryKeyCacheKey = PrimaryKeyHelper.getCompositeKey(1, QTestTableVersion.qTestTableVersion);
        TransactionalCacheDecorator transactionalCacheDecorator = new TransactionalCacheDecorator(cache);
        transactionalCacheDecorator.deleteModel(primaryKeyCacheKey);
        transactionalCacheDecorator.insertModel(primaryKeyCacheKey);
        transactionalCacheDecorator.updateModel(primaryKeyCacheKey);
    }

    @Test
    public void testTransactionalCacheDecorator2() {
        CompositeKey compositeKey = PrimaryKeyHelper.getCompositeKey(1, QTestTableVersion.qTestTableVersion);
        TransactionalCacheDecorator transactionalCacheDecorator = new TransactionalCacheDecorator(cache);
        try {
            TransactionSynchronizationManager.initSynchronization();
            transactionalCacheDecorator.deleteModel(compositeKey);
            transactionalCacheDecorator.insertModel(compositeKey);
            transactionalCacheDecorator.updateModel(compositeKey);
            assertNull(transactionalCacheDecorator.getFromTargetCache(compositeKey, List.class));
            assertNull(transactionalCacheDecorator.getFromTargetCache("1", List.class));
            transactionalCacheDecorator.getFromTargetCache("test", String.class);
            assertNotNull(transactionalCacheDecorator.getDeletedObjects());
            assertNotNull(transactionalCacheDecorator.getEvictObjects());
            assertNotNull(transactionalCacheDecorator.getInsertedObjects());
            assertNotNull(transactionalCacheDecorator.getUpdatedObjects());
            assertNotNull(transactionalCacheDecorator.getFromActiveTransaction("test", String.class));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    public void testTransactionalCacheImpl() {
        TransactionalCacheImpl transactionalCacheImpl = new TransactionalCacheImpl(cache);
        CompositeKey compositeKey = PrimaryKeyHelper.getCompositeKey(1, QTestTableVersion.qTestTableVersion);
        try {
            TransactionSynchronizationManager.initSynchronization();
            transactionalCacheImpl.deleteModel(compositeKey);
            transactionalCacheImpl.insertModel(compositeKey);
            transactionalCacheImpl.updateModel(compositeKey);
            assertNull(transactionalCacheImpl.getFromTargetCache(compositeKey, List.class));
            assertNull(transactionalCacheImpl.getFromTargetCache("1", List.class));
            Assert.assertEquals(transactionalCacheImpl.getFromTargetCache("test", String.class), "testValue");
            transactionalCacheImpl.getFromTargetCache("test", String.class);
            assertNotNull(transactionalCacheImpl.getInternalCache());
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }

    }
}
