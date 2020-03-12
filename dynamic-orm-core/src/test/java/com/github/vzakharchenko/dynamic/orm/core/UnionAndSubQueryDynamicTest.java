package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.DebugAnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;
import com.github.vzakharchenko.dynamic.orm.core.query.UnionBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UnionAndSubQueryDynamicTest extends DebugAnnotationTestQueryOrm {

    @BeforeMethod
    public void beforeMethod() {
        // build structure
        qDynamicTableFactory.buildTables("UnionTable1")
                .addColumns().addStringColumn("Id1").size(255).useAsPrimaryKey().create()
                .addDateTimeColumn("modificationTime1").notNull().create()
                .addStringColumn("TestColumn1_1").size(255).create()
                .addStringColumn("TestColumn1_2").size(255).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).finish()
                .addVersionColumn("modificationTime1")
                .buildNextTable("UnionTable2")
                .addColumns()
                .addStringColumn("Id2").size(255).useAsPrimaryKey().create()
                .addDateTimeColumn("modificationTime2").notNull().create()
                .addStringColumn("TestColumn2_1").size(255).create()
                .addStringColumn("TestColumn2_2").size(255).create()
                .finish()
                .addPrimaryKey().addPrimaryKeyGenerator(PrimaryKeyGenerators.UUID.getPkGenerator()).finish()
                .addVersionColumn("modificationTime2")
                .finish()
                .buildSchema();
    }

    public DynamicTableModel insert1(String testColumn11Value, String testColumn12Value) {
        QDynamicTable unionTable1 = qDynamicTableFactory.getQDynamicTableByName("UnionTable1");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(unionTable1);
        dynamicTableModel.addColumnValue("TestColumn1_1", testColumn11Value);
        dynamicTableModel.addColumnValue("TestColumn1_2", testColumn12Value);
        ormQueryFactory.insert(dynamicTableModel);
        return dynamicTableModel;
    }

    public DynamicTableModel insert2(String testColumn21Value, String testColumn22Value) {
        QDynamicTable unionTable = qDynamicTableFactory.getQDynamicTableByName("UnionTable2");
        DynamicTableModel dynamicTableModel = new DynamicTableModel(unionTable);
        dynamicTableModel.addColumnValue("TestColumn2_1", testColumn21Value);
        dynamicTableModel.addColumnValue("TestColumn2_2", testColumn22Value);
        ormQueryFactory.insert(dynamicTableModel);
        return dynamicTableModel;
    }


    @Test
    public void testSimpleSubQueryQuery() {
        // inserts
        insert1("same", "data1");
        insert2("same", "data2");
        // get unionTable1 Metadata
        QDynamicTable unionTable1 = qDynamicTableFactory.getQDynamicTableByName("UnionTable1");
        // get unionTable2 Metadata
        QDynamicTable unionTable2 = qDynamicTableFactory.getQDynamicTableByName("UnionTable2");

        // get column from unionTable1
        StringPath testColumn11 = unionTable1.getStringColumnByName("TestColumn1_1");
        // get columns from unionTable2
        StringPath testColumn21 = unionTable2.getStringColumnByName("TestColumn2_1");
        StringPath testColumn22 = unionTable2.getStringColumnByName("TestColumn2_2");

        // create subquery
        SQLQuery<String> query = SQLExpressions
                .select(testColumn21)
                .from(unionTable2).where(testColumn22.eq("data2"));

        // generate sql
        String sql = ormQueryFactory.select().showSql(ormQueryFactory.buildQuery().from(unionTable1)
                .where(testColumn11.in(query)), unionTable1, DynamicTableModel.class);

        assertEquals(sql, "select \"UNIONTABLE1\".\"ID1\", \"UNIONTABLE1\".\"MODIFICATIONTIME1\", \"UNIONTABLE1\".\"TESTCOLUMN1_1\", \"UNIONTABLE1\".\"TESTCOLUMN1_2\"\n" +
                "from \"UNIONTABLE1\" \"UNIONTABLE1\"\n" +
                "where \"UNIONTABLE1\".\"TESTCOLUMN1_1\" in (select \"UNIONTABLE2\".\"TESTCOLUMN2_1\"\n" +
                "from \"UNIONTABLE2\" \"UNIONTABLE2\"\n" +
                "where \"UNIONTABLE2\".\"TESTCOLUMN2_2\" = 'data2')");

        // get data
        DynamicTableModel tableModel = ormQueryFactory.select().findOne(
                ormQueryFactory.buildQuery().from(unionTable1)
                        .where(testColumn11.in(query)), unionTable1, DynamicTableModel.class);
        assertEquals(tableModel.getValue("TestColumn1_2", String.class), "data1");

    }

    @Test
    public void testUnionQuery() {
        // inserts
        insert1("same", "data1");
        insert2("same", "data2");
        // get unionTable1 Metadata
        QDynamicTable unionTable1 = qDynamicTableFactory.getQDynamicTableByName("UnionTable1");
        // get unionTable2 Metadata
        QDynamicTable unionTable2 = qDynamicTableFactory.getQDynamicTableByName("UnionTable2");
        // get column from unionTable1
        StringPath testColumn11 = unionTable1.getStringColumnByName("TestColumn1_1");
        StringPath testColumn12 = unionTable1.getStringColumnByName("TestColumn1_2");
        StringPath testColumn21 = unionTable2.getStringColumnByName("TestColumn2_1");
        StringPath testColumn22 = unionTable2.getStringColumnByName("TestColumn2_2");


        SQLQuery<Tuple> query1 = SQLExpressions
                .select(testColumn11.as("column1"), testColumn12.as("column2"))
                .from(unionTable1).where(testColumn12.eq("data1"));

        SQLQuery<Tuple> query2 = SQLExpressions
                .select(testColumn21.as("column1"), testColumn22.as("column2"))
                .from(unionTable2).where(testColumn22.eq("data2"));

        // create UnionBuilder
        UnionBuilder unionBuilder = ormQueryFactory.select()
                .union(ormQueryFactory.buildQuery(), query1, query2);
        // result order by
        unionBuilder
                .orderBy("column1").desc().orderBy("column2").asc();
        // offset and limit (offset = 0, limit = 2 )
        unionBuilder.limit(new Range(0, 2));
        // group by result
        unionBuilder.groupBy("column1", "column2");
        // generateSql
        String sql = unionBuilder.showSql();
        assertEquals(sql, "select \"column1\", \"column2\"\n" +
                "from ((select \"UNIONTABLE1\".\"TESTCOLUMN1_1\" as \"column1\", \"UNIONTABLE1\".\"TESTCOLUMN1_2\" as \"column2\"\n" +
                "from \"UNIONTABLE1\" \"UNIONTABLE1\"\n" +
                "where \"UNIONTABLE1\".\"TESTCOLUMN1_2\" = 'data1')\n" +
                "union\n" +
                "(select \"UNIONTABLE2\".\"TESTCOLUMN2_1\" as \"column1\", \"UNIONTABLE2\".\"TESTCOLUMN2_2\" as \"column2\"\n" +
                "from \"UNIONTABLE2\" \"UNIONTABLE2\"\n" +
                "where \"UNIONTABLE2\".\"TESTCOLUMN2_2\" = 'data2')) as \"union\"\n" +
                "group by \"column1\", \"column2\"\n" +
                "order by \"column1\" desc, \"column2\" asc\n" +
                "limit 2\n" +
                "offset 0");

        // get result
        List<RawModel> rawModels = unionBuilder.findAll();
        // get first record
        RawModel rawModel = rawModels.get(0);
        // get column1 value
        String column1Value = rawModel.getValueByColumnName("column1", String.class);
        // get column2 value
        String column2Value = rawModel.getValueByColumnName("column2", String.class);

        assertEquals(rawModels.size(), 2);

        assertEquals(column1Value, "same");
        assertEquals(column2Value, "data1");

    }

    @Test
    public void testUnionAllQuery() {
        // inserts
        insert1("same", "data1");
        insert2("same", "data2");
        // get unionTable1 Metadata
        QDynamicTable unionTable1 = qDynamicTableFactory.getQDynamicTableByName("UnionTable1");
        // get unionTable2 Metadata
        QDynamicTable unionTable2 = qDynamicTableFactory.getQDynamicTableByName("UnionTable2");
        // get column from unionTable1
        StringPath testColumn11 = unionTable1.getStringColumnByName("TestColumn1_1");
        StringPath testColumn12 = unionTable1.getStringColumnByName("TestColumn1_2");
        StringPath testColumn21 = unionTable2.getStringColumnByName("TestColumn2_1");
        StringPath testColumn22 = unionTable2.getStringColumnByName("TestColumn2_2");

        // first subquery
        SQLQuery<Tuple> query1 = SQLExpressions
                .select(testColumn11.as("column1"), testColumn12.as("column2"))
                .from(unionTable1).where(testColumn12.eq("data1"));
        // second subquery
        SQLQuery<Tuple> query2 = SQLExpressions
                .select(testColumn21.as("column1"), testColumn22.as("column2"))
                .from(unionTable2).where(testColumn22.eq("data2"));

        // create UnionBuilder
        UnionBuilder unionBuilder = ormQueryFactory.select()
                .unionAll(ormQueryFactory.buildQuery(), query1, query2);
        // result order by
        unionBuilder
                .orderBy("column1").desc().orderBy("column2").asc();
        // offset and limit (offset = 0, limit = 2 )
        unionBuilder.limit(new Range(0, 2));
        // group by result
        unionBuilder.groupBy("column1", "column2");
        // generateSql
        String sql = unionBuilder.showSql();
        assertEquals(sql, "select \"column1\", \"column2\"\n" +
                "from ((select \"UNIONTABLE1\".\"TESTCOLUMN1_1\" as \"column1\", \"UNIONTABLE1\".\"TESTCOLUMN1_2\" as \"column2\"\n" +
                "from \"UNIONTABLE1\" \"UNIONTABLE1\"\n" +
                "where \"UNIONTABLE1\".\"TESTCOLUMN1_2\" = 'data1')\n" +
                "union all\n" +
                "(select \"UNIONTABLE2\".\"TESTCOLUMN2_1\" as \"column1\", \"UNIONTABLE2\".\"TESTCOLUMN2_2\" as \"column2\"\n" +
                "from \"UNIONTABLE2\" \"UNIONTABLE2\"\n" +
                "where \"UNIONTABLE2\".\"TESTCOLUMN2_2\" = 'data2')) as \"union\"\n" +
                "group by \"column1\", \"column2\"\n" +
                "order by \"column1\" desc, \"column2\" asc\n" +
                "limit 2\n" +
                "offset 0");

        // get result
        List<RawModel> rawModels = unionBuilder.findAll();
        // get first record
        RawModel rawModel = rawModels.get(0);
        // get column1 value
        String column1Value = rawModel.getValueByColumnName("column1", String.class);
        // get column2 value
        String column2Value = rawModel.getValueByColumnName("column2", String.class);

        assertEquals(rawModels.size(), 2);

        assertEquals(column1Value, "same");
        assertEquals(column2Value, "data1");

    }

    @Test
    public void testCountUnionQueryWithCache() {
        // inserts
        insert1("same", "data1"); //   ormQueryFactory.insert(unionTable1);
        insert2("same", "data2");//   ormQueryFactory.insert(unionTable2);
        // get unionTable1 Metadata
        QDynamicTable unionTable1 = qDynamicTableFactory.getQDynamicTableByName("UnionTable1");
        // get unionTable2 Metadata
        QDynamicTable unionTable2 = qDynamicTableFactory.getQDynamicTableByName("UnionTable2");
        // get column from unionTable1
        StringPath id1 = unionTable1.getStringColumnByName("Id1");
        // get column from unionTable2
        StringPath id2 = unionTable2.getStringColumnByName("Id2");


        SQLQuery<String> query1 = SQLExpressions
                .select(id1)
                .from(unionTable1);

        SQLQuery<String> query2 = SQLExpressions
                .select(id2)
                .from(unionTable2);

        // create UnionBuilder
        UnionBuilder unionBuilder = ormQueryFactory.selectCache()
                .unionAll(ormQueryFactory.buildQuery(), query1, query2);

        // unionBuilder.groupBy("column1", "column2");

        // generateSql
        String sql = unionBuilder.showCountSql();
        assertEquals(sql, "select count(*)\n" +
                "from ((select \"UNIONTABLE1\".\"ID1\"\n" +
                "from \"UNIONTABLE1\" \"UNIONTABLE1\")\n" +
                "union all\n" +
                "(select \"UNIONTABLE2\".\"ID2\"\n" +
                "from \"UNIONTABLE2\" \"UNIONTABLE2\")) as \"union\"");

        // get result
        Long count1 = unionBuilder.count();
        // result from cache
        Long count2 = unionBuilder.count();
        // insert to unionTable1
        insert2("someData", "data3"); //   ormQueryFactory.insert(unionTable2);
        // cache is evicted and get a new value
        Long count3 = unionBuilder.count();

        assertEquals(count1, Long.valueOf(2L));
        assertTrue(count1 == count2);
        assertEquals(count3, Long.valueOf(3L));

    }

}
