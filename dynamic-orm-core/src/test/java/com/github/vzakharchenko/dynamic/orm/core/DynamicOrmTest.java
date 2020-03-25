package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.OracleTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.SchemaUtils;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGeneratorInteger;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGeneratorSequence;
import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;
import com.github.vzakharchenko.dynamic.orm.core.pk.UUIDPKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.CrudBuilder;
import com.github.vzakharchenko.dynamic.orm.model.TestTableVersionAnnotation;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersion;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersionAnnotation;
import com.github.vzakharchenko.dynamic.orm.qModel.QTesttable;
import com.querydsl.core.QueryException;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import liquibase.datatype.core.NVarcharType;
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
        testTable1.columns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).notNull().useAsPrimaryKey().createColumn();
        // build String Field

        testTable1.columns().addStringColumn("STRING_Test_FIELD").size(200).nullable().createColumn();

        // build foreign Key to table testtable with Primary key ID
        NumberPath<Integer> id = QTesttable.testtable.id;
        testTable1
                .columns().addNumberColumn("Test_FK", id.getType()).size(ModelHelper.getColumnSize(id)).decimalDigits(ModelHelper.getColumnDigitSize(id)).nullable().createColumn().endColumns()
                .foreignKey("Test_FK").addForeignKey(QTesttable.testtable, id);

        // validate and create structure
        testTable1.endBuildTables().buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable1");
        assertNotNull(qDynamicTable);
        List<Path<?>> columns = qDynamicTable.getColumns();
        assertEquals(columns.size(), 3);

        assertTrue(PrimaryKeyHelper.hasPrimaryKey(qDynamicTable));

        NumberPath<Integer> pk = qDynamicTable.getNumberColumnByName("ID", Integer.class);
        assertEquals(PrimaryKeyHelper.getPrimaryKeyColumns(qDynamicTable).get(0), pk);
        assertNotNull(qDynamicTable.getStringColumnByName("STRING_Test_FIELD"));
        assertEquals(qDynamicTable.getStringColumnByName("STRING_Test_FIELD"), ModelHelper.getColumnByName(qDynamicTable, "STRING_Test_FIELD"));
        assertNotNull(qDynamicTable.getNumberColumnByName("Test_FK", id.getType()));
    }

    @Test
    public void testBuildBatchTable() {
        QTableBuilder testTable1 = qDynamicTableFactory.buildTables("dynamicTestTable1");
        // build primary key
        testTable1.columns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).nullable().useAsPrimaryKey().createColumn();

        // build String Field

        testTable1.columns().addStringColumn("STRING_Test_FIELD").size(200).nullable().notPrimaryKey().createColumn();

        // build foreign Key to table testtable with Primary key ID
        NumberPath<Integer> id = QTesttable.testtable.id;
        testTable1.columns()
                .addNumberColumn("Test_FK", id.getType()).size(ModelHelper.getColumnSize(id)).decimalDigits(ModelHelper.getColumnDigitSize(id)).nullable().createColumn().endColumns()
                .foreignKey("Test_FK").addForeignKey(QTesttable.testtable, id);

        // build next table

        QTableBuilder testTable2 = testTable1.buildNextTable("dynamicTestTable2");
        // build primary key for table testTable2
        testTable2.columns().addStringColumn("ID").size(18).notNull().useAsPrimaryKey().createColumn();

        testTable2.columns().addDateColumn("dateColumn").notNull().createColumn();

        testTable2.columns().addNumberColumn("testTable1_FK", Integer.class).size(18).decimalDigits(0).notNull().createColumn().endColumns()
                .foreignKey("testTable1_FK").addForeignKey("dynamicTestTable1");

        testTable2.endBuildTables().buildSchema();
        QDynamicTable qDynamicTable1 = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable1");
        QDynamicTable qDynamicTable2 = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable2");

        assertNotNull(qDynamicTable1);
        assertNotNull(qDynamicTable2);
        List<Path<?>> columns1 = qDynamicTable1.getColumns();
        assertEquals(columns1.size(), 3);
        List<Path<?>> columns2 = qDynamicTable1.getColumns();
        assertEquals(columns2.size(), 3);

        assertTrue(PrimaryKeyHelper.hasPrimaryKey(qDynamicTable1));
        assertTrue(PrimaryKeyHelper.hasPrimaryKey(qDynamicTable2));

        NumberPath<Integer> pk1 = qDynamicTable1.getNumberColumnByName("ID", Integer.class);
        StringPath pk2 = qDynamicTable2.getStringColumnByName("ID");
        assertEquals(PrimaryKeyHelper.getPrimaryKeyColumn(qDynamicTable1), pk1);
        assertEquals(PrimaryKeyHelper.getPrimaryKeyColumn(qDynamicTable2), pk2);
        assertNotEquals(PrimaryKeyHelper.getPrimaryKeyColumn(qDynamicTable2), pk1);
        assertNotEquals(PrimaryKeyHelper.getPrimaryKeyColumn(qDynamicTable1), pk2);

        assertNotNull(qDynamicTable1.getStringColumnByName("STRING_Test_FIELD"));
        assertEquals(qDynamicTable1.getStringColumnByName("STRING_Test_FIELD"), ModelHelper.getColumnByName(qDynamicTable1, "STRING_Test_FIELD"));
        assertNotNull(qDynamicTable1.getNumberColumnByName("Test_FK", id.getType()));

        assertNotNull(qDynamicTable2.getDateColumnByName("dateColumn"));
        assertEquals(qDynamicTable2.getDateColumnByName("dateColumn"), ModelHelper.getColumnByName(qDynamicTable2, "dateColumn"));
        assertNotNull(qDynamicTable2.getNumberColumnByName("testTable1_FK", PrimaryKeyHelper.getPrimaryKeyColumn(qDynamicTable1).getType()));
    }

    @Test
    public void testInsertToTable() {
        QTableBuilder testTable1 = qDynamicTableFactory.buildTables("dynamicTestTable1");
        // build primary key
        testTable1.columns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).notNull().useAsPrimaryKey().createColumn();

        // build String Field

        testTable1.columns().addStringColumn("STRING_Test_FIELD").size(200).nullable().createColumn();

        // validate and create structure

        testTable1.endBuildTables().buildSchema();
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
        testTable1.columns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).notNull().createColumn().endColumns()
                .primaryKey().addPrimaryKey("ID").addPrimaryKeyGenerator(PKGeneratorInteger.getInstance()).endPrimaryKey();

        // build String Field

        testTable1.columns().addStringColumn("STRING_Test_FIELD").size(200).createColumn();

        // validate and create structure

        testTable1.endBuildTables().buildSchema();
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
        testTable1.columns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).useAsPrimaryKey().createColumn();

        // build String Field

        testTable1.columns().addStringColumn("STRING_Test_FIELD").size(200).nullable().createColumn();

        // validate and create structure

        testTable1.endBuildTables().buildSchema();
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
        testTable1.columns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).useAsPrimaryKey().createColumn();

        // build String Field

        testTable1.columns().addStringColumn("STRING_Test_FIELD").size(200).createColumn();

        // validate and create structure

        testTable1.endBuildTables().buildSchema();
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
                .columns().addNumberColumn("ID", Integer.class).useAsPrimaryKey().createColumn()
                .addStringColumn("test").createColumn()
                .addDateTimeColumn("dateTime").createColumn()
                .addBooleanColumn("b").createColumn()
                .addDateColumn("date").createColumn()
                .addBlobColumn("blob").createColumn()
                .addClobColumn("clob").createColumn()
                .addCharColumn("c").createColumn()
                .addTimeColumn("time").createColumn()
                .addNumberColumn("fk1", Integer.class).createColumn()
                .addNumberColumn("fk2", Integer.class).createColumn()
                .addNumberColumn("fk3", Integer.class).createColumn()
                .endColumns()
                .foreignKey("fk2").addForeignKey(QTestTableVersion.qTestTableVersion)
                .endBuildTables().buildSchema();
        ormQueryFactory.transactionManager().commit();

        QDynamicTable dynamicTestTable = qDynamicTableFactory.getQDynamicTableByName("dynamicTestTable");

        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .foreignKeyPath(dynamicTestTable.getNumberColumnByName("fk1"))
                .addForeignKey(
                        QTestTableVersion.qTestTableVersion, QTestTableVersion.qTestTableVersion.id
                ).endBuildTables().buildSchema();
        ormQueryFactory.transactionManager().commit();

        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .addVersionColumn(
                        dynamicTestTable.getDateTimeColumnByName("dateTime")
                ).endBuildTables().buildSchema();
        ormQueryFactory.transactionManager().commit();

        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .index(dynamicTestTable.getStringColumnByName("test")).addIndex(
        ).endBuildTables().buildSchema();
        ormQueryFactory.transactionManager().commit();

    }

    @Test
    public void testCustomColumns() {
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .columns().addNumberColumn("ID", Integer.class).useAsPrimaryKey().createColumn()
                .addCustomColumn("customColumn")
                .column(Expressions::stringPath)
                .jdbcType(new NVarcharType()).createColumn()
                .endColumns()
                .endBuildTables().buildSchema();
    }

    @Test
    public void testSequanceTestSUCCESS() {
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .columns().addNumberColumn("ID", Integer.class).useAsPrimaryKey().createColumn()
                .addStringColumn("testColumn").size(100).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence("dynamicTestTableSequance1")).endPrimaryKey()
                .endBuildTables()
                .createSequence("dynamicTestTableSequance1")
                .initialValue(1000L)
                .increment(10L)
                .addSequence()
                .buildSchema();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testSequanceTestFailed() {
        qDynamicTableFactory.buildTables("dynamicTestTable")
                .columns().addNumberColumn("ID", Integer.class).useAsPrimaryKey().createColumn()
                .addStringColumn("testColumn").size(100).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence("dynamicTestTableSequance1")).endPrimaryKey()
                .endBuildTables()
                .createSequence("dynamicTestTableSequance1")
                .initialValue(1000L)
                .increment(10L)
                .min(1000L)
                .max(10000L)
                .addSequence()
                .buildSchema();
    }

    @Test
    public void testViewTest() {
        qDynamicTableFactory
                .createView("testView").resultSet(ormQueryFactory.buildQuery()
                .from(QTestTableVersionAnnotation.qTestTableVersionAnnotation), QTestTableVersionAnnotation.qTestTableVersionAnnotation.id).addView()
                .buildSchema();
    }

    @Test(expectedExceptions = Exception.class)
    public void testViewTestUnsupported() {
        qDynamicTableFactory
                .createView("testView").resultSet(ormQueryFactory.buildQuery()
                .from(QTestTableVersionAnnotation.qTestTableVersionAnnotation), QTestTableVersionAnnotation.qTestTableVersionAnnotation.id).addView()
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
                .from(QTestTableVersionAnnotation.qTestTableVersionAnnotation), QTestTableVersionAnnotation.qTestTableVersionAnnotation.id).addView()
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
        ).addView()
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
        ).addView()
                .buildSchema();

        qDynamicTableFactory.getQDynamicTableByName("testView223");

    }

    @Test
    public void testViewSizeColumn() {
        qDynamicTableFactory.buildTables("table")
                .columns().addStringColumn("string1").createColumn()
                .addStringColumn("string2").createColumn()
                .addTimeColumn("time1").createColumn()
                .addTimeColumn("time2").createColumn()
                .addCharColumn("char1").createColumn()
                .addCharColumn("char2").createColumn()
                .addClobColumn("clob1").createColumn()
                .addClobColumn("clob2").createColumn()
                .addBlobColumn("blob1").createColumn()
                .addBlobColumn("blob2").createColumn()
                .addDateColumn("date1").createColumn()
                .addDateColumn("date2").createColumn()
                .addDateTimeColumn("datetime1").createColumn()
                .addDateTimeColumn("datetime2").createColumn()
                .addBooleanColumn("boolean1").createColumn()
                .addBooleanColumn("boolean2").createColumn()
                .endColumns().endBuildTables()
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
        ).addView().buildSchema();

        QDynamicTable testView = qDynamicTableFactory.getQDynamicTableByName("testView");
        assertNotNull(testView);

    }

    @Test
    public void testFileSaver() throws IOException {
        qDynamicTableFactory.buildTables("table")
                .columns().addStringColumn("string1").size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("string2").size(255).createColumn()
                .addTimeColumn("time1").createColumn()
                .addTimeColumn("time2").createColumn()
                .addCharColumn("char1").size(255).createColumn()
                .addCharColumn("char2").size(255).createColumn()
                .addClobColumn("clob1").createColumn()
                .addClobColumn("clob2").createColumn()
                .addBlobColumn("blob1").createColumn()
                .addBlobColumn("blob2").createColumn()
                .addDateColumn("date1").createColumn()
                .addDateColumn("date2").createColumn()
                .addDateTimeColumn("datetime1").createColumn()
                .addDateTimeColumn("datetime2").createColumn()
                .addBooleanColumn("boolean1").createColumn()
                .addBooleanColumn("boolean2").createColumn()
                .endColumns()
                .addVersionColumn("datetime1")
                .addSoftDeleteColumn("boolean1", true, false)
                .primaryKey().addPrimaryKeyGenerator(UUIDPKGenerator.getInstance()).endPrimaryKey()
                .endBuildTables()
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
        ).addView()
                .buildTables("testTable").columns()
                .addNumberColumn("Id", Integer.class).size(37).useAsPrimaryKey().createColumn()
                .addNumberColumn("exIdt", Integer.class).createColumn()
                .addStringColumn("exIdt2").size(255).createColumn()
                .endColumns()
                .index("exIdt").addUniqueIndex()
                .foreignKey("exIdt").addForeignKey(QTestTableVersionAnnotation.qTestTableVersionAnnotation)
                .foreignKey("exIdt2").addForeignKey(qDynamicTable.getTableName())
                .endBuildTables()
                .createSequence("sequence").addSequence()
                .buildSchema();
        File file = new File("./target/", "testSchema.json");
        qDynamicTableFactory.saveSchema(SchemaUtils.getFileSaver(file));
        byte[] bytes = FileUtils.readFileToByteArray(file);
        assertNotNull(bytes);

    }


    @Test
    public void testRemoveForeignKey() {
        qDynamicTableFactory.buildTables("table1").columns()
                .addStringColumn("Id1").size(255).useAsPrimaryKey().createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .buildNextTable("table2")
                .columns()
                .addStringColumn("Id2").size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("Id1").size(255).notNull().createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .foreignKey("Id1").addForeignKey("table1")
                .endBuildTables().buildSchema();
        QDynamicTable table1 = qDynamicTableFactory.getQDynamicTableByName("table1");
        QDynamicTable table2 = qDynamicTableFactory.getQDynamicTableByName("table2");

        // insert Table 1
        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(table1);
        ormQueryFactory.insert(dynamicTableModel1);

        // insert to table 2 with foreign Key
        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(table2);
        dynamicTableModel2.addColumnValue("id1", dynamicTableModel1.getValue("Id1"));
        ormQueryFactory.insert(dynamicTableModel2);

        // drop foreign Key

        qDynamicTableFactory.buildTables("table2")
                .foreignKey("Id1").drop()
                .endBuildTables().buildSchema();


        // insert to table 2 with foreign Key
        DynamicTableModel dynamicTableModel2WithoutForeign = new DynamicTableModel(table2);
        dynamicTableModel2WithoutForeign.addColumnValue("id1", "Not Foreign Key Value");
        ormQueryFactory.insert(dynamicTableModel2WithoutForeign);

    }

    @Test(expectedExceptions = QueryException.class)
    public void testRemoveAddForeignKey() {
        qDynamicTableFactory.buildTables("table1").columns()
                .addStringColumn("Id1").size(255).useAsPrimaryKey().createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .buildNextTable("table2")
                .columns()
                .addStringColumn("Id2").size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("Id1").size(255).notNull().createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .foreignKey("Id1").addForeignKey("table1")
                .endBuildTables().buildSchema();
        QDynamicTable table1 = qDynamicTableFactory.getQDynamicTableByName("table1");
        QDynamicTable table2 = qDynamicTableFactory.getQDynamicTableByName("table2");

        // insert Table 1
        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(table1);
        ormQueryFactory.insert(dynamicTableModel1);

        // insert to table 2 with foreign Key
        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(table2);
        dynamicTableModel2.addColumnValue("id1", dynamicTableModel1.getValue("Id1"));
        ormQueryFactory.insert(dynamicTableModel2);

        // drop foreign Key

        qDynamicTableFactory.buildTables("table2")
                .foreignKey("Id1").drop()
                .endBuildTables().buildSchema();

        // add foreign Key
        qDynamicTableFactory.buildTables("table2")
                .foreignKey("Id1").addForeignKey("table1")
                .endBuildTables().buildSchema();


        // insert to table 2 with foreign Key
        DynamicTableModel dynamicTableModel2WithoutForeign = new DynamicTableModel(table2);
        dynamicTableModel2WithoutForeign.addColumnValue("id1", "Not Foreign Key Value");
        ormQueryFactory.insert(dynamicTableModel2WithoutForeign);

    }


    @Test
    public void testDropIndex() {
        // create schema
        qDynamicTableFactory.buildTables("table1").columns()
                .addStringColumn("Id1").size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("column1").size(255).createColumn()
                .addStringColumn("column2").size(255).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).endPrimaryKey()
                .index("column1", "column2").addIndex()
                .endBuildTables().buildSchema();

        QDynamicTable table1 = qDynamicTableFactory.getQDynamicTableByName("table1");
        ormQueryFactory.insert(new DynamicTableModel(table1));

        // drop Index
        qDynamicTableFactory.buildTables("table1")
                .index("column1", "column2").drop()
                .endBuildTables().buildSchema();

        ormQueryFactory.insert(new DynamicTableModel(table1));
    }

}