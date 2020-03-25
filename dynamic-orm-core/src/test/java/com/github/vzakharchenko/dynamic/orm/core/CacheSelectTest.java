package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.DebugAnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class CacheSelectTest extends DebugAnnotationTestQueryOrm {

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
    public void testRawModel() {
        QDynamicTable dynamicTable = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(dynamicTable);
        dynamicTableModel.addColumnValue("TestColumn", "testData");
        ormQueryFactory.insert(dynamicTableModel);
        List<RawModel> rawModels = ormQueryFactory.selectCache().rawSelect(ormQueryFactory.buildQuery().from(dynamicTable)).findAll(dynamicTable.getStringColumnByName("TestColumn"));
        assertNotNull(rawModels);
        assertEquals(rawModels.size(), 1);
        RawModel rawModel = rawModels.get(0);
        assertNotNull(rawModel);
        assertEquals(rawModel.getDynamicModel(dynamicTable).getValue("TestColumn"), "testData");

    }
}
