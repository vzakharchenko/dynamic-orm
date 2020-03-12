package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.OracleTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.CrudBuilder;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.UpdateBuilder;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.UpdateModelBuilder;
import com.github.vzakharchenko.dynamic.orm.model.Testtable;
import com.github.vzakharchenko.dynamic.orm.qModel.QTesttable;
import com.querydsl.sql.SQLCommonQuery;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Objects;

import static org.testng.Assert.*;

/**
 *
 */
public class BuilderTest extends OracleTestQueryOrm {


    @Test
    public void testSelectOne() {

        Testtable testtable = new Testtable();
        testtable.setId(0);
        testtable.setTest2(2);

        CrudBuilder<Testtable> crudBuilder = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);
        crudBuilder.insert(testtable);

        SQLCommonQuery<?> sqlQuery = ormQueryFactory.buildQuery().from(QTesttable.testtable).where(QTesttable.testtable.id.isNotNull());
        ormQueryFactory.select().findAll(sqlQuery, QTesttable.testtable, Testtable.class);

        Testtable tt = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery().from(QTesttable.testtable), QTesttable.testtable, Testtable.class);

        assertNotNull(tt);
        assertEquals(tt.getId().intValue(), 0);
        assertEquals(tt.getTest2().intValue(), 2);
    }

    @Test
    public void testDelete() {


        Testtable testtable = new Testtable();
        testtable.setId(0);
        testtable.setTest2(2);

        CrudBuilder<Testtable> crudBuilder = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);
        crudBuilder.insert(testtable);
        crudBuilder.delete(testtable).byId().delete();
        Testtable tt = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery().from(QTesttable.testtable).where(QTesttable.testtable.id.isNotNull()), QTesttable.testtable, Testtable.class);

        assertNull(tt);
    }

    @Test
    public void testBatchDelete() {

        CrudBuilder<Testtable> crudBuilder = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);

        Testtable testtable1 = new Testtable();
        testtable1.setId(0);
        testtable1.setTest2(2);
        crudBuilder.insert(testtable1);

        Testtable testtable2 = new Testtable();
        testtable2.setId(1);
        testtable2.setTest2(4);
        crudBuilder.insert(testtable2);


        crudBuilder.delete(testtable1).byId().batch(testtable2).byId().delete();

        Testtable tt = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery().from(QTesttable.testtable).where(QTesttable.testtable.id.isNotNull()), QTesttable.testtable, Testtable.class);

        assertNull(tt);
    }


    @Test
    public void testDeleteByIds() {

        CrudBuilder<Testtable> crudBuilder = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);

        Testtable testtable1 = new Testtable();
        testtable1.setId(0);
        testtable1.setTest2(2);
        crudBuilder.insert(testtable1);

        Testtable testtable2 = new Testtable();
        testtable2.setId(1);
        testtable2.setTest2(4);
        crudBuilder.insert(testtable2);


        crudBuilder.deleteByIds(0, 1);

        Testtable tt = ormQueryFactory.select().findOne(ormQueryFactory.buildQuery().from(QTesttable.testtable).where(QTesttable.testtable.id.isNotNull()), QTesttable.testtable, Testtable.class);

        assertNull(tt);
    }

    @Test
    public void testUpdate() {

        CrudBuilder<Testtable> crudBuilder = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);

        Testtable testtable1 = new Testtable();
        testtable1.setId(0);
        testtable1.setTest2(2);
        crudBuilder.insert(testtable1);

        Testtable testtable2 = new Testtable();
        testtable2.setId(1);
        testtable2.setTest2(4);
        crudBuilder.insert(testtable2);

        testtable1.setTest2(1234);
        assertEquals(crudBuilder.updateBuilder().updateModel(testtable1).update(), Long.valueOf(1));
        assertEquals(crudBuilder.updateBuilder().set(QTesttable.testtable.test2, 4321).set(QTesttable.testtable.id, 1).batch().update(), Long.valueOf(1));

        List<Testtable> tt = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(QTesttable.testtable).where(QTesttable.testtable.id.isNotNull()), QTesttable.testtable, Testtable.class);

        assertNotNull(tt);
        assertEquals(tt.size(), 2);
        assertEquals(tt.get(0).getTest2(), Integer.valueOf(1234));
        assertEquals(tt.get(1).getTest2(), Integer.valueOf(4321));

    }

    @Test
    public void testUpdate2() {

        CrudBuilder<Testtable> crudBuilder = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);

        Testtable testtable1 = new Testtable();
        testtable1.setId(0);
        testtable1.setTest2(2);
        crudBuilder.insert(testtable1);

        Testtable testtable2 = new Testtable();
        testtable2.setId(1);
        testtable2.setTest2(4);
        crudBuilder.insert(testtable2);

        testtable1.setTest2(1234);
        assertEquals(crudBuilder.updateBuilder().updateModel(testtable1).update(), Long.valueOf(1));
        assertEquals(crudBuilder.updateBuilder().set(QTesttable.testtable.test2, 4321).set(QTesttable.testtable.id, 1).batch().update(), Long.valueOf(1));
        assertEquals(crudBuilder.updateBuilder().set(QTesttable.testtable.test2, 9999).set(QTesttable.testtable.id, 1).batch().update(), Long.valueOf(1));

        List<Testtable> tt = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(QTesttable.testtable).where(QTesttable.testtable.id.isNotNull()), QTesttable.testtable, Testtable.class);

        assertNotNull(tt);
        assertEquals(tt.size(), 2);
        assertEquals(tt.get(0).getTest2(), Integer.valueOf(1234));
        assertEquals(tt.get(1).getTest2(), Integer.valueOf(9999));

    }

    @Test
    public void testBatchUpdate() {

        CrudBuilder<Testtable> crudBuilder = ormQueryFactory.modify(QTesttable.testtable, Testtable.class);

        Testtable testtable1 = new Testtable();
        testtable1.setId(0);
        testtable1.setTest2(2);
        crudBuilder.insert(testtable1);

        Testtable testtable2 = new Testtable();
        testtable2.setId(1);
        testtable2.setTest2(4);
        crudBuilder.insert(testtable2);

        testtable1.setTest2(1234);
        testtable2.setTest2(4321);
        UpdateModelBuilder<Testtable> setBuilder = crudBuilder.updateBuilder();
        UpdateBuilder<Testtable> updateBuilder = setBuilder
                .updateModel(testtable1)
                .addNextBatch(testtable2)
                .where(QTesttable.testtable.test2.eq(4));
        String sqls = setBuilder.showSql();
        assertEquals(sqls, "\n" +
                "update \"TESTTABLE\"\n" +
                "set \"TEST2\" = 1234, \"ID\" = 0\n" +
                "where \"TESTTABLE\".\"ID\" = 0\n" +
                "update \"TESTTABLE\"\n" +
                "set \"TEST2\" = 4321, \"ID\" = 1\n" +
                "where \"TESTTABLE\".\"ID\" = 1 and (\"TESTTABLE\".\"TEST2\" = 4)\n");

        assertEquals(updateBuilder.update(), Long.valueOf(2), Objects.toString(setBuilder.showSql()));

        List<Testtable> tt = ormQueryFactory.select().findAll(ormQueryFactory.buildQuery().from(QTesttable.testtable).where(QTesttable.testtable.id.isNotNull()), QTesttable.testtable, Testtable.class);

        assertNotNull(tt);
        assertEquals(tt.size(), 2);
        assertEquals(tt.get(0).getTest2(), Integer.valueOf(1234));
        assertEquals(tt.get(1).getTest2(), Integer.valueOf(4321));

    }


}
