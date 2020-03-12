package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.DebugAnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
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
        DynamicTableModel tableModel = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable, DynamicTableModel.class);
        tableModel.addColumnValue("TestColumn", "newValue");
        ormQueryFactory.updateById(tableModel);
        DynamicTableModel tableModel2 = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable, DynamicTableModel.class);
        assertEquals(tableModel2.getValue("TestColumn", String.class), "newValue");
    }

    @Test
    public void testDeleteDynamic() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel);
        DynamicTableModel tableModel = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable, DynamicTableModel.class);
        ormQueryFactory.deleteById(tableModel);
        assertNull(ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable, DynamicTableModel.class));
    }

    @Test
    public void testUpdatePartById() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel);
        DynamicTableModel tableModel = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable, DynamicTableModel.class);
        ormQueryFactory.modify(dynamicTable, DynamicTableModel.class)
                .updateBuilder()
                .set(dynamicTable.getStringColumnByName("TestColumn"), "newValue")
                .set(dynamicTable.getStringColumnByName("Id"), dynamicTableModel.getValue("Id", String.class))
                .byId().update();

        DynamicTableModel tableModel2 = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable, DynamicTableModel.class);
        assertEquals(tableModel2.getValue("TestColumn", String.class), "newValue");
    }

    @Test
    public void testUpdatePartWhere() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel);
        DynamicTableModel tableModel = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable, DynamicTableModel.class);
        ormQueryFactory.modify(dynamicTable, DynamicTableModel.class)
                .updateBuilder()
                .set(dynamicTable.getStringColumnByName("TestColumn"), "newValue")
                .set(dynamicTable.getStringColumnByName("Id"), dynamicTableModel.getValue("Id", String.class))
                .where(dynamicTable.getStringColumnByName("TestColumn").eq("testData")).update();

        DynamicTableModel tableModel2 = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable, DynamicTableModel.class);
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
        ormQueryFactory.modify(dynamicTable, DynamicTableModel.class)
                .updateBuilder()
                .set(dynamicTable.getStringColumnByName("TestColumn"), "newValue")
                .where(dynamicTable.getStringColumnByName("TestColumn").eq("testData")).update();

        DynamicTableModel tableModel2 = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable, DynamicTableModel.class);
        assertEquals(tableModel2.getValue("TestColumn", String.class), "newValue");
    }

}
