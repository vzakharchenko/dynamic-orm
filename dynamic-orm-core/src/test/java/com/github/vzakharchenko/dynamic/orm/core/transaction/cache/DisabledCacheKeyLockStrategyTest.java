package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import org.testng.annotations.Test;

import static org.testng.Assert.assertNull;

public class DisabledCacheKeyLockStrategyTest {

    @Test
    public void testDisabledCacheKeyLockStrategy(){
        DisabledCacheKeyLockStrategy disabledCacheKeyLockStrategy = new DisabledCacheKeyLockStrategy();
        disabledCacheKeyLockStrategy.lock(null);
        disabledCacheKeyLockStrategy.unLock(null);
        KeyLock keyLock = new KeyLock(null);
        assertNull(keyLock.getKey());
    }
}
