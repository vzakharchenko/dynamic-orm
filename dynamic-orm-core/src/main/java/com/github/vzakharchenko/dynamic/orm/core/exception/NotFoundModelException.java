package com.github.vzakharchenko.dynamic.orm.core.exception;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 13.04.15
 * Time: 20:52
 */
public class NotFoundModelException extends ModelFromRawModelTypeException {

    public NotFoundModelException() {
        super();
    }

    public NotFoundModelException(String message) {
        super(message);
    }

    public NotFoundModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundModelException(Throwable cause) {
        super(cause);
    }
}
