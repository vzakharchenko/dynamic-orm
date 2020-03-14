package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import org.testng.annotations.Test;

public class SimpleCacheKeyLockStrategyTest {
    SimpleCacheKeyLockStrategy simpleCacheKeyLockStrategy = new SimpleCacheKeyLockStrategy();

    @Test
    public void testSimpleCacheKeyLockStrategy() {

        simpleCacheKeyLockStrategy.lock("test");
        simpleCacheKeyLockStrategy.unLock("test");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testSimpleCacheKeyLockStrategyFailed() {
        simpleCacheKeyLockStrategy.unLock("test1");
    }
}
