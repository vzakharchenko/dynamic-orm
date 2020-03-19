package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.AnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.OracleTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.cache.LazyList;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;
import com.github.vzakharchenko.dynamic.orm.core.predicate.PredicateFactory;
import com.github.vzakharchenko.dynamic.orm.model.TestTableVersionAnnotation;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersionAnnotation;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.Wildcard;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

import static org.testng.Assert.*;

public class SelectTest extends OracleTestQueryOrm {

    @BeforeMethod
    public void beforeMethod() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns().addStringColumn("Id").size(255).useAsPrimaryKey().create()
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
        assertTrue(ormQueryFactory.selectCache().exist(ormQueryFactory.buildQuery().from(QTestTableVersionAnnotation.qTestTableVersionAnnotation)));
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

        List<TestTableVersionAnnotation> limit = ormQueryFactory.select()
                .findAll(ormQueryFactory.buildQuery().limit(3),
                        TestTableVersionAnnotation.class);

        List<TestTableVersionAnnotation> offset = ormQueryFactory.select()
                .findAll(ormQueryFactory.buildQuery().limit(3).offset(3),
                        TestTableVersionAnnotation.class);

        assertEquals(limit.size(), 3);
        assertEquals(offset.size(), 2);


    }

    private DynamicTableModel createDynamicModel(String testColumn) {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", testColumn);
        return dynamicTableModel;
    }

    @Test
    public void alwaysFalseQuery() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        PredicateFactory.alwaysFalsePredicate();

        assertTrue(ormQueryFactory.select().notExist(ormQueryFactory
                .buildQuery()
                .from(dynamicTable)
                .where(PredicateFactory.alwaysFalsePredicate())));

    }

    @Test
    public void wrapWhere() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel value1 = createDynamicModel("1");
        DynamicTableModel value2 = createDynamicModel("2");
        DynamicTableModel value3 = createDynamicModel("3");
        DynamicTableModel value4 = createDynamicModel("4");
        DynamicTableModel value5 = createDynamicModel("5");
        PredicateFactory.alwaysFalsePredicate();

        assertTrue(ormQueryFactory.select().notExist(ormQueryFactory
                .buildQuery()
                .from(dynamicTable)
                .where(PredicateFactory
                        .wrapPredicate(PredicateFactory.alwaysFalsePredicate().and(dynamicTable.getStringColumnByName("TestColumn").isNotNull())))));

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

        List<DynamicTableModel> limit = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().limit(3), dynamicTable);
        assertEquals(limit.size(), 3);

        List<DynamicTableModel> offset = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().limit(3).offset(3), dynamicTable);
        assertEquals(offset.size(), 2);
    }

    @Test
    public void wildCardSelect() {

        // insert 5 records
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel value1 = createDynamicModel("1");
        DynamicTableModel value2 = createDynamicModel("2");
        DynamicTableModel value3 = createDynamicModel("3");
        DynamicTableModel value4 = createDynamicModel("4");
        DynamicTableModel value5 = createDynamicModel("5");
        ormQueryFactory.insert(value1, value2, value3, value4, value5);


        StringPath testColumn = dynamicTable.getStringColumnByName("TestColumn");

        // fetch all data from all table
        // if you want cache the result you can use selectCache() instead of select()
        List<RawModel> rawModels = ormQueryFactory.select().rawSelect(
                ormQueryFactory.buildQuery().from(dynamicTable)
                        .orderBy(testColumn.asc())).findAll(Wildcard.all);

        RawModel rawModel = rawModels.get(0);
        Object columnValue1 = rawModel.getValueByPosition(0);
        Object columnValue2 = rawModel.getValueByPosition(1);
        Object columnValue3 = rawModel.getValueByPosition(2);
        assertEquals(columnValue1, value1.getValue("Id", String.class));
        assertEquals(columnValue2, value1.getValue("modificationTime", Date.class));
        assertEquals(columnValue3, value1.getValue("TestColumn", String.class));
        assertEquals(rawModels.size(), 5);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void wildCardSelectFailed() {

        // insert 5 records
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel value1 = createDynamicModel("1");
        DynamicTableModel value2 = createDynamicModel("2");
        DynamicTableModel value3 = createDynamicModel("3");
        DynamicTableModel value4 = createDynamicModel("4");
        DynamicTableModel value5 = createDynamicModel("5");
        ormQueryFactory.insert(value1, value2, value3, value4, value5);


        StringPath testColumn = dynamicTable.getStringColumnByName("TestColumn");

        // fetch all data from all table
        // if you want cache the result you can use selectCache() instead of select()
        List<RawModel> rawModels = ormQueryFactory.select().rawSelect(
                ormQueryFactory.buildQuery().from(dynamicTable)
                        .orderBy(testColumn.asc())).findAll(Wildcard.all, testColumn);

        RawModel rawModel = rawModels.get(0);
        Object columnValue1 = rawModel.getValueByPosition(0);
        Object columnValue2 = rawModel.getValueByPosition(1);
        Object columnValue3 = rawModel.getValueByPosition(2);
        assertEquals(columnValue1, value1.getValue("Id", String.class));
        assertEquals(columnValue2, value1.getValue("modificationTime", Date.class));
        assertEquals(columnValue3, value1.getValue("TestColumn", String.class));
        assertEquals(rawModels.size(), 5);
    }


    @Test
    public void testCacheFindAll() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel value1 = createDynamicModel("1");
        ormQueryFactory.insert(value1);
        LazyList<DynamicTableModel> list = ormQueryFactory.modelCacheBuilder(dynamicTable).findAll();
        assertNotNull(list);
        assertEquals(list.size(), 1);
        DynamicTableModel value2 = createDynamicModel("2");
        ormQueryFactory.insert(value2);
        list = ormQueryFactory.modelCacheBuilder(dynamicTable).findAll();
        assertEquals(list.size(), 2);
    }


    @Test
    public void testFetchOneEmpty() {
        assertNull(ormQueryFactory.select()
                .findOne(ormQueryFactory.buildQuery(),
                        TestTableVersionAnnotation.class));
    }

    @Test
    public void testFetchOne_ColumnEmpty() {
        assertNull(ormQueryFactory.select()
                .findOne(ormQueryFactory.buildQuery().from(QTestTableVersionAnnotation.qTestTableVersionAnnotation),
                        QTestTableVersionAnnotation.qTestTableVersionAnnotation.id));
    }
    @Test
    public void testFetchOne_RawEmpty() {
        assertNull(ormQueryFactory.select()
                .rawSelect(ormQueryFactory.buildQuery().from(QTestTableVersionAnnotation.qTestTableVersionAnnotation))
                .findOne(QTestTableVersionAnnotation.qTestTableVersionAnnotation.id));
    }

    @Test(expectedExceptions = IncorrectResultSizeDataAccessException.class)
    public void testFetchOne_ColumnTwoRecords() {
        TestTableVersionAnnotation value1 = new TestTableVersionAnnotation();
        TestTableVersionAnnotation value2 = new TestTableVersionAnnotation();
        ormQueryFactory.insert(value1, value2);
        ormQueryFactory.select()
                .findOne(ormQueryFactory.buildQuery().from(QTestTableVersionAnnotation.qTestTableVersionAnnotation),
                        QTestTableVersionAnnotation.qTestTableVersionAnnotation.id);
    }

    @Test(expectedExceptions = IncorrectResultSizeDataAccessException.class)
    public void testFetchOne_TwoRecords() {
        TestTableVersionAnnotation value1 = new TestTableVersionAnnotation();
        TestTableVersionAnnotation value2 = new TestTableVersionAnnotation();
        ormQueryFactory.insert(value1, value2);
        ormQueryFactory.select()
                .findOne(ormQueryFactory.buildQuery(),
                        TestTableVersionAnnotation.class);
    }

    @Test(expectedExceptions = IncorrectResultSizeDataAccessException.class)
    public void testFetchRawOne_TwoRecords() {
        TestTableVersionAnnotation value1 = new TestTableVersionAnnotation();
        TestTableVersionAnnotation value2 = new TestTableVersionAnnotation();
        ormQueryFactory.insert(value1, value2);
        ormQueryFactory.select()
                .rawSelect(ormQueryFactory.buildQuery().from(QTestTableVersionAnnotation.qTestTableVersionAnnotation)).findOne(
                        QTestTableVersionAnnotation.qTestTableVersionAnnotation.id);
    }

}
