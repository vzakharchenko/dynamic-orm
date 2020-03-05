package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;


import com.mysema.commons.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class SimpleCacheKeyLockStrategy implements CacheKeyLockStrategy {
    private static Logger logger = LoggerFactory.getLogger(SimpleCacheKeyLockStrategy.class);
    private final ConcurrentMap<Serializable, KeyLock> keyLockMap = new ConcurrentHashMap<>();

    @Override
    public void lock(Serializable key) {
        keyLockMap.putIfAbsent(key, new KeyLock(key));
        KeyLock keyLock = keyLockMap.get(key);
        try {
            Assert.isTrue(keyLock.tryLock(15, TimeUnit.SECONDS), "Locking timeout");
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        logger.trace("Lock cache Key:" + key + " " + System.nanoTime());
    }

    @Override
    public void unLock(Serializable key) {
        KeyLock keyLock = keyLockMap.get(key);
        if (keyLock == null) {
            throw new IllegalStateException(" lock for object " + key + " is not found");
        }
        if (!keyLock.isLocked()) {
            throw new IllegalStateException(key + " is not locked");
        }
        keyLock.unlock();
        logger.trace("Unlock cache Key:" + key + " " + System.nanoTime());
    }

}
