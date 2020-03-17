package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.OracleTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGeneratorSequence;
import org.testng.annotations.Test;

/**
 *
 */
public class DynamicIndexTest extends OracleTestQueryOrm {

    @Test
    public void indexTest() {
        qDynamicTableFactory.buildTables("Test_table_Index")
                .columns().addNumberColumn("ID", Integer.class).size(18).useAsPrimaryKey().create()
                .addStringColumn("test_column").size(200).create()
                .addNumberColumn("INDEXED_COLUMN", Integer.class).size(38).decimalDigits(3).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence<>("TEST_SEQUENCE")).finish()
                .addIndex("INDEXED_COLUMN").buildIndex()
                .finish().buildSchema();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void indexTestFailed() {
        qDynamicTableFactory.buildTables("Test_table_Index")
                .columns().addNumberColumn("ID", Integer.class).size(18).useAsPrimaryKey().create()
                .addStringColumn("test_column").size(200).create()
                .addNumberColumn("INDEXED_COLUMN", Integer.class).size(38).decimalDigits(3).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence<>("TEST_SEQUENCE")).finish()
                .addIndex((String) null).buildIndex()
                .finish().buildSchema();
    }
    @Test(expectedExceptions = IllegalStateException.class)
    public void indexTestFailed2() {
        qDynamicTableFactory.buildTables("Test_table_Index")
                .columns().addNumberColumn("ID", Integer.class).size(18).useAsPrimaryKey().create()
                .addStringColumn("test_column").size(200).create()
                .addNumberColumn("INDEXED_COLUMN", Integer.class).size(38).decimalDigits(3).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence<>("TEST_SEQUENCE")).finish()
                .addIndex(new String[0]).buildIndex()
                .finish().buildSchema();
    }

    @Test
    public void indexUniqueTest() {
        qDynamicTableFactory.buildTables("Test_table_Index")
                .columns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).useAsPrimaryKey().create()
                .addStringColumn("test_column").size(200).create()
                .addNumberColumn("INDEXED_COLUMN", Integer.class).size(38).decimalDigits(3).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence<>("TEST_SEQUENCE")).finish()
                .addIndex("INDEXED_COLUMN").clustered().buildUniqueIndex()
                .finish().buildSchema();
    }
}
