package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.DebugAnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.querydsl.core.QueryException;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class DynamicCompositeKeyTest extends DebugAnnotationTestQueryOrm {

    public void createSchema() {
        // create Database schema
        qDynamicTableFactory
                .buildTables("testDynamicTableWithCompositeKey")
                .addColumns().addNumberColumn("id1", Integer.class)
                .useAsPrimaryKey().create()
                .addStringColumn("id2").size(255).useAsPrimaryKey().create()
                .addStringColumn("testColumn").size(255).create()
                .finish().finish().buildSchema();
    }

    public void createSchemaRelatedTable() {
        // update Database schema
        qDynamicTableFactory
                .buildTables("testDynamicTableRelated")
                .addColumns().addNumberColumn("id", Integer.class)
                .useAsPrimaryKey().create()
                .addNumberColumn("id1", Integer.class).notNull().create()
                .addStringColumn("id2").size(255).notNull().create()
                .addStringColumn("testColumn").size(255).create()
                .finish()
                .addForeignKey("id1", "id2").buildForeignKey("testDynamicTableWithCompositeKey")
                .finish()
                .buildSchema();
    }

    public void createSchemaRelatedTable2() {
        // update Database schema
        qDynamicTableFactory
                .buildTables("testDynamicTableRelated")
                .addColumns().addNumberColumn("id", Integer.class)
                .useAsPrimaryKey().create()
                .addNumberColumn("id1", Integer.class).notNull().create()
                .addStringColumn("id2").size(255).notNull().create()
                .addStringColumn("testColumn").size(255).create()
                .finish()
                .addForeignKey("id1", "id2").buildForeignKey("testDynamicTableWithCompositeKey","id1", "id2")
                .finish()
                .buildSchema();
    }


    @Test
    public void compositeKeyPrivateKeyTest() {
        createSchema();
        // get dynamic table
        QDynamicTable table = qDynamicTableFactory
                .getQDynamicTableByName("testDynamicTableWithCompositeKey");

        // insert Data
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        dynamicTableModel.addColumnValue("Id1", 1);
        dynamicTableModel.addColumnValue("Id2", "2");
        dynamicTableModel.addColumnValue("testColumn", "test");
        ormQueryFactory.insert(dynamicTableModel);

        List<DynamicTableModel> models = ormQueryFactory.selectCache().findAll(table);
        assertNotNull(models);
        assertEquals(models.size(), 1);
        assertEquals(models.get(0).getValue("Id1", Integer.class), Integer.valueOf(1));
        assertEquals(models.get(0).getValue("Id2", String.class), "2");
        assertEquals(models.get(0).getValue("testColumn", String.class), "test");
    }


    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "model for TESTDYNAMICTABLEWITHCOMPOSITEKEY has empty Primary Key : class com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel")
    public void compositeKeyPrivateKeyPartDataTest1() {
        // create Database schema
        createSchema();
        // get dynamic table
        QDynamicTable table = qDynamicTableFactory
                .getQDynamicTableByName("testDynamicTableWithCompositeKey");

        // insert Data
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        dynamicTableModel.addColumnValue("Id2", "2");
        dynamicTableModel.addColumnValue("testColumn", "test");
        ormQueryFactory.insert(dynamicTableModel);

    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "model for TESTDYNAMICTABLEWITHCOMPOSITEKEY has empty Primary Key : class com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel")
    public void compositeKeyPrivateKeyPartDataTest2() {
        // create Database schema
        createSchema();
        // get dynamic table
        QDynamicTable table = qDynamicTableFactory
                .getQDynamicTableByName("testDynamicTableWithCompositeKey");

        // insert Data
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        dynamicTableModel.addColumnValue("Id1", 1);
        dynamicTableModel.addColumnValue("testColumn", "test");
        ormQueryFactory.insert(dynamicTableModel);
    }


    @Test
    public void compositeKeyForeignKeyTest() {
        createSchema();
        // get dynamic table
        QDynamicTable table = qDynamicTableFactory
                .getQDynamicTableByName("testDynamicTableWithCompositeKey");

        // insert Data
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        dynamicTableModel.addColumnValue("Id1", 1);
        dynamicTableModel.addColumnValue("Id2", "2");
        dynamicTableModel.addColumnValue("testColumn", "test");
        ormQueryFactory.insert(dynamicTableModel);

        List<DynamicTableModel> models = ormQueryFactory.select().findAll(table);
        assertNotNull(models);
        assertEquals(models.size(), 1);
        assertEquals(models.get(0).getValue("Id1", Integer.class), Integer.valueOf(1));
        assertEquals(models.get(0).getValue("Id2", String.class), "2");
        assertEquals(models.get(0).getValue("testColumn", String.class), "test");
    }

    @Test
    public void compositeForeignKeyTest() {
        createSchema();
        createSchemaRelatedTable();
        // get dynamic table
        QDynamicTable table1 = qDynamicTableFactory
                .getQDynamicTableByName("testDynamicTableWithCompositeKey");
        QDynamicTable table2 = qDynamicTableFactory
                .getQDynamicTableByName("testDynamicTableRelated");

        // insert Data1
        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(table1);
        dynamicTableModel1.addColumnValue("Id1", 1);
        dynamicTableModel1.addColumnValue("Id2", "2");
        dynamicTableModel1.addColumnValue("testColumn", "test");
        ormQueryFactory.insert(dynamicTableModel1);
        // insert Data1
        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(table2);
        dynamicTableModel2.addColumnValue("Id", 0);
        dynamicTableModel2.addColumnValue("Id1", 1);
        dynamicTableModel2.addColumnValue("Id2", "2");
        dynamicTableModel2.addColumnValue("testColumn", "test");
        ormQueryFactory.insert(dynamicTableModel2);


        List<DynamicTableModel> models = ormQueryFactory.select().findAll(table1);
        assertNotNull(models);
        assertEquals(models.size(), 1);
        assertEquals(models.get(0).getValue("Id1", Integer.class), Integer.valueOf(1));
        assertEquals(models.get(0).getValue("Id2", String.class), "2");
        assertEquals(models.get(0).getValue("testColumn", String.class), "test");
    }

    @Test
    public void compositeForeignKeyTest2() {
        createSchema();
        createSchemaRelatedTable2();
        // get dynamic table
        QDynamicTable table1 = qDynamicTableFactory
                .getQDynamicTableByName("testDynamicTableWithCompositeKey");
        QDynamicTable table2 = qDynamicTableFactory
                .getQDynamicTableByName("testDynamicTableRelated");

        // insert Data1
        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(table1);
        dynamicTableModel1.addColumnValue("Id1", 1);
        dynamicTableModel1.addColumnValue("Id2", "2");
        dynamicTableModel1.addColumnValue("testColumn", "test");
        ormQueryFactory.insert(dynamicTableModel1);
        // insert Data1
        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(table2);
        dynamicTableModel2.addColumnValue("Id", 0);
        dynamicTableModel2.addColumnValue("Id1", 1);
        dynamicTableModel2.addColumnValue("Id2", "2");
        dynamicTableModel2.addColumnValue("testColumn", "test");
        ormQueryFactory.insert(dynamicTableModel2);


        List<DynamicTableModel> models = ormQueryFactory.select().findAll(table1);
        assertNotNull(models);
        assertEquals(models.size(), 1);
        assertEquals(models.get(0).getValue("Id1", Integer.class), Integer.valueOf(1));
        assertEquals(models.get(0).getValue("Id2", String.class), "2");
        assertEquals(models.get(0).getValue("testColumn", String.class), "test");
    }

    @Test(expectedExceptions = QueryException.class)
    public void compositeForeignKeyTestFail() {
        createSchema();
        createSchemaRelatedTable();
        // get dynamic table
        QDynamicTable table1 = qDynamicTableFactory
                .getQDynamicTableByName("testDynamicTableWithCompositeKey");
        QDynamicTable table2 = qDynamicTableFactory
                .getQDynamicTableByName("testDynamicTableRelated");

        // insert Data1
        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(table1);
        dynamicTableModel1.addColumnValue("Id1", 1);
        dynamicTableModel1.addColumnValue("Id2", "2");
        dynamicTableModel1.addColumnValue("testColumn", "test");
        ormQueryFactory.insert(dynamicTableModel1);
        // insert Data1
        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(table2);
        dynamicTableModel2.addColumnValue("Id", 0);
        dynamicTableModel2.addColumnValue("Id1", 2);
        dynamicTableModel2.addColumnValue("Id2", "2");
        dynamicTableModel2.addColumnValue("testColumn", "test");
        ormQueryFactory.insert(dynamicTableModel2);

    }

}
