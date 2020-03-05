package orm.query.examples.modify;

import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import orm.query.examples.models.Userdata;
import orm.query.examples.qmodels.QUserdata;

/**
 *
 */
public class ApplicationMain {


    public static void insert(OrmQueryFactory ormQueryFactory) {

        System.out.println("Main: start Transaction");
        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        Userdata userdata = new Userdata();
        userdata.setId(1001);
        userdata.setName("testName");
        userdata.setPassword("12345677890");
        ormQueryFactory.modify(Userdata.class).insert(userdata);
        System.out.println("Main: insert record");
        ormQueryFactory.transactionManager().commit();
        Long count = ormQueryFactory.select().count(ormQueryFactory.buildQuery().from(QUserdata.userdata));
        System.out.println("Main: count of records " + count);
    }

    public static void update(OrmQueryFactory ormQueryFactory) {
        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        Userdata userdata = new Userdata();
        userdata.setId(1001);
        userdata.setName("testName2");
        userdata.setPassword("12345677890");
        ormQueryFactory.modify(Userdata.class).updateBuilder().updateModel(userdata).update();
        System.out.println("Main: update record");
        ormQueryFactory.transactionManager().commit();
        Long count = ormQueryFactory.select().count(ormQueryFactory.buildQuery().from(QUserdata.userdata));
        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        System.out.println("Main: count of records " + count);
    }

    public static void delete(OrmQueryFactory ormQueryFactory) {
        System.out.println("Main: start Transaction");
        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        System.out.println("Main: delete record");
        ormQueryFactory.modify(Userdata.class).deleteByIds(1001);
        ormQueryFactory.transactionManager().commit();
        System.out.println("Main: rollback");
        Long count = ormQueryFactory.select().count(ormQueryFactory.buildQuery().from(QUserdata.userdata));
        System.out.println("Main: count of records " + count);
    }

    public static void rollback(OrmQueryFactory ormQueryFactory) {
        System.out.println("Main: start Transaction");
        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        System.out.println("Main: insert record");
        Userdata userdata = new Userdata();
        userdata.setId(1002);
        userdata.setName("testName4");
        userdata.setPassword("12345677890");
        ormQueryFactory.modify(Userdata.class).insert(userdata);
        ormQueryFactory.transactionManager().rollback();
        System.out.println("Main: rollback");
        Long count = ormQueryFactory.select().count(ormQueryFactory.buildQuery().from(QUserdata.userdata));
        System.out.println("Main: count of records " + count);
    }

    public static void main(String... args) {
        ApplicationContext ctx =
                new GenericXmlApplicationContext("classpath:context.xml");
        OrmQueryFactory ormQueryFactory = ctx.getBean(OrmQueryFactory.class);

        insert(ormQueryFactory);
        update(ormQueryFactory);
        delete(ormQueryFactory);
        rollback(ormQueryFactory);
    }
}
