package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase.change;

import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Column;

/**
 * Created by vzakharchenko on 20.10.14.
 */
public abstract class ChangeDatabaseObjectFactory {
    private static final ChangeMissedColumn CHANGE_MISSED_COLUMN = new ChangeMissedColumn();

    public static ChangeMissedDatabaseObject changeMissedObject(DatabaseObject databaseObject) {
        if (databaseObject instanceof Column) {
            return CHANGE_MISSED_COLUMN;
        }
        return null;
    }
}
