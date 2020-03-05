package com.github.vzakharchenko.dynamic.orm.structure;

import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.exception.LiquibaseException;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.precondition.Precondition;
import liquibase.precondition.core.PreconditionContainer;
import liquibase.resource.ResourceAccessor;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by vzakharchenko on 01.08.14.
 */
public class DatabaseChangeLogReaderImpl implements DatabaseChangeLogReader {

    private final ResourceAccessor resourceAccessor;
    private final ChangeLogParameters changeLogParameters;

    public DatabaseChangeLogReaderImpl(ResourceAccessor resourceAccessor, Database database) {
        this.resourceAccessor = resourceAccessor;
        this.changeLogParameters = new ChangeLogParameters(database);
    }

    @Override
    public void read(DatabaseChangeLog databaseChangeLog, String path,
                     Comparator<String> comporator)
            throws IOException, LiquibaseException {
        Collection<String> unsortedResources = resourceAccessor
                .list(path, path, true, false, false);
        if (CollectionUtils.isNotEmpty(unsortedResources)) {
            Collection<String> resources = new TreeSet<>(comporator);
            resources.addAll(unsortedResources);
            for (String file : resources) {
                DatabaseChangeLog changeLog = ChangeLogParserFactory.getInstance()
                        .getParser(file, resourceAccessor)
                        .parse(file, changeLogParameters, resourceAccessor);
                PreconditionContainer preconditions = changeLog.getPreconditions();
                List<Precondition> nestedPreconditions = preconditions.getNestedPreconditions();
                databaseChangeLog.getChangeSets().addAll(changeLog.getChangeSets());
                if (CollectionUtils.isNotEmpty(nestedPreconditions)) {
                    for (Precondition precondition : nestedPreconditions) {
                        databaseChangeLog.getPreconditions().addNestedPrecondition(precondition);
                    }
                }
            }
        }
    }
}
