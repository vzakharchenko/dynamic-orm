package com.github.vzakharchenko.dynamic.orm.structure;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.LiquibaseException;

import java.io.IOException;
import java.util.Comparator;

/**
 * Created by vzakharchenko on 01.08.14.
 */
public interface DatabaseChangeLogReader {

    /**
     * read all ChangeLog xml to databaseChangeLog
     *
     * @param databaseChangeLog databaseChangeLog object
     * @param path              path to the ChangeLog files
     * @param comporator        comporator for compare fileName
     * @throws IOException
     * @throws LiquibaseException
     */
    void read(DatabaseChangeLog databaseChangeLog, String path, Comparator<String> comporator)
            throws IOException, LiquibaseException;
}
