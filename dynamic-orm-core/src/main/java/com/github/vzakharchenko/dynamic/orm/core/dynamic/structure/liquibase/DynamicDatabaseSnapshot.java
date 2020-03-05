package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase.change.ChangeDatabaseObjectFactory;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase.change.ChangeMissedDatabaseObject;
import liquibase.database.Database;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.structure.DatabaseObject;
import liquibase.structure.DatabaseObjectCollection;
import liquibase.structure.core.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class DynamicDatabaseSnapshot extends DatabaseSnapshot {

    protected DynamicDatabaseSnapshot(Class<? extends Database> databaseType) throws Exception {
        super(null, databaseType.newInstance());
    }

    private DatabaseObjectCollection getDatabaseObjectCollection(
            DatabaseSnapshot databaseSnapshot) {
        return (DatabaseObjectCollection) databaseSnapshot.getSerializableFieldValue("objects");
    }

    public DatabaseObjectCollection getDatabaseObjectCollection() {
        return getDatabaseObjectCollection(this);
    }

    public <DATABASEOBJECT extends DatabaseObject> DATABASEOBJECT changeDatabaseForMissedObject(
            DATABASEOBJECT databaseObject, Class<DATABASEOBJECT> databaseObjectClass) {
        ChangeMissedDatabaseObject changeMissedDatabaseObject = ChangeDatabaseObjectFactory
                .changeMissedObject(databaseObject);
        if (changeMissedDatabaseObject != null) {
            return (DATABASEOBJECT) changeMissedDatabaseObject.change(databaseObject);
        }
        return null;
    }

    public void addDatabaseObject(DatabaseObject databaseObject) {
        getDatabaseObjectCollection().add(databaseObject);
    }

    public void addTableWithColumn(Table table) {
        addDatabaseObject(table);
        table.getColumns().forEach(this::addDatabaseObject);
    }

    public void mergedDatabase(DatabaseSnapshot referenceDatabaseSnapshot) {
        DatabaseObjectCollection referenceObjectCollection =
                getDatabaseObjectCollection(referenceDatabaseSnapshot);
        mergedDynamicTables(referenceObjectCollection);
        mergedDatabase(referenceObjectCollection, Table.class, true);
        mergedDatabase(referenceObjectCollection, ForeignKey.class, true);
        mergedDatabase(referenceObjectCollection, PrimaryKey.class, true);
        mergedDatabase(referenceObjectCollection, Index.class, false);
        mergedDatabase(referenceObjectCollection, Sequence.class, false);
        mergedDatabase(referenceObjectCollection, StoredProcedure.class, false);
        mergedDatabase(referenceObjectCollection, UniqueConstraint.class, false);
        mergedDatabase(referenceObjectCollection, View.class, true);
        mergedDatabase(referenceObjectCollection, Column.class, false);
        mergedDatabase(referenceObjectCollection, Catalog.class, false);
        mergedDatabase(referenceObjectCollection, Schema.class, false);
    }

    private void mergedDatabase(DatabaseObjectCollection referenceObjectCollection,
                                Class<? extends DatabaseObject> databaseObjectClass,
                                boolean setSnapshotId) {
        Set<? extends DatabaseObject> databaseObjects = referenceObjectCollection
                .get(databaseObjectClass);
        DatabaseObjectCollection databaseObjectCollection = getDatabaseObjectCollection();
        for (DatabaseObject databaseObject : databaseObjects) {
            //first set snapshotID and init Tables
            if (setSnapshotId) {
                setInitNewObjectByOldObject(databaseObject);
            }

            if (!databaseObjectCollection.contains(databaseObject, null)) {
                addDatabaseObject(databaseObject);
            }
        }
    }

    private void mergedDynamicTables(DatabaseObjectCollection referenceObjectCollection) {
        Map<Table, Table> referenceTables = convertCollectionToMap(referenceObjectCollection
                .get(Table.class), Table.class);
        DatabaseObjectCollection databaseObjectCollection = getDatabaseObjectCollection();
        Set<Table> generatedTables = databaseObjectCollection.get(Table.class);
        for (Table generatedTable : generatedTables) {
            Table table = getTableFromMap(generatedTable, referenceTables);
            if (table != null) {
                diffColumn(table, generatedTable);
            }
            addTableWithColumn(generatedTable);
        }
    }

    private Table getTableFromMap(Table table, Map<Table, Table> referenceTables) {
        for (Table t : referenceTables.keySet()) {
            if (StringUtils.equalsIgnoreCase(t.getName(), table.getName())) {
                return t;
            }
        }
        return null;
    }

    private void diffColumn(Table referenceTable, Table generatedTable) {
        for (Column column : generatedTable.getColumns()) {
            column.setRelation(referenceTable);
        }
    }

    protected void setInitNewObjectByOldObject(DatabaseObject databaseObjectOld) {
        Set<DatabaseObject> databaseObjectSet = (Set<DatabaseObject>) getDatabaseObjectCollection()
                .get(databaseObjectOld.getClass());
        databaseObjectSet.stream().filter(databaseObjectNew -> Objects
                .equals(databaseObjectNew, databaseObjectOld)).forEach(databaseObjectNew -> {
            if (databaseObjectNew.getSnapshotId() == null) {
                databaseObjectNew.setSnapshotId(databaseObjectOld.getSnapshotId());
            }
            if (databaseObjectNew instanceof Relation) {
                initTable((Relation) databaseObjectOld, (Relation) databaseObjectNew);
            }
        });
    }


    public void initTable(Relation oldTable, Relation newTable) {
        newTable.setSchema(oldTable.getSchema());
    }

    public <T> Map<T, T> convertCollectionToMap(Collection<T> collection, Class<T> tClass) {
        return collection.stream().collect(Collectors.toMap(t -> t, t -> t));
    }
}
