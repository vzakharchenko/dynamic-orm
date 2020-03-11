package com.github.vzakharchenko.dynamic.orm.core.exception;

/**
 *
 */
public class NoActionExeption extends RuntimeException {

    private final long rowsAffected;
    private final long rowsExpected;

    public NoActionExeption(long rowsAffected, long rowsExpected) {
        super("expected " + rowsExpected + " actual " + rowsAffected);
        this.rowsAffected = rowsAffected;
        this.rowsExpected = rowsExpected;
    }

    public long getRowsAffected() {
        return rowsAffected;
    }

    public long getRowsExpected() {
        return rowsExpected;
    }
}
