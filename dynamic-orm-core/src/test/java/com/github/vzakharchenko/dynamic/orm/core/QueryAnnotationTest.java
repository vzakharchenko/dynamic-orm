package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.AnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.pk.UUIDPKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.transaction.TransactionBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class QueryAnnotationTest extends AnnotationTestQueryOrm {

    @Test
    // suspend the current transaction if one exists.
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testQuery() {
        TransactionBuilder transactionManager = ormQueryFactory.transactionManager();
        transactionManager.startTransactionIfNeeded();
        // build schema
        qDynamicTableFactory.buildTables("firstTable")
                .columns().addStringColumn("Id")
                .size(255).useAsPrimaryKey().createColumn()
                .addStringColumn("TestStringColumn").size(255).createColumn()
                .addDateColumn("modificationTime").createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(UUIDPKGenerator.getInstance())
                .endPrimaryKey()
                .addVersionColumn("modificationTime")
                .buildNextTable("secondTable")
                .columns().addStringColumn("Id")
                .size(255).useAsPrimaryKey().createColumn()
                .addBooleanColumn("isDeleted").notNull().createColumn()
                .addDateTimeColumn("modificationTime").notNull().createColumn()
                .addStringColumn("linkToFirstTable").size(255).createColumn()
                .addStringColumn("uniqValue").size(255).createColumn()
                .endColumns()
                .primaryKey().addPrimaryKeyGenerator(UUIDPKGenerator.getInstance()).endPrimaryKey()
                .addSoftDeleteColumn("isDeleted", true, false)
                .addVersionColumn("modificationTime")
                .index("uniqValue").addUniqueIndex()
                .foreignKey("linkToFirstTable").addForeignKey( "firstTable")
                .endBuildTables().buildSchema();
        transactionManager.commit();

        QDynamicTable firstTable = qDynamicTableFactory.getQDynamicTableByName("firstTable");
        QDynamicTable secondTable = qDynamicTableFactory.getQDynamicTableByName("secondTable");

        // insert data to the first table
        transactionManager.startTransactionIfNeeded();
        DynamicTableModel firstTableModel1 = new DynamicTableModel(firstTable);
        firstTableModel1.addColumnValue("TestStringColumn", "testValue");
        ormQueryFactory.insert(firstTableModel1);

        // insert data to the second table
        DynamicTableModel secondModel1 = new DynamicTableModel(secondTable);
        secondModel1.addColumnValue("uniqValue", "123");
        secondModel1.addColumnValue("linkToFirstTable", firstTableModel1.getValue("Id"));

        DynamicTableModel secondModel2 = new DynamicTableModel(secondTable);
        secondModel2.addColumnValue("uniqValue", "1234");
        secondModel2.addColumnValue("linkToFirstTable", firstTableModel1.getValue("Id"));

        ormQueryFactory.insert(secondModel1, secondModel2);
        transactionManager.commit();


        // add integer column to table1
        transactionManager.startTransactionIfNeeded();
        qDynamicTableFactory.buildTables("firstTable")
                .columns().addNumberColumn("newColumn", Integer.class).createColumn().endColumns()
                .endBuildTables().buildSchema();
        transactionManager.commit();


        // modify first table
        transactionManager.startTransactionIfNeeded();
        firstTableModel1.addColumnValue("newColumn", 122);
        ormQueryFactory.updateById(firstTableModel1);

        // select one value from firstTable where newColumn == 122
        DynamicTableModel firstTableFromDatabase = ormQueryFactory.select().findOne(ormQueryFactory
                        .buildQuery()
                        .from(firstTable)
                        .where(firstTable.getNumberColumnByName("newColumn").eq(122)),
                firstTable,
                DynamicTableModel.class);
        // get value of TestStringColumn from firstTable
        String testStringColumnValue = firstTableFromDatabase.getValue("TestStringColumn", String.class);
        assertEquals(testStringColumnValue, "testValue");

        // get value  from secondTable and put it to cache
        List<DynamicTableModel> tableModels = ormQueryFactory.selectCache().findAll(secondTable);
        assertEquals(tableModels.size(), 2);
        transactionManager.commit();

        // get value from cache
        ormQueryFactory.selectCache().findAll(secondTable);

        //soft delete the second row of the second Table
        transactionManager.startTransactionIfNeeded();
        ormQueryFactory.softDeleteById(secondModel2);
        transactionManager.commit();

        // get new cache records (soft deleted values are not included)
        tableModels = ormQueryFactory.selectCache().findAll(secondTable);
        assertEquals(tableModels.size(), 1);

        // select all data from all table
        List<RawModel> rawModels = ormQueryFactory.select().rawSelect(
                ormQueryFactory.buildQuery().from(firstTable)
                        .innerJoin(secondTable).on(
                        secondTable.getStringColumnByName("linkToFirstTable").eq(
                                firstTable.getStringColumnByName("Id")))
                        .where(secondTable.getBooleanColumnByName("isDeleted").eq(false)))
                .findAll(ArrayUtils.addAll(firstTable.all(), secondTable.all()));

        assertEquals(rawModels.size(), 1);
        RawModel rawModel = rawModels.get(0);
        DynamicTableModel firstModelFromJoin = rawModel.getDynamicModel(firstTable);
        DynamicTableModel secondModelFromJoin = rawModel.getDynamicModel(secondTable);
        assertEquals(firstModelFromJoin.getValue("Id"), firstTableFromDatabase.getValue("Id"));
        assertEquals(secondModelFromJoin.getValue("Id"), secondModel1.getValue("Id"));
    }

}
