package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.DebugAnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;
import com.querydsl.core.QueryException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class ModifySchemaTest extends DebugAnnotationTestQueryOrm {

    @BeforeMethod
    public void createDefaultSchema() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns().addStringColumn("Id").size(255).useAsPrimaryKey().createColumn()
                .addDateTimeColumn("modificationTime").notNull().createColumn()
                .addStringColumn("TestColumn").notNull().size(255).createColumn()
                .addNumberColumn("TestColumnNull", Integer.class).nullable().size(255).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .addVersionColumn("modificationTime").endBuildTables().buildSchema();
    }

    @Test
    public void testDeleteColumnModificationSchema() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns()
                .modifyColumn()
                .dropColumns("TestColumn")
                .finish()
                .endColumns()
                .endBuildTables().buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        ormQueryFactory.insert(dynamicTableModel);
    }

    @Test
    public void testDeleteColumnModificationSchema2() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns().dropColumns("TestColumn")
                .endColumns()
                .endBuildTables().buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        ormQueryFactory.insert(dynamicTableModel);
    }

    @Test
    public void testDropDynamicTable() {
        qDynamicTableFactory.dropTableOrView("DynamicTable").buildSchema();
        assertFalse(qDynamicTableFactory.isTableExist("DynamicTable"));
    }

    @Test()
    public void testDropStaticTable() {
        qDynamicTableFactory
                .dropTableOrView("TEST_Delete_TABLE").buildSchema();
    }

    @Test()
    public void testDropSequenceTable() {
        qDynamicTableFactory
                .createSequence("sequence1").addSequence()
                .buildSchema();
        qDynamicTableFactory.dropSequence("sequence1").buildSchema();
    }

    @Test
    public void testColumnModificationSchema() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns()
                .modifyColumn()
                .size("TestColumn", 1)
                .finish()
                .endColumns()
                .endBuildTables()
                .buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        dynamicTableModel.addColumnValue("TestColumn", "1");
        ormQueryFactory.insert(dynamicTableModel);
    }

    @Test(expectedExceptions = QueryException.class)
    public void testColumnModificationSchemaFailed() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns()
                .modifyColumn()
                .size("TestColumn", 1).finish().endColumns().endBuildTables().buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        dynamicTableModel.addColumnValue("TestColumn", "Size bigger than 1");
        ormQueryFactory.insert(dynamicTableModel);
    }

    @Test
    public void testColumnModificationSchemaNullable() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns()
                .modifyColumn()
                .nullable("TestColumn").finish().endColumns().endBuildTables().buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        ormQueryFactory.insert(dynamicTableModel);
    }

    @Test
    public void testColumnModificationSchemaDecimalDigits() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns()
                .modifyColumn()
                .decimalDigits("TestColumnNull", 10)
                .changeNumberType("TestColumnNull", Double.class).finish().endColumns().endBuildTables().buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        dynamicTableModel.addColumnValue("TestColumnNull", 1.1);
        dynamicTableModel.addColumnValue("TestColumn", "22");
        ormQueryFactory.insert(dynamicTableModel);
        List<DynamicTableModel> tableModels = ormQueryFactory.select().findAll(table);
        DynamicTableModel tableModel = tableModels.get(0);
        assertEquals(tableModel.getValue("TestColumnNull", Double.class).doubleValue(), 1.1);
    }

    @Test(expectedExceptions = QueryException.class)
    public void testColumnModificationSchemaNotNull() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns()
                .modifyColumn()
                .notNull("TestColumnNull").finish().endColumns().endBuildTables().buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        ormQueryFactory.insert(dynamicTableModel);
    }
}
