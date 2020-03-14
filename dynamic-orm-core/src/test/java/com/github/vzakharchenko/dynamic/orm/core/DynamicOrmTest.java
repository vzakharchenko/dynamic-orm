package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.OracleTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.SchemaUtils;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGeneratorInteger;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGeneratorSequence;
import com.github.vzakharchenko.dynamic.orm.core.pk.UUIDPKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.CrudBuilder;
import com.github.vzakharchenko.dynamic.orm.model.TestTableVersionAnnotation;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersion;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersionAnnotation;
import com.github.vzakharchenko.dynamic.orm.qModel.QTesttable;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import org.apache.commons.io.FileUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.*;


/**
 *
 */
public class DynamicOrmTest extends OracleTestQueryOrm {

    @Test
    public void testBuildOneTable() {
        QTableBuilder testTable1 = qDynamicTableFactory.buildTables("dynamicTestTable1");
        // build primary key
        testTable1.addColumns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).notNull().useAsPrimaryKey().create();
        // build String Field

        testTable1.addColumns().addStringColumn("STRING_Test_FIELD").size(200).nullable().create();

        // build foreign Key to table testtable with Primary key ID
        NumberPath<Integer> id = QTesttable.testtable.id;
        testTable1
                .addColumns().addNumberColumn("Test_FK", id.getType()).size(ModelHelper.getColumnSize(id)).decimalDigits(ModelHelper.getColumnDigitSize(id)).nullable().create().finish()
                .addForeignKey().buildForeignKey("Test_FK", QTesttable.testtable, id);

