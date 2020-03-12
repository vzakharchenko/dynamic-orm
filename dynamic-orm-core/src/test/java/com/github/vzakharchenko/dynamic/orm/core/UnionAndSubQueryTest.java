package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.DebugAnnotationTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.predicate.OrderSpecifierFactory;
import com.github.vzakharchenko.dynamic.orm.core.query.UnionBuilder;
import com.github.vzakharchenko.dynamic.orm.model.TestTableCache;
import com.github.vzakharchenko.dynamic.orm.model.Testtable;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableCache;
import com.github.vzakharchenko.dynamic.orm.qModel.QTesttable;
import com.github.vzakharchenko.dynamic.orm.qModel.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLCommonQuery;
import com.querydsl.sql.SQLExpressions;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

/**
 *
 */
public class UnionAndSubQueryTest extends DebugAnnotationTestQueryOrm {

    @Test
    public void testSimpleSubQueryQuery() {
        SubQueryExpression<Tuple> queryExpression = SQLExpressions.select(QUser.user.id, QTesttable.testtable.id)
                .from(QTesttable.testtable)
                .distinct()
                .fullJoin(QUser.user).on(QUser.user.id.eq(QTesttable.testtable.id))
                .orderBy(new OrderSpecifier(Order.DESC, QUser.user.id));


        SQLCommonQuery<?> sqlQuery = ormQueryFactory.buildQuery().from(QTesttable.testtable).innerJoin(QUser.user).on(QUser.user.id.eq(QTesttable.testtable.id)).where(Expressions.list(QUser.user.id, QTesttable.testtable.id).in(queryExpression));


        String sql = ormQueryFactory.select().showSql(sqlQuery, QTesttable.testtable, Testtable.class);
        assertEquals(sql, "select \"TESTTABLE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "inner join \"USER\" \"USER\"\n" +
                "on \"USER\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "where (\"USER\".\"ID\", \"TESTTABLE\".\"ID\") in (select distinct \"USER\".\"ID\", \"TESTTABLE\".\"ID\" as \"col__ID1\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "full join \"USER\" \"USER\"\n" +
                "on \"USER\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"USER\".\"ID\" desc)");
    }

    @Test
    public void testUnionAllQuery() {

        Testtable testtable1 = new Testtable();
        testtable1.setId(0);
        testtable1.setTest2(2456);
        ormQueryFactory.modify(QTesttable.testtable, Testtable.class).insert(testtable1);

        TestTableCache testtable2 = new TestTableCache();
        testtable2.setId(0);
        testtable2.setTest2(4546);
        ormQueryFactory.modify(QTestTableCache.testTableCache, TestTableCache.class).insert(testtable2);

        SubQueryExpression<Tuple> listSubQuery1 = SQLExpressions.select(QTestTableCache.testTableCache.id, QTesttable.testtable.test2)
                .from(QTesttable.testtable)
                .distinct()
                .fullJoin(QTestTableCache.testTableCache).on(QTestTableCache.testTableCache.id.eq(QTesttable.testtable.id))
                .orderBy(new OrderSpecifier(Order.DESC, QTesttable.testtable.test2));
        SubQueryExpression<Tuple> listSubQuery2 = SQLExpressions.select(QTestTableCache.testTableCache.id, QTesttable.testtable.test2)
                .from(QTesttable.testtable)
                .leftJoin(QTestTableCache.testTableCache).on(QTestTableCache.testTableCache.id.eq(QTesttable.testtable.id))
                .orderBy(new OrderSpecifier(Order.DESC, QTestTableCache.testTableCache.test2));
        List<SubQueryExpression<?>> listSubQueries = Arrays.asList(listSubQuery1, listSubQuery2);
        UnionBuilder unionAll = ormQueryFactory.select().unionAll(ormQueryFactory.buildQuery(), listSubQueries);
        String showSql = unionAll.showSql();

        assertEquals(showSql, "select \"ID\", \"TEST2\"\n" +
                "from ((select distinct \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "full join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TESTTABLE\".\"TEST2\" desc)\n" +
                "union all\n" +
                "(select \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "left join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"TEST2\" desc)) as \"union\"");

        assertEquals(unionAll.showCountSql(), "select count(*)\n" +
                "from ((select distinct \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "full join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TESTTABLE\".\"TEST2\" desc)\n" +
                "union all\n" +
                "(select \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "left join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"TEST2\" desc)) as \"union\"");
        List<RawModel> rawModels = unionAll.findAll();
        Long count = unionAll.count();
        assertEquals(count.intValue(), 2);
        Long cacheCount = unionAll.count();
        Long cacheCount2 = unionAll.count();

        assertTrue(cacheCount == cacheCount2);
        unionAll = ormQueryFactory.select().unionAll(ormQueryFactory.buildQuery(), listSubQueries.toArray(new SubQueryExpression[listSubQueries.size()]));
        count = unionAll.count();
        assertEquals(count.intValue(), 2);
    }

    @Test
    public void testUnionAllQueryCache() {

        Testtable testtable1 = new Testtable();
        testtable1.setId(0);
        testtable1.setTest2(2456);
        ormQueryFactory.modify(QTesttable.testtable, Testtable.class).insert(testtable1);

        TestTableCache testtable2 = new TestTableCache();
        testtable2.setId(0);
        testtable2.setTest2(4546);
        ormQueryFactory.modify(QTestTableCache.testTableCache, TestTableCache.class).insert(testtable2);

        SubQueryExpression<Tuple> listSubQuery1 = SQLExpressions.select(QTestTableCache.testTableCache.id, QTesttable.testtable.test2)
                .from(QTesttable.testtable)
                .distinct()
                .fullJoin(QTestTableCache.testTableCache).on(QTestTableCache.testTableCache.id.eq(QTesttable.testtable.id))
                .orderBy(new OrderSpecifier(Order.DESC, QTesttable.testtable.test2));
        SubQueryExpression<Tuple> listSubQuery2 = SQLExpressions.select(QTestTableCache.testTableCache.id, QTesttable.testtable.test2)
                .from(QTesttable.testtable)
                .leftJoin(QTestTableCache.testTableCache).on(QTestTableCache.testTableCache.id.eq(QTesttable.testtable.id))
                .orderBy(new OrderSpecifier(Order.DESC, QTestTableCache.testTableCache.test2));
        List<SubQueryExpression<?>> listSubQueries = Arrays.asList(listSubQuery1, listSubQuery2);
        UnionBuilder unionAll = ormQueryFactory.selectCache().unionAll(ormQueryFactory.buildQuery(), listSubQueries);
        String showSql = unionAll.showSql();

        assertEquals(showSql, "select \"ID\", \"TEST2\"\n" +
                "from ((select distinct \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "full join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TESTTABLE\".\"TEST2\" desc)\n" +
                "union all\n" +
                "(select \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "left join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"TEST2\" desc)) as \"union\"");

        assertEquals(unionAll.showCountSql(), "select count(*)\n" +
                "from ((select distinct \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "full join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TESTTABLE\".\"TEST2\" desc)\n" +
                "union all\n" +
                "(select \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "left join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"TEST2\" desc)) as \"union\"");
        List<RawModel> rawModels = unionAll.findAll();
        Long count = unionAll.count();
        assertEquals(count.intValue(), 2);
        Long cacheCount = unionAll.count();
        Long cacheCount2 = unionAll.count();

        assertTrue(cacheCount == cacheCount2);
        unionAll = ormQueryFactory.select().unionAll(ormQueryFactory.buildQuery(), listSubQueries.toArray(new SubQueryExpression[listSubQueries.size()]));
        count = unionAll.count();
        assertEquals(count.intValue(), 2);
    }

    @Test
    public void testUnionQuery() {

        Testtable testtable1 = new Testtable();
        testtable1.setId(0);
        testtable1.setTest2(2456);
        ormQueryFactory.modify(QTesttable.testtable, Testtable.class).insert(testtable1);

        TestTableCache testtable2 = new TestTableCache();
        testtable2.setId(0);
        testtable2.setTest2(4546);
        ormQueryFactory.modify(QTestTableCache.testTableCache, TestTableCache.class).insert(testtable2);

        SubQueryExpression<Tuple> listSubQuery1 = SQLExpressions.select(QTestTableCache.testTableCache.id, QTesttable.testtable.test2)
                .from(QTesttable.testtable)
                .distinct()
                .fullJoin(QTestTableCache.testTableCache).on(QTestTableCache.testTableCache.id.eq(QTesttable.testtable.id))
                .orderBy(new OrderSpecifier(Order.DESC, QTesttable.testtable.test2));
        SubQueryExpression<Tuple> listSubQuery2 = SQLExpressions.select(QTestTableCache.testTableCache.id, QTesttable.testtable.test2)
                .from(QTesttable.testtable)
                .leftJoin(QTestTableCache.testTableCache).on(QTestTableCache.testTableCache.id.eq(QTesttable.testtable.id))
                .orderBy(new OrderSpecifier(Order.DESC, QTestTableCache.testTableCache.test2));
        List<SubQueryExpression<?>> listSubQueries = Arrays.asList(listSubQuery1, listSubQuery2);
        UnionBuilder union = ormQueryFactory.select().union(ormQueryFactory.buildQuery(), listSubQueries);
        String showSql = union.showSql();

        assertEquals(showSql, "select \"ID\", \"TEST2\"\n" +
                "from ((select distinct \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "full join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TESTTABLE\".\"TEST2\" desc)\n" +
                "union\n" +
                "(select \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "left join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"TEST2\" desc)) as \"union\"");

        assertEquals(union.showCountSql(), "select count(*)\n" +
                "from ((select distinct \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "full join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TESTTABLE\".\"TEST2\" desc)\n" +
                "union\n" +
                "(select \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "left join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"TEST2\" desc)) as \"union\"");
        List<RawModel> rawModels = union.findAll();
        Long count = union.count();
        assertEquals(count.intValue(), 1);
        RawModel rawModel = union.findOne();
        assertNotNull(rawModel);
        assertEquals(rawModel.getValueByColumnName("TEST2", Integer.class), Integer.valueOf(2456));
        Long cacheCount = union.count();
        Long cacheCount2 = union.count();

        assertTrue(cacheCount == cacheCount2);
        union = ormQueryFactory.select().union(ormQueryFactory.buildQuery(), listSubQueries.toArray(new SubQueryExpression[listSubQueries.size()]));
        count = union.count();
        assertEquals(count.intValue(), 1);
    }

    @Test
    public void testUnionAllOrderByQuery() {

        Testtable testtable1 = new Testtable();
        testtable1.setId(0);
        testtable1.setTest2(2456);
        ormQueryFactory.modify(QTesttable.testtable, Testtable.class).insert(testtable1);

        TestTableCache testtable2 = new TestTableCache();
        testtable2.setId(0);
        testtable2.setTest2(4546);
        ormQueryFactory.modify(QTestTableCache.testTableCache, TestTableCache.class).insert(testtable2);

        SubQueryExpression<Tuple> listSubQuery1 = SQLExpressions.select(QTestTableCache.testTableCache.id, QTesttable.testtable.test2)
                .from(QTesttable.testtable)
                .distinct()
                .fullJoin(QTestTableCache.testTableCache).on(QTestTableCache.testTableCache.id.eq(QTesttable.testtable.id))
                .orderBy(new OrderSpecifier(Order.DESC, QTesttable.testtable.test2));
        SubQueryExpression<Tuple> listSubQuery2 = SQLExpressions.select(QTestTableCache.testTableCache.id, QTesttable.testtable.test2)
                .from(QTesttable.testtable)
                .leftJoin(QTestTableCache.testTableCache).on(QTestTableCache.testTableCache.id.eq(QTesttable.testtable.id))
                .orderBy(new OrderSpecifier(Order.DESC, QTestTableCache.testTableCache.test2));
        List<SubQueryExpression<?>> listSubQueries = Arrays.asList(listSubQuery1, listSubQuery2);
        UnionBuilder union = ormQueryFactory.select().unionAll(ormQueryFactory.buildQuery(), listSubQueries)
                .orderBy(OrderSpecifierFactory.getOrderSpecifierbyName(Order.DESC, "ID"));
        String showSql = union.showSql();

        assertEquals(showSql, "select \"ID\", \"TEST2\"\n" +
                "from ((select distinct \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "full join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TESTTABLE\".\"TEST2\" desc)\n" +
                "union all\n" +
                "(select \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"TEST2\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "left join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"TEST2\" desc)) as \"union\"\n" +
                "order by ID desc");

        List<RawModel> rawModels = union.findAll();
        assertEquals(rawModels.size(), 2);
        RawModel rawModel = rawModels.get(0);
        assertNotNull(rawModel);
        assertNotNull(rawModel);
        assertEquals(rawModel.getValueByColumnName("TEST2", Integer.class), Integer.valueOf(2456));
    }

    @Test
    public void testUnionQueryWithLimit() {
        SubQueryExpression<Tuple> listSubQuery1 = SQLExpressions.select(QTestTableCache.testTableCache.id, QTesttable.testtable.id)
                .from(QTesttable.testtable)
                .distinct()
                .fullJoin(QTestTableCache.testTableCache).on(QTestTableCache.testTableCache.id.eq(QTesttable.testtable.id))
                .orderBy(new OrderSpecifier(Order.DESC, QTestTableCache.testTableCache.id));
        SubQueryExpression<Tuple> listSubQuery2 = SQLExpressions.select(QTestTableCache.testTableCache.id, QTesttable.testtable.id)
                .from(QTesttable.testtable)
                .leftJoin(QTestTableCache.testTableCache).on(QTestTableCache.testTableCache.id.eq(QTesttable.testtable.id))
                .orderBy(new OrderSpecifier(Order.DESC, QTestTableCache.testTableCache.id));
        List<SubQueryExpression<?>> listSubQueries = Arrays.asList(listSubQuery1, listSubQuery2);
        UnionBuilder unionAll = ormQueryFactory.select().unionAll(ormQueryFactory.buildQuery(), listSubQueries);
        unionAll = unionAll.limit(new Range(0, 1000));
        String showSql = unionAll.showSql();

        assertEquals(showSql, "select \"ID\"\n" +
                "from ((select distinct \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"ID\" as \"col__ID1\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "full join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"ID\" desc)\n" +
                "union all\n" +
                "(select \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"ID\" as \"col__ID1\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "left join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"ID\" desc)) as \"union\"\n" +
                "limit 1000\n" +
                "offset 0");

        assertEquals(unionAll.showCountSql(), "select count(*)\n" +
                "from ((select distinct \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"ID\" as \"col__ID1\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "full join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"ID\" desc)\n" +
                "union all\n" +
                "(select \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"ID\" as \"col__ID1\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "left join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"ID\" desc)) as \"union\"\n" +
                "limit 1000\n" +
                "offset 0");
        List<RawModel> rawModels = unionAll.findAll();
        Long count = unionAll.count();
        assertEquals(count.intValue(), 0);
        Long cacheCount = unionAll.count();
        Long cacheCount2 = unionAll.count();

        assertTrue(cacheCount == cacheCount2);
    }

    @Test
    public void testUnionQueryWithLimit2() {
        SubQueryExpression<Tuple> listSubQuery1 = SQLExpressions.select(QTestTableCache.testTableCache.id, QTesttable.testtable.id)
                .from(QTesttable.testtable)
                .distinct()
                .fullJoin(QTestTableCache.testTableCache).on(QTestTableCache.testTableCache.id.eq(QTesttable.testtable.id))
                .orderBy(new OrderSpecifier(Order.DESC, QTestTableCache.testTableCache.id));
        SubQueryExpression<Tuple> listSubQuery2 = SQLExpressions.select(QTestTableCache.testTableCache.id, QTesttable.testtable.id)
                .from(QTesttable.testtable)
                .leftJoin(QTestTableCache.testTableCache).on(QTestTableCache.testTableCache.id.eq(QTesttable.testtable.id))
                .orderBy(new OrderSpecifier(Order.DESC, QTestTableCache.testTableCache.id));
        List<SubQueryExpression<?>> listSubQueries = Arrays.asList(listSubQuery1, listSubQuery2);
        UnionBuilder unionAll = ormQueryFactory.select().unionAll(ormQueryFactory.buildQuery(), listSubQueries);
        unionAll = unionAll.limit(new Range(1000));
        String showSql = unionAll.showSql();

        assertEquals(showSql, "select \"ID\"\n" +
                "from ((select distinct \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"ID\" as \"col__ID1\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "full join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"ID\" desc)\n" +
                "union all\n" +
                "(select \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"ID\" as \"col__ID1\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "left join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"ID\" desc)) as \"union\"\n" +
                "limit 1000");

        assertEquals(unionAll.showCountSql(), "select count(*)\n" +
                "from ((select distinct \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"ID\" as \"col__ID1\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "full join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"ID\" desc)\n" +
                "union all\n" +
                "(select \"TEST_TABLE_CACHE\".\"ID\", \"TESTTABLE\".\"ID\" as \"col__ID1\"\n" +
                "from \"TESTTABLE\" \"TESTTABLE\"\n" +
                "left join \"TEST_TABLE_CACHE\" \"TEST_TABLE_CACHE\"\n" +
                "on \"TEST_TABLE_CACHE\".\"ID\" = \"TESTTABLE\".\"ID\"\n" +
                "order by \"TEST_TABLE_CACHE\".\"ID\" desc)) as \"union\"\n" +
                "limit 1000");
        List<RawModel> rawModels = unionAll.findAll();
        Long count = unionAll.count();
        assertEquals(count.intValue(), 0);
        Long cacheCount = unionAll.count();
        Long cacheCount2 = unionAll.count();

        assertTrue(cacheCount == cacheCount2);
    }
}
