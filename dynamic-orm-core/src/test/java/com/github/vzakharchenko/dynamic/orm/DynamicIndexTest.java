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
                .addColumns().addNumberColumn("ID", Integer.class).size(18).useAsPrimaryKey().create()
                .addStringColumn("test_column").size(200).create()
                .addNumberColumn("INDEXED_COLUMN", Integer.class).size(38).decimalDigits(3).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence<>("TEST_SEQUENCE")).finish()
                .addIndex().buildIndex("INDEXED_COLUMN", false)
                .buildSchema();
    }

    @Test
    public void indexUniqueTest() {
        qDynamicTableFactory.buildTable("Test_table_Index")
                .addColumns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).useAsPrimaryKey().create()
                .addStringColumn("test_column").size(200).create()
                .addNumberColumn("INDEXED_COLUMN", Integer.class).size(38).decimalDigits(3).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence<>("TEST_SEQUENCE")).finish()
                .addIndex().buildIndex("INDEXED_COLUMN", true)
                .buildSchema();
    }
}
