package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
public class KeyLock extends ReentrantLock {

    private final Serializable key;

    public KeyLock(Serializable key) {
        super();
        this.key = key;
    }

    public Serializable getKey() {
        return key;
    }


}
