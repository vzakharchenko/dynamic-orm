package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase;

import liquibase.database.Database;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Table;
import org.apache.commons.collections.CollectionUtils;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public abstract class DynamicDatabaseSnapshotFactory {

    public static DynamicDatabaseSnapshot build(
            Database databaseType, Collection<QDynamicTable> dynamicTables) {
        try {
            Set<Table> tables = dynamicTables.stream().map(TableFactory::createTable)
                    .collect(Collectors.toSet());
            return createDatabaseObject(databaseType, tables);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    private static DynamicDatabaseSnapshot createDatabaseObject(
            Database databaseType, Collection<Table> tables) {
        try {
            DynamicDatabaseSnapshot databaseSnapshot = new DynamicDatabaseSnapshot(
                    databaseType.getClass());
            for (Table table : tables) {
                databaseSnapshot.addDatabaseObject(table);
                databaseSnapshot.addDatabaseObject(table.getPrimaryKey());
                addDatabaseObjects(databaseSnapshot, table.getIndexes());
                addDatabaseObjects(databaseSnapshot, table.getOutgoingForeignKeys());
                addDatabaseObjects(databaseSnapshot, table.getUniqueConstraints());
                addDatabaseObjects(databaseSnapshot, table.getColumns());
                //databaseSnapshot.addDatabaseObject(table.getSchema());
            }
            return databaseSnapshot;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void addDatabaseObjects(DynamicDatabaseSnapshot databaseSnapshot,
                                           List<? extends DatabaseObject> databaseObjects) {
        if (CollectionUtils.isNotEmpty(databaseObjects)) {
            databaseObjects.forEach(databaseSnapshot::addDatabaseObject);
        }

    }
}
