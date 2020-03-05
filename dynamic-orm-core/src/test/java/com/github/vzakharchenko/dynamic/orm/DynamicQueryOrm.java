package com.github.vzakharchenko.dynamic.orm;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGeneratorInteger;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.CrudBuilder;
import com.github.vzakharchenko.dynamic.orm.qModel.QTesttable;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;


/**
 *
 */
public class DynamicQueryOrm extends OracleTestQueryOrm {

    @Test
    public void testBuildOneTable() {
        QTableBuilder testTable1 = qDynamicTableFactory.buildTable("dynamicTestTable1");
        // build primary key
        testTable1.createNumberColumn("ID", Integer.class, 18, 0, true).addPrimaryKey("ID");

        // build String Field

        testTable1.createStringColumn("STRING_Test_FIELD", 200, false);

        // build foreign Key to table testtable with Primary key ID
        NumberPath<Integer> id = QTesttable.testtable.id;
        testTable1
                .createNumberColumn("Test_FK", id.getType(), ModelHelper.getColumnSize(id), ModelHelper.getColumnDigitSize(id), false)
                .addForeignKey("Test_FK", QTesttable.testtable, id);

        // validate and create structure
        testTable1.buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable1");
        assertNotNull(qDynamicTable);
        List<Path<?>> columns = qDynamicTable.getColumns();
        assertEquals(columns.size(), 3);

        assertTrue(ModelHelper.hasPrimaryKey(qDynamicTable));

        NumberPath<Integer> pk = qDynamicTable.getNumberColumnByName("ID", Integer.class);
        assertEquals(ModelHelper.getPrimaryKeyColumn(qDynamicTable), pk);
        assertNotNull(qDynamicTable.getStringColumnByName("STRING_Test_FIELD"));
        assertEquals(qDynamicTable.getStringColumnByName("STRING_Test_FIELD"), ModelHelper.getColumnByName(qDynamicTable, "STRING_Test_FIELD"));
        assertNotNull(qDynamicTable.getNumberColumnByName("Test_FK", id.getType()));
    }

    @Test
    public void testBuildBatchTable() {
        QTableBuilder testTable1 = qDynamicTableFactory.buildTable("dynamicTestTable1");
        // build primary key
        testTable1.createNumberColumn("ID", Integer.class, 18, 0, true).addPrimaryKey("ID");

        // build String Field

        testTable1.createStringColumn("STRING_Test_FIELD", 200, false);

        // build foreign Key to table testtable with Primary key ID
        NumberPath<Integer> id = QTesttable.testtable.id;
        testTable1
                .createNumberColumn("Test_FK", id.getType(), ModelHelper.getColumnSize(id), ModelHelper.getColumnDigitSize(id), false)
                .addForeignKey("Test_FK", QTesttable.testtable, id);

        // build next table

        QTableBuilder testTable2 = testTable1.buildNextTable("dynamicTestTable2");
        // build primary key for table testTable2
        testTable2.createStringColumn("ID", 18, true).addPrimaryKey("ID");

        testTable2.createDateColumn("dateColumn", true);

        testTable2.createNumberColumn("testTable1_FK", Integer.class, 18, 0, true).addForeignKey("testTable1_FK", "dynamicTestTable1");

        testTable2.buildSchema();
        QDynamicTable qDynamicTable1 = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable1");
        QDynamicTable qDynamicTable2 = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable2");

        assertNotNull(qDynamicTable1);
        assertNotNull(qDynamicTable2);
        List<Path<?>> columns1 = qDynamicTable1.getColumns();
        assertEquals(columns1.size(), 3);
        List<Path<?>> columns2 = qDynamicTable1.getColumns();
        assertEquals(columns2.size(), 3);

        assertTrue(ModelHelper.hasPrimaryKey(qDynamicTable1));
        assertTrue(ModelHelper.hasPrimaryKey(qDynamicTable2));

        NumberPath<Integer> pk1 = qDynamicTable1.getNumberColumnByName("ID", Integer.class);
        StringPath pk2 = qDynamicTable2.getStringColumnByName("ID");
        assertEquals(ModelHelper.getPrimaryKeyColumn(qDynamicTable1), pk1);
        assertEquals(ModelHelper.getPrimaryKeyColumn(qDynamicTable2), pk2);
        assertNotEquals(ModelHelper.getPrimaryKeyColumn(qDynamicTable2), pk1);
        assertNotEquals(ModelHelper.getPrimaryKeyColumn(qDynamicTable1), pk2);

        assertNotNull(qDynamicTable1.getStringColumnByName("STRING_Test_FIELD"));
        assertEquals(qDynamicTable1.getStringColumnByName("STRING_Test_FIELD"), ModelHelper.getColumnByName(qDynamicTable1, "STRING_Test_FIELD"));
        assertNotNull(qDynamicTable1.getNumberColumnByName("Test_FK", id.getType()));

        assertNotNull(qDynamicTable2.getDateColumnByName("dateColumn"));
        assertEquals(qDynamicTable2.getDateColumnByName("dateColumn"), ModelHelper.getColumnByName(qDynamicTable2, "dateColumn"));
        assertNotNull(qDynamicTable2.getNumberColumnByName("testTable1_FK", ModelHelper.getPrimaryKey(qDynamicTable1).getType()));
    }

