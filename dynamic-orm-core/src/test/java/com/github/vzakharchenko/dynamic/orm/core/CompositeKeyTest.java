package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.DebugAnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKeyBuilder;
import com.github.vzakharchenko.dynamic.orm.model.TestTableCache;
import com.github.vzakharchenko.dynamic.orm.model.TestTableCompositePrimaryKey;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableCompositePrimaryKey;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public class CompositeKeyTest extends DebugAnnotationTestQueryOrm {


    private TestTableCompositePrimaryKey createStaticDml(Integer id1, String id2, Integer test2) {
        TestTableCompositePrimaryKey table = new TestTableCompositePrimaryKey();
        table.setId1(id1);
        table.setId2(id2);
        table.setTestColumn(test2);
        return table;
    }

    @Test
    public void testInsertCompositeStaticTable() {
        TestTableCompositePrimaryKey data1 = createStaticDml(1, "key1", 11);
        TestTableCompositePrimaryKey data2 = createStaticDml(2, "key2", 11);
        ormQueryFactory.insert(data1, data2);
        List<TestTableCompositePrimaryKey> primaryKeys =
                ormQueryFactory.select().findAll(ormQueryFactory.buildQuery(), TestTableCompositePrimaryKey.class);
        assertNotNull(primaryKeys);
        assertEquals(primaryKeys.size(), 2);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testInsertCompositeStaticTableKeyIsNull() {
        TestTableCompositePrimaryKey data = createStaticDml(null, null, 11);
        ormQueryFactory.insert(data);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testInsertCompositeStaticTableOneKeyIsNull() {
        TestTableCompositePrimaryKey data = createStaticDml(1, null, 11);
        ormQueryFactory.insert(data);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testInsertCompositeStaticTableFailed() {
        ormQueryFactory.modify(QTestTableCompositePrimaryKey.qTestTableCompositePrimaryKey, TestTableCache.class).insert(new TestTableCache());
    }

    @Test
    public void testSelectCache() {
        TestTableCompositePrimaryKey data1 = createStaticDml(1, "key1", 11);
        // insert data
        ormQueryFactory.insert(data1);
        // fetch data from database and put result to the cache
        List<TestTableCompositePrimaryKey> tableCompositePrimaryKeys = ormQueryFactory.selectCache().findAll(TestTableCompositePrimaryKey.class);
        assertEquals(tableCompositePrimaryKeys.size(), 1);
        // get result from the cache
        List<TestTableCompositePrimaryKey> tableCompositePrimaryKeysCache = ormQueryFactory.selectCache().findAll(TestTableCompositePrimaryKey.class);
        // get result from the cache
        // check value the same
        assertEquals(tableCompositePrimaryKeys.get(0).getId1(), tableCompositePrimaryKeysCache.get(0).getId1());
        TestTableCompositePrimaryKey data2 = createStaticDml(2, "key2", 11);
        // insert a new value and evict cache
        ormQueryFactory.insert(data2);

        // fetch data from database and put result to the cache
        tableCompositePrimaryKeys = ormQueryFactory.selectCache().findAll(TestTableCompositePrimaryKey.class);

        assertEquals(tableCompositePrimaryKeys.size(), 2);

    }

    @Test
    public void testSelectSmartCache() {
        TestTableCompositePrimaryKey data1 = createStaticDml(1, "key1", 11);
        // insert data
        ormQueryFactory.insert(data1);
        QTestTableCompositePrimaryKey qTable = QTestTableCompositePrimaryKey.qTestTableCompositePrimaryKey;
        List<TestTableCompositePrimaryKey> testTableCompositePrimaryKeys = ormQueryFactory
                .modelCacheBuilder(TestTableCompositePrimaryKey.class)
                .findAllByIds(Arrays.asList(CompositeKeyBuilder.create(qTable)
                        .addPrimaryKey(qTable.id1, 1)
                        .addPrimaryKey(qTable.id2, "key1")
                        .build()
                ));
        assertNotNull(testTableCompositePrimaryKeys);
        assertEquals(testTableCompositePrimaryKeys.size(), 1);
        assertEquals(testTableCompositePrimaryKeys.get(0).getId1(), Integer.valueOf(1));
        assertEquals(testTableCompositePrimaryKeys.get(0).getId2(), "key1");
        assertEquals(testTableCompositePrimaryKeys.get(0).getTestColumn(), Integer.valueOf(11));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSelectSmartCacheAddExtraCache() {
        TestTableCompositePrimaryKey data1 = createStaticDml(1, "key1", 11);
        // insert data
        ormQueryFactory.insert(data1);
        QTestTableCompositePrimaryKey qTable = QTestTableCompositePrimaryKey.qTestTableCompositePrimaryKey;ormQueryFactory
                .modelCacheBuilder(TestTableCompositePrimaryKey.class)
                .findAllByIds(Arrays.asList(CompositeKeyBuilder.create(qTable)
                        .addPrimaryKey(qTable.id1, 1)
                        .addPrimaryKey(qTable.id2, "key1")
                        .addPrimaryKey(qTable.testColumn, 123)
                        .build()
                ));

    }
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Type mismatch: class java.lang.String is not accessible from class java.lang.Integer")
    public void testSelectSmartCacheWrongType() {
        TestTableCompositePrimaryKey data1 = createStaticDml(1, "key1", 11);
        // insert data
        ormQueryFactory.insert(data1);
        QTestTableCompositePrimaryKey qTable = QTestTableCompositePrimaryKey.qTestTableCompositePrimaryKey;ormQueryFactory
                .modelCacheBuilder(TestTableCompositePrimaryKey.class)
                .findAllByIds(Arrays.asList(CompositeKeyBuilder.create(qTable)
                        .addPrimaryKey(qTable.id1, "1")
                        .addPrimaryKey(qTable.id2, "key1")
                        .build()
                ));

    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "Primary key value is null")
    public void testSelectSmartCacheFailedJustValue() {
        TestTableCompositePrimaryKey data1 = createStaticDml(1, "key1", 11);
        // insert data
        ormQueryFactory.insert(data1);
        ormQueryFactory
                .modelCacheBuilder(TestTableCompositePrimaryKey.class)
                .findAllByIds(Arrays.asList(1));
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "Primary key value is null")
    public void testSelectSmartCacheOnlyOneKey() {
        TestTableCompositePrimaryKey data1 = createStaticDml(1, "key1", 11);
        // insert data
        ormQueryFactory.insert(data1);
        QTestTableCompositePrimaryKey qTable = QTestTableCompositePrimaryKey.qTestTableCompositePrimaryKey;
        ormQueryFactory
                .modelCacheBuilder(TestTableCompositePrimaryKey.class)
                .findAllByIds(Arrays.asList(CompositeKeyBuilder.create(qTable)
                        .addPrimaryKey(qTable.id1, 1)
                        .build()
                ));
    }
}
