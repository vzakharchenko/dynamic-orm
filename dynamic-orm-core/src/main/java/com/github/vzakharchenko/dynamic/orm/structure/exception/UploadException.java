package com.github.vzakharchenko.dynamic.orm.structure.exception;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 02.08.14
 * Time: 12:50
 */
public class UploadException extends Exception {

    public UploadException() {
        super();
    }

    public UploadException(String message) {
        super(message);
    }

    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadException(Throwable cause) {
        super(cause);
    }
}
