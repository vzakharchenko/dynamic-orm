package com.github.vzakharchenko.dynamic.orm.core.exception;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 13.04.15
 * Time: 18:23
 */
public class ModelFromRawModelTypeException extends Exception {

    public ModelFromRawModelTypeException() {
        super();
    }

    public ModelFromRawModelTypeException(String message) {
        super(message);
    }

    public ModelFromRawModelTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelFromRawModelTypeException(Throwable cause) {
        super(cause);
    }
}
