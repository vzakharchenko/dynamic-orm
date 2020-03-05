package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase.change;

/**
 * Created by vzakharchenko on 20.10.14.
 */
public interface ChangeMissedDatabaseObject<DATABASEOBJECT
        extends liquibase.structure.DatabaseObject> {

    DATABASEOBJECT change(DATABASEOBJECT databaseobject);
}
