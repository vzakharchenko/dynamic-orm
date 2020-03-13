package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase.change;

import liquibase.structure.core.Column;
import liquibase.structure.core.Table;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class ChangeMissedColumnTest {
    @Test
    public void testChangeMissedColumn() {
        ChangeMissedColumn changeMissedColumn = new ChangeMissedColumn();
        Column column = new Column();
        column.setNullable(true);
        column.setAttribute("testAttribute", false);
        Column change = changeMissedColumn.change(column);
        assertNotNull(change);
        ChangeMissedDatabaseObject changeMissedDatabaseObject = ChangeDatabaseObjectFactory.changeMissedObject(column);
        assertNotNull(changeMissedDatabaseObject);
        assertNull(ChangeDatabaseObjectFactory.changeMissedObject(new Table()));
    }


}
