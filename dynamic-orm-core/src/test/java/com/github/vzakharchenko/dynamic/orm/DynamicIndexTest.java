package com.github.vzakharchenko.dynamic.orm;

import com.github.vzakharchenko.dynamic.orm.core.pk.PKGeneratorSequence;
import org.testng.annotations.Test;

/**
 *
 */
public class DynamicIndexTest extends OracleTestQueryOrm {

    @Test
    public void indexTest() {
        qDynamicTableFactory.buildTable("Test_table_Index")
                .addPrimaryNumberKey("ID", Integer.class, 18, 0)
                .addPrimaryKeyGenerator(new PKGeneratorSequence<>("TEST_SEQUENCE"))
                .createStringColumn("test_column", 200, false)
                .createNumberColumn("INDEXED_COLUMN", Integer.class, 38, 3, false)
                .addIndex("INDEXED_COLUMN", false)
                .buildSchema();
    }

    @Test
    public void indexUniqueTest() {
        qDynamicTableFactory.buildTable("Test_table_Index")
                .addPrimaryNumberKey("ID", Integer.class, 18, 0)
                .addPrimaryKeyGenerator(new PKGeneratorSequence<>("TEST_SEQUENCE"))
                .createStringColumn("test_column", 200, false)
                .createNumberColumn("INDEXED_COLUMN", Integer.class, 38, 3, false)
                .addIndex("INDEXED_COLUMN", true)
                .buildSchema();
    }
}
