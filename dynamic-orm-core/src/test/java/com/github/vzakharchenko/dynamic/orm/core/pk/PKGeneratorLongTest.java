package com.github.vzakharchenko.dynamic.orm.core.pk;

import com.github.vzakharchenko.dynamic.orm.AnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class PKGeneratorLongTest extends AnnotationTestQueryOrm {
    @Test
    public void uniq() {
        qDynamicTableFactory.buildTables("testTable")
                .columns().addNumberColumn("Id", Long.class).useAsPrimaryKey().create().finish()
                .addPrimaryKey().addPrimaryKeyGenerator(PKGeneratorLong.getInstance()).finish()
                .finish().buildSchema();
        QDynamicTable testTable = qDynamicTableFactory.getQDynamicTableByName("testTable");
        DynamicTableModel newDynamicTableModel = new DynamicTableModel(testTable);
        ormQueryFactory.insert(newDynamicTableModel);
        DynamicTableModel dynamicTableModel = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), testTable);
        assertEquals(dynamicTableModel.getValue("Id", Long.class), newDynamicTableModel.getValue("Id", Long.class));
    }
}
