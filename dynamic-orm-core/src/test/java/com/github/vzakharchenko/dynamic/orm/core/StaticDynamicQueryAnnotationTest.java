package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.AnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.pk.UUIDPKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.transaction.TransactionBuilder;
import com.github.vzakharchenko.dynamic.orm.model.TestTableVersionAnnotation;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersionAnnotation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class StaticDynamicQueryAnnotationTest extends AnnotationTestQueryOrm {

    @Test
    // suspend the current transaction if one exists.
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testQuery() {
        TransactionBuilder transactionManager = ormQueryFactory.transactionManager();

        //insert data to static Table
        transactionManager.startTransactionIfNeeded();
        TestTableVersionAnnotation staticTable = new TestTableVersionAnnotation();
        ormQueryFactory.insert(staticTable);
        transactionManager.commit();

        transactionManager.startTransactionIfNeeded();
        // build dynamic Table with foreign Key to Static Table
        qDynamicTableFactory.buildTables("relatedTable")
                .addColumns().addStringColumn("Id").size(255).useAsPrimaryKey().create()
                .addNumberColumn("StaticId", Integer.class).create()
                .addDateTimeColumn("modificationTime").notNull().create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(UUIDPKGenerator.getInstance()).finish()
                .addVersionColumn("modificationTime")
                .addForeignKey().buildForeignKey("StaticId", QTestTableVersionAnnotation.qTestTableVersionAnnotation,  QTestTableVersionAnnotation.qTestTableVersionAnnotation.id)
                .finish().buildSchema();

        // get dynamic table metadata
        QDynamicTable relatedTable = qDynamicTableFactory.getQDynamicTableByName("relatedTable");

        // insert to dynamic table
        DynamicTableModel relatedTableData = new DynamicTableModel(relatedTable);
        relatedTableData.addColumnValue("StaticId", staticTable.getId());

        ormQueryFactory.insert(relatedTableData);

        // select with join
        DynamicTableModel tableModel = ormQueryFactory
                .select()
                .findOne(ormQueryFactory
                                .buildQuery().from(relatedTable)
                                .innerJoin(QTestTableVersionAnnotation.qTestTableVersionAnnotation)
                                .on(relatedTable
                                        .getNumberColumnByName("StaticId", Integer.class)
                                        .eq(QTestTableVersionAnnotation
                                                .qTestTableVersionAnnotation.id))
                                .where(QTestTableVersionAnnotation
                                        .qTestTableVersionAnnotation.id.eq(staticTable.getId())),
                        relatedTable);
        assertNotNull(tableModel);
        assertEquals(tableModel.getValue("Id"), relatedTableData.getValue("Id"));
        transactionManager.commit();
    }

}
