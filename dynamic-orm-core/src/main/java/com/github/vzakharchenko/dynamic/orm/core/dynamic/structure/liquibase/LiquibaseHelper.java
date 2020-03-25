package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase;

import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Relation;

import java.util.Optional;

public final class LiquibaseHelper {
    private LiquibaseHelper() {
    }

    public static Relation getRelation(DatabaseObject databaseObject) {
        return Optional.ofNullable(getSimpleRelation(databaseObject)).or(() ->
                Optional.ofNullable(getForeignRelation(databaseObject)).or(()
                        -> Optional.ofNullable(getIndexRelation(databaseObject)).or(
                        Optional::empty))).orElse(null);
    }

    private static Relation getForeignRelation(DatabaseObject databaseObject) {
        return databaseObject.getAttribute("foreignKeyTable", Relation.class);
    }

    private static Relation getIndexRelation(DatabaseObject databaseObject) {
        return databaseObject.getAttribute("table", Relation.class);
    }

    private static Relation getSimpleRelation(DatabaseObject databaseObject) {
        return databaseObject.getAttribute("relation", Relation.class);
    }
}
