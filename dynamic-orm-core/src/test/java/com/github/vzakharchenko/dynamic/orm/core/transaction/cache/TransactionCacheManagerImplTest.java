package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.testng.annotations.Test;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;

public class TransactionCacheManagerImplTest {

    private CacheManager cacheManager = mock(CacheManager.class);
    private Cache cache = mock(Cache.class);
    private TransactionCacheManagerImpl transactionCacheManager;

    @Test
    public void beforeMethods() {
        Mockito.reset(cacheManager);
        Mockito.reset(cache);
        when(cacheManager.getCacheNames()).thenReturn(Set.of("test"));
        when(cacheManager.getCache("test")).thenReturn(cache);
        when(cache.getName()).thenReturn("test");
        transactionCacheManager = new TransactionCacheManagerImpl(cacheManager);
    }

    @Test
    public void testTransactionCacheManagerImpl() {
        transactionCacheManager.setCacheKeyLockStrategy(mock(CacheKeyLockStrategy.class));
        assertNotNull(transactionCacheManager.getCacheNames());
    }
}
