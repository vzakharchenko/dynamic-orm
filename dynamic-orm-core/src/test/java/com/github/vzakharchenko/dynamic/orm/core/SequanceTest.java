package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.OracleTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.exception.EmptyBatchException;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGeneratorSequence;
import com.github.vzakharchenko.dynamic.orm.model.TestTableSequence;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableSequence;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 *
 */
public class SequanceTest extends OracleTestQueryOrm {

    @Test
    public void generateNextSEQUENCEValue() {
        PKGeneratorSequence<Number> sequance = new PKGeneratorSequence<>("TEST_SEQUENCE");
        Number number = sequance.generateNewValue(ormQueryFactory, QTestTableSequence.Q_TEST_TABLE_SEQUENCE, null);
        assertNotNull(number);
    }

    @Test
    public void insertModel() {
        TestTableSequence testTableSequence = new TestTableSequence();
        testTableSequence.setTest2(88);
        ormQueryFactory.insert(testTableSequence);
        List<TestTableSequence> testTableSequences = ormQueryFactory.select()
                .findAll(
                        ormQueryFactory.buildQuery().from(QTestTableSequence.Q_TEST_TABLE_SEQUENCE),
                        TestTableSequence.class);
        assertNotNull(testTableSequences);
        assertEquals(testTableSequences.size(), 1);
        assertNotNull(testTableSequence.getId());
        assertEquals(testTableSequence.getId(), testTableSequences.get(0).getId());
        assertEquals(testTableSequence.getTest2().intValue(), 88);
        assertEquals(testTableSequence.getTest2(), testTableSequences.get(0).getTest2());
    }

    @Test(expectedExceptions = EmptyBatchException.class)
    public void insertModelFail() {
        ormQueryFactory.insert(new ArrayList<>());
    }

    @Test
    public void insertDynamicModel() {
        qDynamicTableFactory.buildTables("new_test_Dynamic_Table")
                .columns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).useAsPrimaryKey().create()
                .addStringColumn("test_column").size(200).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence<>("TEST_SEQUENCE")).finish()
                .finish().buildSchema();

        QDynamicTable qDynamicTable = qDynamicTableFactory.getQDynamicTableByName("new_test_Dynamic_Table");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(qDynamicTable);
        dynamicTableModel.addColumnValue("test_column", "test123");

        ormQueryFactory.insert(dynamicTableModel);

        List<DynamicTableModel> testTableSequances = ormQueryFactory.select()
                .findAll(ormQueryFactory.buildQuery().from(qDynamicTable)
                        , qDynamicTable
                        );
        assertNotNull(testTableSequances);
        assertEquals(testTableSequances.size(), 1);
        assertNotNull(dynamicTableModel.getValue("ID"));
        assertEquals(dynamicTableModel.getValue("ID"), testTableSequances.get(0).getValue("ID"));
        assertEquals(dynamicTableModel.getValue("test_column", String.class), "test123");
        assertEquals(dynamicTableModel.getValue("test_column", String.class), testTableSequances.get(0).getValue("test_column", String.class));
    }
}
