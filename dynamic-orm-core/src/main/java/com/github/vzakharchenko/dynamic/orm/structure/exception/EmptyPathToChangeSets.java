package com.github.vzakharchenko.dynamic.orm.structure.exception;

/**
 *
 */
public class EmptyPathToChangeSets extends RuntimeException {
    public static final String PATH_TO_CHANGE_SETS_IS_EMPTY = "pathToChangeSets is Empty";

    public EmptyPathToChangeSets() {
        super(PATH_TO_CHANGE_SETS_IS_EMPTY);
    }
}
