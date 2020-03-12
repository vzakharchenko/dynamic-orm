package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.OracleTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.RawModel;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.CrudBuilder;
import com.github.vzakharchenko.dynamic.orm.model.TestTableCache;
import com.github.vzakharchenko.dynamic.orm.model.Testtable;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableCache;
import com.github.vzakharchenko.dynamic.orm.qModel.QTesttable;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.SQLCommonQuery;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 *
 */
public class RawModelTest extends OracleTestQueryOrm {

    @Test
    public void testJoinColumns() {
        CrudBuilder<Testtable> crudBuilder1 = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);
        CrudBuilder<TestTableCache> crudBuilder2 = ormQueryFactory.modify(QTestTableCache.testTableCache, TestTableCache.class);

        Testtable testtable1 = new Testtable();
        testtable1.setId(0);
        testtable1.setTest2(2);
        crudBuilder1.insert(testtable1);

        Testtable testtable2 = new Testtable();
        testtable2.setId(1);
        testtable2.setTest2(4);
        crudBuilder1.insert(testtable2);

        TestTableCache testtable21 = new TestTableCache();
        testtable21.setId(0);
        testtable21.setTest2(22);
        crudBuilder2.insert(testtable21);

        TestTableCache testtable22 = new TestTableCache();
        testtable22.setId(1);
        testtable22.setTest2(44);
        crudBuilder2.insert(testtable22);

        TestTableCache testtable23 = new TestTableCache();
        testtable23.setId(2);
        testtable23.setTest2(44);
        crudBuilder2.insert(testtable23);

        SQLCommonQuery<?> query = ormQueryFactory.buildQuery()
                .from(QTesttable.testtable)
                .innerJoin(QTestTableCache.testTableCache).on(QTestTableCache.testTableCache.id.eq(QTesttable.testtable.id))
                .groupBy(QTestTableCache.testTableCache.test2);
        List<RawModel> rawModels = ormQueryFactory.select().rawSelect(query).findAll(QTestTableCache.testTableCache.test2, Wildcard.count);
        String sql = ormQueryFactory.select().rawSelect(query).showSql(QTestTableCache.testTableCache.test2, Wildcard.count);

        assertEquals(sql, "select \"TEST_TABLE_CACHE\".\"TEST2\", count(*)\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "inner join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "group by \"TEST_TABLE_CACHE\".\"TEST2\"");

        assertNotNull(rawModels);
        assertEquals(rawModels.size(), 2);
        RawModel rawModel1 = rawModels.get(0);
        RawModel rawModel2 = rawModels.get(1);

        assertEquals(rawModel1.getAliasValue(QTestTableCache.testTableCache.test2), Integer.valueOf(22));
        assertEquals(rawModel1.getColumnValue(QTestTableCache.testTableCache.test2), Integer.valueOf(22));
        assertEquals(rawModel1.getAliasValue(Wildcard.count), Long.valueOf(1));

        assertEquals(rawModel2.getAliasValue(QTestTableCache.testTableCache.test2), Integer.valueOf(44));
        assertEquals(rawModel2.getColumnValue(QTestTableCache.testTableCache.test2), Integer.valueOf(44));
        assertEquals(rawModel2.getValueByPosition(0), Integer.valueOf(44));
        assertEquals(rawModel2.getAliasValue(Wildcard.count), Long.valueOf(1));
        assertEquals(rawModel2.getValueByPosition(1), Long.valueOf(1));
    }

    @Test
    public void testOneJoinColumns() {
        CrudBuilder<Testtable> crudBuilder1 = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);
        CrudBuilder<TestTableCache> crudBuilder2 = ormQueryFactory.modify(QTestTableCache.testTableCache, TestTableCache.class);

        Testtable testtable1 = new Testtable();
        testtable1.setId(0);
        testtable1.setTest2(2);
        crudBuilder1.insert(testtable1);

        Testtable testtable2 = new Testtable();
        testtable2.setId(1);
        testtable2.setTest2(4);
        crudBuilder1.insert(testtable2);

        TestTableCache testtable21 = new TestTableCache();
        testtable21.setId(0);
        testtable21.setTest2(22);
        crudBuilder2.insert(testtable21);

        TestTableCache testtable22 = new TestTableCache();
        testtable22.setId(1);
        testtable22.setTest2(44);
        crudBuilder2.insert(testtable22);

        TestTableCache testtable23 = new TestTableCache();
        testtable23.setId(2);
        testtable23.setTest2(44);
        crudBuilder2.insert(testtable23);

        SQLCommonQuery<?> query = ormQueryFactory.buildQuery()
                .from(QTesttable.testtable)
                .innerJoin(QTestTableCache.testTableCache).on(QTestTableCache.testTableCache.id.eq(QTesttable.testtable.id));
        RawModel rawModel = ormQueryFactory.select().rawSelect(query).findOne(QTestTableCache.testTableCache.test2.max(), QTestTableCache.testTableCache.test2.min());
        String sql = ormQueryFactory.select().rawSelect(query).showSql(QTestTableCache.testTableCache.test2.max(), QTestTableCache.testTableCache.test2.min());

        assertEquals(sql, "select max(\"TEST_TABLE_CACHE\".\"TEST2\"), min(\"TEST_TABLE_CACHE\".\"TEST2\")\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "inner join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"");

        assertNotNull(rawModel);
        assertEquals(rawModel.getAliasValue(QTestTableCache.testTableCache.test2.max()), Integer.valueOf(44));
        assertEquals(rawModel.getValueByPosition(0), Integer.valueOf(44));
        assertEquals(rawModel.getAliasValue(QTestTableCache.testTableCache.test2.min()), Integer.valueOf(22));
        assertEquals(rawModel.getValueByPosition(1), Integer.valueOf(22));

    }
}
