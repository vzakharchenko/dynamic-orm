package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.OracleTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.cache.LazyList;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGeneratorInteger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;


/**
 *
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class DynamicCacheTest extends OracleTestQueryOrm {


    @Test
    public void testCacheSelect() {
        QTableBuilder testTable1 = qDynamicTableFactory.buildTables("testtable1");
        // build primary key
        testTable1.columns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).useAsPrimaryKey().create();

        // build String Field

        testTable1.columns().addStringColumn("STRING_Test_FIELD").size( 200).create();

        // validate and create structure

        testTable1.finish().buildSchema();
        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("testtable1");
        assertNotNull(qDynamicTable);


        DynamicTableModel dynamicTableModel1 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel1.addColumnValue("ID", 1000);
        dynamicTableModel1.addColumnValue("STRING_Test_FIELD", "test123");

        DynamicTableModel dynamicTableModel2 = new DynamicTableModel(qDynamicTable);
        dynamicTableModel2.addColumnValue("STRING_Test_FIELD", "model 2 value");

        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        ormQueryFactory.modify(qDynamicTable).primaryKeyGenerator(PKGeneratorInteger.getInstance()).insert(dynamicTableModel1, dynamicTableModel2);
        ormQueryFactory.transactionManager().commit();

        List<DynamicTableModel> tableModels = ormQueryFactory.selectCache().findAll(ormQueryFactory.buildQuery().from(qDynamicTable), qDynamicTable);

        assertNotNull(tableModels);
        assertEquals(tableModels.size(), 2);

        DynamicTableModel tableModel1 = tableModels.get(0);
        DynamicTableModel tableModel2 = tableModels.get(1);
        assertEquals(tableModel1.getValue("ID", Integer.class).intValue(), 1000);
        assertEquals(tableModel1.getValue("STRING_Test_FIELD", String.class), "test123");
        assertEquals(tableModel2.getValue("STRING_Test_FIELD", String.class), "model 2 value");
        assertNotNull(tableModel2.getValue("ID", Integer.class));

    }

}
