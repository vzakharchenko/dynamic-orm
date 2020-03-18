package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.DebugAnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotNull;

public class LoadCurrentSchemaTest extends DebugAnnotationTestQueryOrm {
    @Test
    public void testLoadCurrentSchema(){
        qDynamicTableFactory.loadCurrentSchema();
        QDynamicTable dtable = qDynamicTableFactory.getQDynamicTableByName("TEST_TABLE_VERSION_ANNOTATION");
        List<DynamicTableModel> all = ormQueryFactory.select().findAll(dtable);
        assertNotNull(all);
    }
}
