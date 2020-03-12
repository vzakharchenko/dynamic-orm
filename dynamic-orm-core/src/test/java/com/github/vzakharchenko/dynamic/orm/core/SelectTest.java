package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.AnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;
import com.github.vzakharchenko.dynamic.orm.model.TestTableVersionAnnotation;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersionAnnotation;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class SelectTest extends AnnotationTestQueryOrm {

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
    public void existTest() {
        TestTableVersionAnnotation value = new TestTableVersionAnnotation();
        ormQueryFactory.insert(value);
        assertTrue(ormQueryFactory.select().exist(ormQueryFactory.buildQuery().from(QTestTableVersionAnnotation.qTestTableVersionAnnotation)));
        assertFalse(ormQueryFactory.select().notExist(ormQueryFactory.buildQuery().from(QTestTableVersionAnnotation.qTestTableVersionAnnotation)));
    }

    @Test
    public void existDynamicTest() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel);
        assertTrue(ormQueryFactory.select().exist(ormQueryFactory.buildQuery().from(dynamicTable)));
        assertFalse(ormQueryFactory.select().notExist(ormQueryFactory.buildQuery().from(dynamicTable)));

    }

    @Test
    public void notExistTest() {
        assertFalse(ormQueryFactory.select().exist(ormQueryFactory.buildQuery().from(QTestTableVersionAnnotation.qTestTableVersionAnnotation)));
        assertTrue(ormQueryFactory.select().notExist(ormQueryFactory.buildQuery().from(QTestTableVersionAnnotation.qTestTableVersionAnnotation)));
    }


    @Test
    public void notExistDynamicTest() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        assertFalse(ormQueryFactory.select().exist(ormQueryFactory.buildQuery().from(dynamicTable)));
        assertTrue(ormQueryFactory.select().notExist(ormQueryFactory.buildQuery().from(dynamicTable)));

    }

    @Test
    public void limitOffsetTest() {

        // insert 5 records

        TestTableVersionAnnotation value1 = new TestTableVersionAnnotation();
        TestTableVersionAnnotation value2 = new TestTableVersionAnnotation();
        TestTableVersionAnnotation value3 = new TestTableVersionAnnotation();
        TestTableVersionAnnotation value4 = new TestTableVersionAnnotation();
        TestTableVersionAnnotation value5 = new TestTableVersionAnnotation();
        ormQueryFactory.insert(value1, value2, value3, value4, value5);

        List<TestTableVersionAnnotation> limit = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().limit(3), TestTableVersionAnnotation.class);
        assertEquals(limit.size(), 3);

        List<TestTableVersionAnnotation> offset = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().limit(3).offset(3), TestTableVersionAnnotation.class);
        assertEquals(offset.size(), 2);


    }

    private DynamicTableModel createDynamicModel(String testColumn) {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", testColumn);
        return dynamicTableModel;
    }

    @Test
    public void limitOffsetDynamicTest() {

        // insert 5 records
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel value1 = createDynamicModel("1");
        DynamicTableModel value2 = createDynamicModel("2");
        DynamicTableModel value3 = createDynamicModel("3");
        DynamicTableModel value4 = createDynamicModel("4");
        DynamicTableModel value5 = createDynamicModel("5");
        ormQueryFactory.insert(value1, value2, value3, value4, value5);

        List<DynamicTableModel> limit = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().limit(3), dynamicTable, DynamicTableModel.class);
        assertEquals(limit.size(), 3);

        List<DynamicTableModel> offset = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().limit(3).offset(3), dynamicTable, DynamicTableModel.class);
        assertEquals(offset.size(), 2);


    }
}
