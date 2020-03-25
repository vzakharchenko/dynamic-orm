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
                .columns().addNumberColumn("ID", Integer.class).size(18).useAsPrimaryKey().createColumn()
                .addStringColumn("test_column").size(200).createColumn()
                .addNumberColumn("INDEXED_COLUMN", Integer.class).size(38).decimalDigits(3).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence<>("TEST_SEQUENCE")).endPrimaryKey()
                .index("INDEXED_COLUMN").addIndex()
                .endBuildTables().buildSchema();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void indexTestFailed() {
        qDynamicTableFactory.buildTables("Test_table_Index")
                .columns().addNumberColumn("ID", Integer.class).size(18).useAsPrimaryKey().createColumn()
                .addStringColumn("test_column").size(200).createColumn()
                .addNumberColumn("INDEXED_COLUMN", Integer.class).size(38).decimalDigits(3).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence<>("TEST_SEQUENCE")).endPrimaryKey()
                .index((String) null).addIndex()
                .endBuildTables().buildSchema();
    }
    @Test(expectedExceptions = IllegalStateException.class)
    public void indexTestFailed2() {
        qDynamicTableFactory.buildTables("Test_table_Index")
                .columns().addNumberColumn("ID", Integer.class).size(18).useAsPrimaryKey().createColumn()
                .addStringColumn("test_column").size(200).createColumn()
                .addNumberColumn("INDEXED_COLUMN", Integer.class).size(38).decimalDigits(3).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence<>("TEST_SEQUENCE")).endPrimaryKey()
                .index(new String[0]).addIndex()
                .endBuildTables().buildSchema();
    }

    @Test
    public void indexUniqueTest() {
        qDynamicTableFactory.buildTables("Test_table_Index")
                .columns().addNumberColumn("ID", Integer.class).size(18).decimalDigits(0).useAsPrimaryKey().createColumn()
                .addStringColumn("test_column").size(200).createColumn()
                .addNumberColumn("INDEXED_COLUMN", Integer.class).size(38).decimalDigits(3).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(new PKGeneratorSequence<>("TEST_SEQUENCE")).endPrimaryKey()
                .index("INDEXED_COLUMN").clustered().addUniqueIndex()
                .endBuildTables().buildSchema();
    }
}
