package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.LiquibaseHolder;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.snapshot.InvalidExampleException;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Sequence;
import liquibase.structure.core.Table;
import liquibase.structure.core.View;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 *
 */
public abstract class DynamicDatabaseSnapshotFactory {

    public static DynamicDatabaseSnapshot build(
            Database databaseType, LiquibaseHolder liquibaseHolder) {
        return createDatabaseObject(databaseType, liquibaseHolder);
    }

    private static void addTables(DynamicDatabaseSnapshot databaseSnapshot,
                                  List<Table> tables) {
        for (Table table : tables) {
            databaseSnapshot.addDatabaseObject(table);
            databaseSnapshot.addDatabaseObject(table.getPrimaryKey());
            addDatabaseObjects(databaseSnapshot, table.getIndexes());
            addDatabaseObjects(databaseSnapshot, table.getOutgoingForeignKeys());
            addDatabaseObjects(databaseSnapshot, table.getUniqueConstraints());
            addDatabaseObjects(databaseSnapshot, table.getColumns());
            //databaseSnapshot.addDatabaseObject(table.getSchema());
        }
    }

    private static void addSequences(DynamicDatabaseSnapshot databaseSnapshot,
                                     List<Sequence> sequences) {
        for (Sequence sequence : sequences) {
            databaseSnapshot.addDatabaseObject(sequence);
        }
    }

    private static void addViews(DynamicDatabaseSnapshot databaseSnapshot,
                                 List<View> views) {
        for (View view : views) {
            databaseSnapshot.addDatabaseObject(view);
        }
    }

    private static DynamicDatabaseSnapshot createDatabaseObject0(
            Database databaseType, LiquibaseHolder liquibaseHolder)
            throws NoSuchMethodException, IllegalAccessException,
            InstantiationException, DatabaseException,
            InvalidExampleException, InvocationTargetException {
        DynamicDatabaseSnapshot databaseSnapshot = new DynamicDatabaseSnapshot(
                databaseType.getClass());
        addTables(databaseSnapshot, liquibaseHolder.getTables());
        addSequences(databaseSnapshot, liquibaseHolder.getSequencies());
        addViews(databaseSnapshot, liquibaseHolder.getViews());
        return databaseSnapshot;
    }

    private static DynamicDatabaseSnapshot createDatabaseObject(
            Database databaseType, LiquibaseHolder liquibaseHolder) {
        try {
            return createDatabaseObject0(databaseType, liquibaseHolder);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static void addDatabaseObjects(DynamicDatabaseSnapshot databaseSnapshot,
                                           List<? extends DatabaseObject> databaseObjects) {
        if (CollectionUtils.isNotEmpty(databaseObjects)) {
            databaseObjects.forEach(databaseSnapshot::addDatabaseObject);
        }

    }
}
