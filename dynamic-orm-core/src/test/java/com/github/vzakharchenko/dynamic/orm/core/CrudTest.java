package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.DebugAnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;
import com.github.vzakharchenko.dynamic.orm.model.TestTableVersionAnnotation;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersionAnnotation;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class CrudTest extends DebugAnnotationTestQueryOrm {

    @BeforeMethod
    public void beforeMethod() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .addColumns().addStringColumn("Id").size(255).useAsPrimaryKey().create()
                .addDateTimeColumn("modificationTime").notNull().create()
                .addStringColumn("TestColumn").size(255).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).finish()
                .addVersionColumn("modificationTime").finish().buildSchema();
    }

    @Test
    public void testInsertTest() {
        TestTableVersionAnnotation value = new TestTableVersionAnnotation();
        ormQueryFactory.insert(value);
        assertTrue(ormQueryFactory.select().exist(ormQueryFactory.buildQuery().from(QTestTableVersionAnnotation.qTestTableVersionAnnotation)));
    }

    @Test
    public void testInsertDynamicTest() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        assertTrue(ModelHelper.isPrimaryKey(dynamicTable.getStringColumnByName("Id")));
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel);
        assertTrue(ormQueryFactory.select().exist(ormQueryFactory.buildQuery().from(dynamicTable)));
    }

    @Test
    public void testUpdateIdTest() {
        TestTableVersionAnnotation value = new TestTableVersionAnnotation();
        ormQueryFactory.insert(value);
        TestTableVersionAnnotation tableVersionAnnotation = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), TestTableVersionAnnotation.class);
        tableVersionAnnotation.setVersion(33);
        ormQueryFactory.updateById(tableVersionAnnotation);

    }

    @Test
    public void testUpdateIdDynamicTest() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel);
        DynamicTableModel tableModel = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable);
        tableModel.addColumnValue("TestColumn", "newValue");
        ormQueryFactory.updateById(tableModel);
        DynamicTableModel tableModel2 = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable);
        assertEquals(tableModel2.getValue("TestColumn", String.class), "newValue");
    }

    @Test
    public void testDeleteDynamic() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel);
        DynamicTableModel tableModel = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable);
        ormQueryFactory.deleteById(tableModel);
        assertNull(ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable));
    }

    @Test
    public void testUpdatePartById() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel);
        DynamicTableModel tableModel = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable);
        ormQueryFactory.modify(dynamicTable)
                .updateBuilder()
                .set(dynamicTable.getStringColumnByName("TestColumn"), "newValue")
                .set(dynamicTable.getStringColumnByName("Id"), dynamicTableModel.getValue("Id", String.class))
                .byId().update();

        DynamicTableModel tableModel2 = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable);
        assertEquals(tableModel2.getValue("TestColumn", String.class), "newValue");
    }

    @Test
    public void testUpdatePartWhere() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel);
        DynamicTableModel tableModel = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable);
        ormQueryFactory.modify(dynamicTable)
                .updateBuilder()
                .set(dynamicTable.getStringColumnByName("TestColumn"), "newValue")
                .set(dynamicTable.getStringColumnByName("Id"), dynamicTableModel.getValue("Id", String.class))
                .where(dynamicTable.getStringColumnByName("TestColumn").eq("testData")).update();

        DynamicTableModel tableModel2 = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable);
        assertEquals(tableModel2.getValue("TestColumn", String.class), "newValue");
    }

    @Test
    public void testUpdatePartWhereWithoutPrimaryKey() {

        qDynamicTableFactory.buildTables("DynamicTable2")
                .addColumns()
                .addDateTimeColumn("modificationTime").notNull().create()
                .addStringColumn("TestColumn").size(255).create()
                .finish()
                .addVersionColumn("modificationTime")
                .finish()
                .buildSchema();

        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable2");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel);
        ormQueryFactory.modify(dynamicTable)
                .updateBuilder()
                .set(dynamicTable.getStringColumnByName("TestColumn"), "newValue")
                .where(dynamicTable.getStringColumnByName("TestColumn").eq("testData")).update();

        DynamicTableModel tableModel2 = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable);
        assertEquals(tableModel2.getValue("TestColumn", String.class), "newValue");
    }


    @Test
    public void testDeleteWithoutPrimaryKey() {

        qDynamicTableFactory.buildTables("DynamicTable2")
                .addColumns()
                .addDateTimeColumn("modificationTime").notNull().create()
                .addStringColumn("TestColumn").size(255).create()
                .finish()
                .addVersionColumn("modificationTime")
                .finish()
                .buildSchema();

        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable2");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel);
        ormQueryFactory.modify(dynamicTable)
                .delete(dynamicTableModel).delete();

        DynamicTableModel tableModel2 = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable);
        assertNull(tableModel2);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetColumnTypeFailed() {


        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        dynamicTable.getNumberColumnByName("Id");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetColumnFailed() {


        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        dynamicTable.getNumberColumnByName("NotExist");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed1() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .addColumns().addStringColumn(null).create();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed2() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .addColumns().addDateTimeColumn(null).create();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed3() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .addColumns().addNumberColumn(null, null).create();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed4() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .addColumns().addNumberColumn("dsfs", null).create();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed5() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .addColumns().addDateColumn(null).create();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed6() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .addColumns().addDateTimeColumn(null).create();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed7() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .addColumns().addBooleanColumn(null).create();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed8() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .addColumns().addClobColumn(null).create();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed9() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .addColumns().addBlobColumn(null).create();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed10() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .addColumns().addTimeColumn(null).create();
    }
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed11() {
        qDynamicTableFactory.buildTables("DynamicTable")
               .addPrimaryKey().addPrimaryKeyGenerator(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed12() {
        qDynamicTableFactory.buildTables("DynamicTable1")
               .addPrimaryKey().addPrimaryKey((String) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed13() {
        qDynamicTableFactory.buildTables("DynamicTable1")
               .addPrimaryKey().addPrimaryKey((String) null);
    }
    @Test(expectedExceptions = IllegalStateException.class)
    public void testAddFailed14() {
        qDynamicTableFactory.buildTables("DynamicTable1")
               .addVersionColumn((String) null);
    }


}
