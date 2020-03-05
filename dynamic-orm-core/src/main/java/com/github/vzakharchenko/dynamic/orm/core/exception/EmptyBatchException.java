package com.github.vzakharchenko.dynamic.orm.core.exception;

/**
 *
 */
public class EmptyBatchException extends RuntimeException {

    public EmptyBatchException() {
        super("empty batch");
    }
}
