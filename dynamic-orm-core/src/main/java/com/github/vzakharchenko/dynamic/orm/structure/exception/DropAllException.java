package com.github.vzakharchenko.dynamic.orm.structure.exception;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 02.08.14
 * Time: 15:24
 */
public class DropAllException extends Exception {

    public DropAllException() {
    }

    public DropAllException(String message) {
        super(message);
    }

    public DropAllException(String message, Throwable cause) {
        super(message, cause);
    }

    public DropAllException(Throwable cause) {
        super(cause);
    }
}
