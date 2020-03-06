package com.github.vzakharchenko.dynamic.orm.structure.exception;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 02.08.14
 * Time: 13:29
 */
public class UpdateException extends Exception {

    public UpdateException() {
        super();
    }

    public UpdateException(String message) {
        super(message);
    }

    public UpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateException(Throwable cause) {
        super(cause);
    }
}
