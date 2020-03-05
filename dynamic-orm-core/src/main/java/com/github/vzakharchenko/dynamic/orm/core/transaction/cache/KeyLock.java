package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
public class KeyLock extends ReentrantLock {

    private Serializable key;

    public KeyLock(Serializable key) {
        this.key = key;
    }

    public Serializable getKey() {
        return key;
    }


}
