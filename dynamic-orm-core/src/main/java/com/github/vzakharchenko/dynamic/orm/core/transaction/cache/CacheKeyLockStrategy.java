package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import java.io.Serializable;

/**
 * cache lock strategy
 *
 * @see DisabledCacheKeyLockStrategy
 * @see SimpleCacheKeyLockStrategy
 */
public interface CacheKeyLockStrategy {

    void lock(Serializable key);

    void unLock(Serializable key);

}
