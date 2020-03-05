package com.github.vzakharchenko.dynamic.orm.core.exception;

/**
 *
 */
public class IsNotActiveTransaction extends RuntimeException {

    public IsNotActiveTransaction() {
        super("Transaction synchronization is not active");
    }
}
