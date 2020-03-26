package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.DebugAnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKeyBuilder;
import org.springframework.cache.Cache;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class FindByIdCacheTest extends DebugAnnotationTestQueryOrm {

    @BeforeMethod
    public void beforeTests() {
        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        qDynamicTableFactory.buildTables("table")
                .columns().addStringColumn("Id1").size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("Id2").size(255).useAsPrimaryKey().createColumn()
                .addNumberColumn("pos", Integer.class).size(32).notNull().createColumn()
                .endColumns().endBuildTables().buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("table");
        List<DynamicTableModel> inserts = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
            dynamicTableModel.addColumnValue("Id1", String.valueOf(i));
            dynamicTableModel.addColumnValue("Id2", String.valueOf(i));
            dynamicTableModel.addColumnValue("pos", i);
            inserts.add(dynamicTableModel);
        }
        ormQueryFactory.insert(inserts);
        ormQueryFactory.transactionManager().commit();
        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            cache.clear();
        }
    }

    @Test
    public void testLazyList() {
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("table");
        List<CompositeKey> compositeKeys = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            String value = String.valueOf(i);
            compositeKeys.add(CompositeKeyBuilder.create(table)
                    .addPrimaryKey("Id1", value).addPrimaryKey("Id2", value).build());
        }
        List<DynamicTableModel> allByIds = ormQueryFactory.modelCacheBuilder(table).findAllByIds(compositeKeys);
        assertNotNull(allByIds);
        assertEquals(allByIds.size(), 400);
        DynamicTableModel dynamicTableModel = allByIds.get(88);
        assertEquals(dynamicTableModel.getValue("id2"), "88");
        for (int i = 0; i < 90; i++) {
            dynamicTableModel = allByIds.get(i);
            assertEquals(dynamicTableModel.getValue("id2"), String.valueOf(i));
        }

        for (int i = 200; i < 300; i++) {
            dynamicTableModel = allByIds.get(i);
            assertEquals(dynamicTableModel.getValue("id2"), String.valueOf(i));
        }
    }


    @Test
    public void testLazyListOneKey() {
        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        qDynamicTableFactory.buildTables("tableWithOnePrimaryKey")
                .columns()
                .addStringColumn("Id").size(32).useAsPrimaryKey().createColumn()
                .endColumns()
                .endBuildTables().buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("tableWithOnePrimaryKey");
        List<DynamicTableModel> inserts = new ArrayList<>();
        List<CompositeKey> compositeKeys = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
            String value = String.valueOf(i);
            dynamicTableModel.addColumnValue("Id", value);
            compositeKeys.add(CompositeKeyBuilder.create(table)
                    .addPrimaryKey("Id", value).build());
            inserts.add(dynamicTableModel);
        }
        ormQueryFactory.insert(inserts);
        ormQueryFactory.transactionManager().commit();
        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            cache.clear();
        }

        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        List<DynamicTableModel> allByIds = ormQueryFactory.modelCacheBuilder(table).findAllByIds(compositeKeys);
        assertNotNull(allByIds);
        assertEquals(allByIds.size(), 500);
        DynamicTableModel dynamicTableModel = allByIds.get(88);
        assertEquals(dynamicTableModel.getValue("id"), "88");
        for (int i = 0; i < 90; i++) {
            dynamicTableModel = allByIds.get(i);
            assertEquals(dynamicTableModel.getValue("id"), String.valueOf(i));
        }
        for (int i = 200; i < 300; i++) {
            dynamicTableModel = allByIds.get(i);
            assertEquals(dynamicTableModel.getValue("id"), String.valueOf(i));
        }
        ormQueryFactory.transactionManager().commit();
    }
}
