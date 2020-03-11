package com.github.vzakharchenko.dynamic.orm.core.exception;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NoActionExeption that = (NoActionExeption) o;
        return rowsAffected == that.rowsAffected &&
                rowsExpected == that.rowsExpected;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowsAffected, rowsExpected);
    }
}