        // validate and create structure
        testTable1.finish().buildSchema();
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
        QTableBuilder testTable1 = qDynamicTableFactory.buildTables("dynamicTestTable1");
        // build primary key
        testTable1.addColumns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).nullable().useAsPrimaryKey().create();

        // build String Field

        testTable1.addColumns().addStringColumn("STRING_Test_FIELD").size(200).nullable().usAsNotPrimaryKey().create();

        // build foreign Key to table testtable with Primary key ID
        NumberPath<Integer> id = QTesttable.testtable.id;
        testTable1.addColumns()
                .addNumberColumn("Test_FK", id.getType()).size(ModelHelper.getColumnSize(id)).decimalDigits(ModelHelper.getColumnDigitSize(id)).nullable().create().finish()
                .addForeignKey().buildForeignKey("Test_FK", QTesttable.testtable, id);

        // build next table

        QTableBuilder testTable2 = testTable1.buildNextTable("dynamicTestTable2");
        // build primary key for table testTable2
        testTable2.addColumns().addStringColumn("ID").size(18).notNull().useAsPrimaryKey().create();

        testTable2.addColumns().addDateColumn("dateColumn").notNull().create();

        testTable2.addColumns().addNumberColumn("testTable1_FK", Integer.class).size(18).decimalDigits(0).notNull().create().finish().addForeignKey().buildForeignKey("testTable1_FK", "dynamicTestTable1");

        testTable2.finish().buildSchema();
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
        QTableBuilder testTable1 = qDynamicTableFactory.buildTables("dynamicTestTable1");
        // build primary key
        testTable1.addColumns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).notNull().useAsPrimaryKey().create();

        // build String Field

        testTable1.addColumns().addStringColumn("STRING_Test_FIELD").size(200).nullable().create();

        // validate and create structure

        testTable1.finish().buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable1");
        assertNotNull(qDynamicTable);

        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel1.addColumnValue("ID", 1000);
        dynamicTableModel1.addColumnValue("STRING_Test_FIELD", "test123");

        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel2.addColumnValue("STRING_Test_FIELD", "model 2 value");

        ormQueryFactory.modify(qDynamicTable).primaryKeyGenerator(PKGeneratorInteger.getInstance()).insert(dynamicTableModel1, dynamicTableModel2);

        List<DynamicTableModel> tableModels = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(qDynamicTable), qDynamicTable);

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
        QTableBuilder testTable1 = qDynamicTableFactory.buildTables("dynamicTestTable1");
        // build primary key
        testTable1.addColumns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).notNull().create().finish()
                .addPrimaryKey().addPrimaryKey("ID").addPrimaryKeyGenerator(PKGeneratorInteger.getInstance()).finish();

        // build String Field

        testTable1.addColumns().addStringColumn("STRING_Test_FIELD").size(200).create();

        // validate and create structure

        testTable1.finish().buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable1");
        assertNotNull(qDynamicTable);

        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel1.addColumnValue("ID", 1000);
        dynamicTableModel1.addColumnValue("STRING_Test_FIELD", "test123");

        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel2.addColumnValue("STRING_Test_FIELD", "model 2 value");

        ormQueryFactory.insert(dynamicTableModel1, dynamicTableModel2);

        List<DynamicTableModel> tableModels = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(qDynamicTable), qDynamicTable);

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
        QTableBuilder testTable1 = qDynamicTableFactory.buildTables("dynamicTestTable1");
        // build primary key
        testTable1.addColumns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).useAsPrimaryKey().create();

        // build String Field

        testTable1.addColumns().addStringColumn("STRING_Test_FIELD").size(200).nullable().create();

        // validate and create structure

        testTable1.finish().buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable1");
        assertNotNull(qDynamicTable);

        // table builded

        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel1.addColumnValue("ID", 1000);
        dynamicTableModel1.addColumnValue("STRING_Test_FIELD", "test123");

        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel2.addColumnValue("STRING_Test_FIELD", "model 2 value");

        CrudBuilder<DynamicTableModel> modify = ormQueryFactory.modify(qDynamicTable);
        modify.primaryKeyGenerator(PKGeneratorInteger.getInstance()).insert(dynamicTableModel1, dynamicTableModel2);

        List<DynamicTableModel> tableModels = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(qDynamicTable), qDynamicTable);

        assertNotNull(tableModels);
        assertEquals(tableModels.size(), 2);

        modify.deleteByIds(1000);

        tableModels = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(qDynamicTable), qDynamicTable);

        assertNotNull(tableModels);
        assertEquals(tableModels.size(), 1);

        DynamicTableModel tableModel = tableModels.get(0);
        assertEquals(tableModel.getValue("STRING_Test_FIELD", String.class), "model 2 value");
        assertNotNull(tableModel.getValue("ID", Integer.class));

    }


    @Test
    public void testUpdateTable() {
        QTableBuilder testTable1 = qDynamicTableFactory.buildTables("dynamicTestTable1");
        // build primary key
        testTable1.addColumns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).useAsPrimaryKey().create();

        // build String Field

        testTable1.addColumns().addStringColumn("STRING_Test_FIELD").size(200).create();

        // validate and create structure

        testTable1.finish().buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable1");
        assertNotNull(qDynamicTable);

        // table builded

        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel1.addColumnValue("ID", 1000);
        dynamicTableModel1.addColumnValue("STRING_Test_FIELD", "test123");

        CrudBuilder<DynamicTableModel> modify = ormQueryFactory.modify(qDynamicTable);
        modify.primaryKeyGenerator(PKGeneratorInteger.getInstance()).insert(dynamicTableModel1);

        List<DynamicTableModel> tableModels = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(qDynamicTable), qDynamicTable);

        assertNotNull(tableModels);
        assertEquals(tableModels.size(), 1);

        DynamicTableModel tableModel = tableModels.get(0);
        assertEquals(tableModel.getValue("ID", Integer.class).intValue(), 1000);
        assertEquals(tableModel.getValue("STRING_Test_FIELD", String.class), "test123");

        DynamicTableModel dtm = tableModel.clone();
        dtm.addColumnValue("STRING_Test_FIELD", "test1234 is new Value");

        modify.updateBuilder().updateModel(dtm).update();

        tableModels = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(qDynamicTable), qDynamicTable);

        assertNotNull(tableModels);
        assertEquals(tableModels.size(), 1);

        assertEquals(tableModel.getValue("ID", Integer.class).intValue(), 1000);
        assertEquals(tableModel.getValue("STRING_Test_FIELD", String.class), "test1234 is new Value");

    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testDynamicSchema() {
        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .addColumns().addNumberColumn("ID", Integer.class).useAsPrimaryKey().create()
                .addStringColumn("test").create()
                .addDateTimeColumn("dateTime").create()
                .addBooleanColumn("b").create()
                .addDateColumn("date").create()
                .addBlobColumn("blob").create()
                .addClobColumn("clob").create()
                .addCharColumn("c").create()
                .addTimeColumn("time").create()
                .addNumberColumn("fk1", Integer.class).create()
                .addNumberColumn("fk2", Integer.class).create()
                .addNumberColumn("fk3", Integer.class).create()
                .finish()
                .addForeignKey().buildForeignKey("fk2", QTestTableVersion.qTestTableVersion)
                .finish().buildSchema();
        ormQueryFactory.transactionManager().commit();

        QDynamicTable dynamicTestTable = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable");

        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .addForeignKey().buildForeignKey(
                dynamicTestTable.getNumberColumnByName("fk1"),
                QTestTableVersion.qTestTableVersion, QTestTableVersion.qTestTableVersion.id
        ).finish().buildSchema();
        ormQueryFactory.transactionManager().commit();

        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .addVersionColumn(
                        dynamicTestTable.getDateTimeColumnByName("dateTime")
                ).finish().buildSchema();
        ormQueryFactory.transactionManager().commit();

        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .addIndex().buildIndex(
                dynamicTestTable.getStringColumnByName("test"),
                true
        ).finish().buildSchema();
        ormQueryFactory.transactionManager().commit();

    }

    @Test
    public void testSequanceTestSUCCESS() {
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .addColumns().addNumberColumn("ID", Integer.class).useAsPrimaryKey().create()
                .addStringColumn("testColumn").size(100).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence("dynamicTestTableSequance1")).finish()
                .finish()
                .createSequence("dynamicTestTableSequance1")
                .initialValue(1000L)
                .increment(10L)
                .finish()
                .buildSchema();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testSequanceTestFailed() {
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .addColumns().addNumberColumn("ID", Integer.class).useAsPrimaryKey().create()
                .addStringColumn("testColumn").size(100).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence("dynamicTestTableSequance1")).finish()
                .finish()
                .createSequence("dynamicTestTableSequance1")
                .initialValue(1000L)
                .increment(10L)
                .min(1000L)
                .max(10000L)
                .finish()
                .buildSchema();
    }

    @Test
    public void testViewTest() {
        qDynamicTableFactory
                .createView("testView").resultSet(ormQueryFactory.buildQuery()
                .from(QTestTableVersionAnnotation.qTestTableVersionAnnotation), QTestTableVersionAnnotation.qTestTableVersionAnnotation.id).finish()
                .buildSchema();
    }

    @Test(expectedExceptions = Exception.class)
    public void testViewTestUnsupported() {
        qDynamicTableFactory
                .createView("testView").resultSet(ormQueryFactory.buildQuery()
                .from(QTestTableVersionAnnotation.qTestTableVersionAnnotation), QTestTableVersionAnnotation.qTestTableVersionAnnotation.id).finish()
                .buildSchema();

        QDynamicTable testView = qDynamicTableFactory.getQDynamicTableByName("testView");
        assertNotNull(testView);

        TestTableVersionAnnotation testTableVersionAnnotation = new TestTableVersionAnnotation();
        ormQueryFactory.insert(testTableVersionAnnotation);

        // select from table
        TestTableVersionAnnotation versionAnnotation = ormQueryFactory.select()
                .findOne(ormQueryFactory.buildQuery(), TestTableVersionAnnotation.class);
        assertNotNull(versionAnnotation);

        // select from View
        DynamicTableModel dynamicTableModel = ormQueryFactory.select()
                .findOne(ormQueryFactory.buildQuery().from(testView), testView);
        assertNotNull(dynamicTableModel);
    }

    @Test(expectedExceptions = Exception.class)
    public void testViewSelectCacheTestUnsupported() {
        qDynamicTableFactory
                .createView("testView").resultSet(ormQueryFactory.buildQuery()
                .from(QTestTableVersionAnnotation.qTestTableVersionAnnotation), QTestTableVersionAnnotation.qTestTableVersionAnnotation.id).finish()
                .buildSchema();

        QDynamicTable testView = qDynamicTableFactory.getQDynamicTableByName("testView");
        assertNotNull(testView);

        TestTableVersionAnnotation testTableVersionAnnotation = new TestTableVersionAnnotation();
        ormQueryFactory.insert(testTableVersionAnnotation);

        // select from table
        TestTableVersionAnnotation versionAnnotation = ormQueryFactory.select()
                .findOne(ormQueryFactory.buildQuery(), TestTableVersionAnnotation.class);
        assertNotNull(versionAnnotation);

        // fetch from View with cache (need manually register related tables with query)
        DynamicTableModel dynamicTableModel2 = ormQueryFactory.selectCache().registerRelatedTables(
                Collections.singletonList(QTestTableVersionAnnotation.qTestTableVersionAnnotation))
                .findOne(ormQueryFactory.buildQuery().from(testView), testView);
        assertNotNull(dynamicTableModel2);
    }

    @Test
    public void testViewAlias() {
        qDynamicTableFactory
                .createView("testView").resultSet(ormQueryFactory.buildQuery()
                        .from(QTestTableVersionAnnotation.qTestTableVersionAnnotation),
                QTestTableVersionAnnotation.qTestTableVersionAnnotation.id.as("id"),
                QTestTableVersionAnnotation.qTestTableVersionAnnotation.version.as("version")
        ).finish()
                .buildSchema();

        QDynamicTable testView = qDynamicTableFactory.getQDynamicTableByName("testView");
        assertNotNull(testView);

    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testViewFailed() {
        qDynamicTableFactory
                .createView("testView").resultSet(ormQueryFactory.buildQuery()
                        .from(QTestTableVersionAnnotation.qTestTableVersionAnnotation),
                QTestTableVersionAnnotation.qTestTableVersionAnnotation.id.as("id"),
                QTestTableVersionAnnotation.qTestTableVersionAnnotation.version.as("version")
        ).finish()
                .buildSchema();

        qDynamicTableFactory.getQDynamicTableByName("testView223");

    }

    @Test
    public void testViewSizeColumn() {
        qDynamicTableFactory.buildTables("table")
                .addColumns().addStringColumn("string1").create()
                .addStringColumn("string2").create()
                .addTimeColumn("time1").create()
                .addTimeColumn("time2").create()
                .addCharColumn("char1").create()
                .addCharColumn("char2").create()
                .addClobColumn("clob1").create()
                .addClobColumn("clob2").create()
                .addBlobColumn("blob1").create()
                .addBlobColumn("blob2").create()
                .addDateColumn("date1").create()
                .addDateColumn("date2").create()
                .addDateTimeColumn("datetime1").create()
                .addDateTimeColumn("datetime2").create()
                .addBooleanColumn("boolean1").create()
                .addBooleanColumn("boolean2").create()
                .finish().finish()
                .buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("table");

        qDynamicTableFactory.createView("testView").resultSet(
                ormQueryFactory.buildQuery().from(qDynamicTable),
                qDynamicTable.getStringColumnByName("string1"),
                qDynamicTable.getStringColumnByName("string2").as("someString"),
                qDynamicTable.getTimeColumnByName("time1"),
                qDynamicTable.getTimeColumnByName("time2").as("someTime"),
                qDynamicTable.getDateColumnByName("date1"),
                qDynamicTable.getDateColumnByName("date2").as("someDate"),
                qDynamicTable.getDateTimeColumnByName("datetime1"),
                qDynamicTable.getDateTimeColumnByName("datetime2").as("someDateTime"),
                qDynamicTable.getCharColumnByName("char1"),
                qDynamicTable.getCharColumnByName("char2").as("someChar"),
                qDynamicTable.getClobColumnByName("clob1"),
                qDynamicTable.getClobColumnByName("clob2").as("someClob"),
                qDynamicTable.getBlobColumnByName("blob1"),
                qDynamicTable.getBlobColumnByName("blob2").as("someBlob"),
                qDynamicTable.getBooleanColumnByName("boolean1"),
                qDynamicTable.getBooleanColumnByName("boolean2").as("someBoolean")
        ).finish().buildSchema();

        QDynamicTable testView = qDynamicTableFactory.getQDynamicTableByName("testView");
        assertNotNull(testView);

    }

    @Test
    public void testFileSaver() throws IOException {
        qDynamicTableFactory.buildTables("table")
                .addColumns().addStringColumn("string1").size(255).useAsPrimaryKey().create()
                .addStringColumn("string2").size(255).create()
                .addTimeColumn("time1").create()
                .addTimeColumn("time2").create()
                .addCharColumn("char1").size(255).create()
                .addCharColumn("char2").size(255).create()
                .addClobColumn("clob1").create()
                .addClobColumn("clob2").create()
                .addBlobColumn("blob1").create()
                .addBlobColumn("blob2").create()
                .addDateColumn("date1").create()
                .addDateColumn("date2").create()
                .addDateTimeColumn("datetime1").create()
                .addDateTimeColumn("datetime2").create()
                .addBooleanColumn("boolean1").create()
                .addBooleanColumn("boolean2").create()
                .finish()
                .addVersionColumn("datetime1")
                .addSoftDeleteColumn("boolean1", true, false)
                .addPrimaryKey().addPrimaryKeyGenerator(UUIDPKGenerator.getInstance()).finish()
                .finish()
                .buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("table");

        qDynamicTableFactory.createView("testView").resultSet(
                ormQueryFactory.buildQuery().from(qDynamicTable),
                qDynamicTable.getStringColumnByName("string1"),
                qDynamicTable.getStringColumnByName("string2").as("someString"),
                qDynamicTable.getTimeColumnByName("time1"),
                qDynamicTable.getTimeColumnByName("time2").as("someTime"),
                qDynamicTable.getDateColumnByName("date1"),
                qDynamicTable.getDateColumnByName("date2").as("someDate"),
                qDynamicTable.getDateTimeColumnByName("datetime1"),
                qDynamicTable.getDateTimeColumnByName("datetime2").as("someDateTime"),
                qDynamicTable.getCharColumnByName("char1"),
                qDynamicTable.getCharColumnByName("char2").as("someChar"),
                qDynamicTable.getClobColumnByName("clob1"),
                qDynamicTable.getClobColumnByName("clob2").as("someClob"),
                qDynamicTable.getBlobColumnByName("blob1"),
                qDynamicTable.getBlobColumnByName("blob2").as("someBlob"),
                qDynamicTable.getBooleanColumnByName("boolean1"),
                qDynamicTable.getBooleanColumnByName("boolean2").as("someBoolean")
        ).finish()
                .buildTables("testTable").addColumns()
                .addNumberColumn("Id", Integer.class).size(37).useAsPrimaryKey().create()
                .addNumberColumn("exIdt", Integer.class).create()
                .addStringColumn("exIdt2").size(255).create()
                .finish()
                .addIndex().buildIndex("exIdt", true)
                .addForeignKey().buildForeignKey("exIdt", QTestTableVersionAnnotation.qTestTableVersionAnnotation)
                .addForeignKey().buildForeignKey("exIdt2", qDynamicTable.getTableName())
                .finish()
                .createSequence("sequence").finish()
                .buildSchema();
        File file = new File("./target/", "testSchema.json");
        qDynamicTableFactory.saveSchema(SchemaUtils.getFileSaver(file));
        byte[] bytes = FileUtils.readFileToByteArray(file);
        assertNotNull(bytes);

    }
}