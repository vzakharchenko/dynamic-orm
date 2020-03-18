package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure;

import liquibase.snapshot.DatabaseSnapshot;

/**
 *
 */
public interface DynamicStructureUpdater {
    void update(LiquibaseHolder liquibaseHolder);

    DatabaseSnapshot getDatabaseSnapshot();
}
