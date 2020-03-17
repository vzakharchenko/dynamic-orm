package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.DebugAnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;
import com.querydsl.core.QueryException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class ModifySchemaTest extends DebugAnnotationTestQueryOrm {

    @BeforeMethod
    public void createDefaultSchema() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns().addStringColumn("Id").size(255).useAsPrimaryKey().create()
                .addDateTimeColumn("modificationTime").notNull().create()
                .addStringColumn("TestColumn").notNull().size(255).create()
                .addNumberColumn("TestColumnNull", Integer.class).nullable().size(255).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).finish()
                .addVersionColumn("modificationTime").finish().buildSchema();
    }

    @Test
    public void testDeleteColumnModificationSchema() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns()
                    .modifyColumn()
                    .removeColumn("TestColumn")
                    .finish()
                .finish()
                .finish().buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        ormQueryFactory.insert(dynamicTableModel);
    }

    @Test
    public void testColumnModificationSchema() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns()
                    .modifyColumn()
                    .size("TestColumn", 1)
                    .finish()
                .finish()
                .finish()
                .buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        dynamicTableModel.addColumnValue("TestColumn", "1");
        ormQueryFactory.insert(dynamicTableModel);
    }

    @Test(expectedExceptions = QueryException.class)
    public void testColumnModificationSchemaFailed() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns()
                .modifyColumn()
                .size("TestColumn", 1).finish().finish().finish().buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        dynamicTableModel.addColumnValue("TestColumn", "Size bigger than 1");
        ormQueryFactory.insert(dynamicTableModel);
    }

    @Test
    public void testColumnModificationSchemaNullable() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns()
                .modifyColumn()
                .nullable("TestColumn").finish().finish().finish().buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        ormQueryFactory.insert(dynamicTableModel);
    }

    @Test
    public void testColumnModificationSchemaDecimalDigits() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns()
                .modifyColumn()
                .decimalDigits("TestColumnNull", 10)
                .changeNumberType("TestColumnNull", Double.class).finish().finish().finish().buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        dynamicTableModel.addColumnValue("TestColumnNull", 1.1);
        dynamicTableModel.addColumnValue("TestColumn", "22");
        ormQueryFactory.insert(dynamicTableModel);
        List<DynamicTableModel> tableModels = ormQueryFactory.select().findAll(table);
        DynamicTableModel tableModel = tableModels.get(0);
        assertEquals(tableModel.getValue("TestColumnNull", Double.class).doubleValue(), 1.1);
    }

    @Test(expectedExceptions = QueryException.class)
    public void testColumnModificationSchemaNotNull() {
        qDynamicTableFactory.buildTables("DynamicTable")
                .columns()
                .modifyColumn()
                .notNull("TestColumnNull").finish().finish().finish().buildSchema();
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("DynamicTable");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(table);
        ormQueryFactory.insert(dynamicTableModel);
    }
}
