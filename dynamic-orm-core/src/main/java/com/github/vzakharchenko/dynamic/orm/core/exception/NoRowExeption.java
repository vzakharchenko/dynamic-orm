package com.github.vzakharchenko.dynamic.orm.core.exception;

/**
 *
 */
public class NoRowExeption extends RuntimeException {

    private static final String NO_ROW_AFFECTED_MESSAGE = "No rows were affected.";

    public NoRowExeption() {
        super(NO_ROW_AFFECTED_MESSAGE);
    }

}