    @Test
    public void testInsertToTable() {
        QTableBuilder testTable1 = qDynamicTableFactory.buildTable("dynamicTestTable1");
        // build primary key
        testTable1.createNumberColumn("ID", Integer.class, 18, 0, true).addPrimaryKey("ID");

        // build String Field

        testTable1.createStringColumn("STRING_Test_FIELD", 200, false);

        // validate and create structure

        testTable1.buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable1");
        assertNotNull(qDynamicTable);

        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel1.addColumnValue("ID", 1000);
        dynamicTableModel1.addColumnValue("STRING_Test_FIELD", "test123");

        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel2.addColumnValue("STRING_Test_FIELD", "model 2 value");

        ormQueryFactory.modify(qDynamicTable, DynamicTableModel.class).primaryKeyGenerator(PKGeneratorInteger.getInstance()).insert(dynamicTableModel1, dynamicTableModel2);

        List<DynamicTableModel> tableModels = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(qDynamicTable), qDynamicTable, DynamicTableModel.class);

        assertNotNull(tableModels);
        assertEquals(tableModels.size(), 2);

        DynamicTableModel tableModel1 = tableModels.get(0);
        DynamicTableModel tableModel2 = tableModels.get(1);
        assertEquals(tableModel1.getValue("ID", Integer.class).intValue(), 1000);
        assertEquals(tableModel1.getValue("STRING_Test_FIELD", String.class), "test123");
        assertEquals(tableModel2.getValue("STRING_Test_FIELD", String.class), "model 2 value");
        assertNotNull(tableModel2.getValue("ID", Integer.class));

    }

    @Test
    public void testSimpleInsertToTable() {
        QTableBuilder testTable1 = qDynamicTableFactory.buildTable("dynamicTestTable1");
        // build primary key
        testTable1.createNumberColumn("ID", Integer.class, 18, 0, true)
                .addPrimaryKey("ID").addPrimaryKeyGenerator(PKGeneratorInteger.getInstance());

        // build String Field

        testTable1.createStringColumn("STRING_Test_FIELD", 200, false);

        // validate and create structure

        testTable1.buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable1");
        assertNotNull(qDynamicTable);

        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel1.addColumnValue("ID", 1000);
        dynamicTableModel1.addColumnValue("STRING_Test_FIELD", "test123");

        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel2.addColumnValue("STRING_Test_FIELD", "model 2 value");

        ormQueryFactory.insert(dynamicTableModel1, dynamicTableModel2);

        List<DynamicTableModel> tableModels = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(qDynamicTable), qDynamicTable, DynamicTableModel.class);

        assertNotNull(tableModels);
        assertEquals(tableModels.size(), 2);

        DynamicTableModel tableModel1 = tableModels.get(0);
        DynamicTableModel tableModel2 = tableModels.get(1);
        assertEquals(tableModel1.getValue("ID", Integer.class).intValue(), 1000);
        assertEquals(tableModel1.getValue("STRING_Test_FIELD", String.class), "test123");
        assertEquals(tableModel2.getValue("STRING_Test_FIELD", String.class), "model 2 value");
        assertNotNull(tableModel2.getValue("ID", Integer.class));

    }

    @Test
    public void testDeleteFromTable() {
        QTableBuilder testTable1 = qDynamicTableFactory.buildTable("dynamicTestTable1");
        // build primary key
        testTable1.createNumberColumn("ID", Integer.class, 18, 0, true).addPrimaryKey("ID");

        // build String Field

        testTable1.createStringColumn("STRING_Test_FIELD", 200, false);

        // validate and create structure

        testTable1.buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable1");
        assertNotNull(qDynamicTable);

        // table builded

        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel1.addColumnValue("ID", 1000);
        dynamicTableModel1.addColumnValue("STRING_Test_FIELD", "test123");

        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel2.addColumnValue("STRING_Test_FIELD", "model 2 value");

        CrudBuilder<DynamicTableModel> modify = ormQueryFactory.modify(qDynamicTable, DynamicTableModel.class);
        modify.primaryKeyGenerator(PKGeneratorInteger.getInstance()).insert(dynamicTableModel1, dynamicTableModel2);

        List<DynamicTableModel> tableModels = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(qDynamicTable), qDynamicTable, DynamicTableModel.class);

        assertNotNull(tableModels);
        assertEquals(tableModels.size(), 2);

        modify.deleteByIds(1000);

        tableModels = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(qDynamicTable), qDynamicTable, DynamicTableModel.class);

        assertNotNull(tableModels);
        assertEquals(tableModels.size(), 1);

        DynamicTableModel tableModel = tableModels.get(0);
        assertEquals(tableModel.getValue("STRING_Test_FIELD", String.class), "model 2 value");
        assertNotNull(tableModel.getValue("ID", Integer.class));

    }


    @Test
    public void testUpdateTable() {
        QTableBuilder testTable1 = qDynamicTableFactory.buildTable("dynamicTestTable1");
        // build primary key
        testTable1.createNumberColumn("ID", Integer.class, 18, 0, true).addPrimaryKey("ID");

        // build String Field

        testTable1.createStringColumn("STRING_Test_FIELD", 200, false);

        // validate and create structure

        testTable1.buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable1");
        assertNotNull(qDynamicTable);

        // table builded

        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel1.addColumnValue("ID", 1000);
        dynamicTableModel1.addColumnValue("STRING_Test_FIELD", "test123");

        CrudBuilder<DynamicTableModel> modify = ormQueryFactory.modify(qDynamicTable, DynamicTableModel.class);
        modify.primaryKeyGenerator(PKGeneratorInteger.getInstance()).insert(dynamicTableModel1);

        List<DynamicTableModel> tableModels = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(qDynamicTable), qDynamicTable, DynamicTableModel.class);

        assertNotNull(tableModels);
        assertEquals(tableModels.size(), 1);

        DynamicTableModel tableModel = tableModels.get(0);
        assertEquals(tableModel.getValue("ID", Integer.class).intValue(), 1000);
        assertEquals(tableModel.getValue("STRING_Test_FIELD", String.class), "test123");

        DynamicTableModel dtm = tableModel.clone();
        dtm.addColumnValue("STRING_Test_FIELD", "test1234 is new Value");

        modify.updateBuilder().updateModel(dtm).update();

        tableModels = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(qDynamicTable), qDynamicTable, DynamicTableModel.class);

        assertNotNull(tableModels);
        assertEquals(tableModels.size(), 1);

        assertEquals(tableModel.getValue("ID", Integer.class).intValue(), 1000);
        assertEquals(tableModel.getValue("STRING_Test_FIELD", String.class), "test1234 is new Value");

    }

}
