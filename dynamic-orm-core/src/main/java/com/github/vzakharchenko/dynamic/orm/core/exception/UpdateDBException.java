package com.github.vzakharchenko.dynamic.orm.core.exception;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 25.04.15
 * Time: 11:15
 */
public class UpdateDBException extends Exception {

    public UpdateDBException() {
        super();
    }

    public UpdateDBException(String message) {
        super(message);
    }

    public UpdateDBException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateDBException(Throwable cause) {
        super(cause);
    }
}
