package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import java.io.Serializable;

/**
 *
 */
public class DisabledCacheKeyLockStrategy implements CacheKeyLockStrategy {
    @Override
    public void lock(Serializable key) {

    }

    @Override
    public void unLock(Serializable key) {

    }
}
