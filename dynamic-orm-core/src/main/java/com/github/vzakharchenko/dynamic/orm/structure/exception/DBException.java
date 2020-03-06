package com.github.vzakharchenko.dynamic.orm.structure.exception;

/**
 * Created by vzakharchenko on 24.07.14.
 */
public class DBException extends Exception {

    public DBException() {
        super();
    }

    public DBException(String message) {
        super(message);
    }

    public DBException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBException(Throwable cause) {
        super(cause);
    }
}
