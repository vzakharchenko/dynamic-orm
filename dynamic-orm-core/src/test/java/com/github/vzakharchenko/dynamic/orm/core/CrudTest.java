package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.DebugAnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKeyBuilder;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.UpdateModelBuilder;
import com.github.vzakharchenko.dynamic.orm.model.TestTableVersionAnnotation;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersionAnnotation;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class CrudTest extends DebugAnnotationTestQueryOrm {

    @BeforeMethod
    public void beforeMethod() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns().addStringColumn("Id").size(255).useAsPrimaryKey().createColumn()
                .addDateTimeColumn("modificationTime").notNull().createColumn()
                .addStringColumn("TestColumn").size(255).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .addVersionColumn("modificationTime").endBuildTables().buildSchema();
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
        assertTrue(PrimaryKeyHelper.isOneOfPrimaryKey(dynamicTable.getStringColumnByName("Id")));
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
    public void testDeleteDynamicCompositeIds() {
        qDynamicTableFactory.buildTables("DynamicTable2")
                .columns()
                .addStringColumn("Id1").size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("Id2").size(255).useAsPrimaryKey().createColumn()
                .addDateTimeColumn("modificationTime").notNull().createColumn()
                .addStringColumn("TestColumn").size(255).createColumn()
                .endColumns()
                .addVersionColumn("modificationTime").endBuildTables().buildSchema();

        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable2");
        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(dynamicTable);
        dynamicTableModel1.addColumnValue("Id1", "10");
        dynamicTableModel1.addColumnValue("Id2", "11");
        dynamicTableModel1.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel1);

        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(dynamicTable);
        dynamicTableModel2.addColumnValue("Id1", "20");
        dynamicTableModel2.addColumnValue("Id2", "21");
        dynamicTableModel2.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel2);
        ormQueryFactory.modify(dynamicTable).deleteByIds(
                CompositeKeyBuilder.create(dynamicTable)
                        .addPrimaryKey("Id1","10")
                        .addPrimaryKey("Id2","11").build(),
                CompositeKeyBuilder.create(dynamicTable)
                        .addPrimaryKey("Id1","20")
                        .addPrimaryKey("Id2","21").build()

        );
        assertNull(ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable));
    }
    @Test
    public void testSoftDeleteDynamicCompositeIds() {
        qDynamicTableFactory.buildTables("DynamicTable2")
                .columns()
                .addStringColumn("Id1").size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("Id2").size(255).useAsPrimaryKey().createColumn()
                .addDateTimeColumn("modificationTime").notNull().createColumn()
                .addBooleanColumn("softDelete").createColumn()
                .addStringColumn("TestColumn").size(255).createColumn()
                .endColumns()
                .addSoftDeleteColumn("softDelete",true,false)
                .addVersionColumn("modificationTime").endBuildTables().buildSchema();

        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable2");
        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(dynamicTable);
        dynamicTableModel1.addColumnValue("Id1", "10");
        dynamicTableModel1.addColumnValue("Id2", "11");
        dynamicTableModel1.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel1);

        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(dynamicTable);
        dynamicTableModel2.addColumnValue("Id1", "20");
        dynamicTableModel2.addColumnValue("Id2", "21");
        dynamicTableModel2.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel2);
        ormQueryFactory.modify(dynamicTable).softDeleteByIds(
                CompositeKeyBuilder.create(dynamicTable)
                        .addPrimaryKey("Id1","10")
                        .addPrimaryKey("Id2","11").build(),
                CompositeKeyBuilder.create(dynamicTable)
                        .addPrimaryKey("Id1","20")
                        .addPrimaryKey("Id2","21").build()

        );
        assertNull(ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), dynamicTable));
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
    public void testUpdateBuilderPartById() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel);
        UpdateModelBuilder<DynamicTableModel> builder = ormQueryFactory.modify(dynamicTable)
                .updateBuilder();
        PrimaryKeyHelper.updateModelBuilder(builder, dynamicTable, dynamicTableModel.getValue("Id", String.class));
        builder
                .set(dynamicTable.getStringColumnByName("TestColumn"), "newValue")
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
                .columns()
                .addDateTimeColumn("modificationTime").notNull().createColumn()
                .addStringColumn("TestColumn").size(255).createColumn()
                .endColumns()
                .addVersionColumn("modificationTime")
                .endBuildTables()
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
                .columns()
                .addDateTimeColumn("modificationTime").notNull().createColumn()
                .addStringColumn("TestColumn").size(255).createColumn()
                .endColumns()
                .addVersionColumn("modificationTime")
                .endBuildTables()
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
                .columns().addStringColumn(null).createColumn();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed2() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .columns().addDateTimeColumn(null).createColumn();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed3() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .columns().addNumberColumn(null, null).createColumn();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed4() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .columns().addNumberColumn("dsfs", null).createColumn();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed5() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .columns().addDateColumn(null).createColumn();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed6() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .columns().addDateTimeColumn(null).createColumn();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed7() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .columns().addBooleanColumn(null).createColumn();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed8() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .columns().addClobColumn(null).createColumn();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed9() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .columns().addBlobColumn(null).createColumn();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed10() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .columns().addTimeColumn(null).createColumn();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed11() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .primaryKey().addPrimaryKeyGenerator(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed12() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .primaryKey().addPrimaryKey((String) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFailed13() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .primaryKey().addPrimaryKey((String) null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testAddFailed14() {
        qDynamicTableFactory.buildTables("DynamicTable1")
                .addVersionColumn((String) null);
    }


}
