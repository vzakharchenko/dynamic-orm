package orm.query.examples.ehcache;

import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.stereotype.Component;
import orm.query.examples.ehcache.dao.AccountDAO;
import orm.query.examples.ehcache.dao.BoDAO;
import orm.query.examples.ehcache.dao.BoStage;
import orm.query.examples.models.Botable;

import java.util.List;

/**
 *
 */
@Component
public class ApplicationMain {
    public static final String TEST_ROLE1 = "test Role1";
    public static final String TEST_ROLE2 = "test Role2";
    public static final String USER1 = "user1";
    public static final String USER2 = "user2";
    public static final String USER3 = "user3";
    private static final Logger logger = LoggerFactory.getLogger(ApplicationMain.class);
    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private BoDAO boDAO;

    @Autowired
    private OrmQueryFactory ormQueryFactory;

    public static void main(String... args) {
        ApplicationContext ctx =
                new GenericXmlApplicationContext("classpath:/context.xml");
        ApplicationMain applicationMain = ctx.getBean(ApplicationMain.class);
        applicationMain.initAccount();
        applicationMain.addBobjects();
        logger.info(" init cache");
        applicationMain.showStatistic();
        applicationMain.addOneMoreBobject();
        logger.info(" Next, there should be 3 SQL-queries");
        applicationMain.showStatistic();
        logger.info(" Next, there should be no SQL-query");
        applicationMain.showStatistic();

    }

    public void initAccount() {
        ormQueryFactory.transactionManager().startTransactionIfNeeded();
        try {
            accountDAO.addUser(USER1, "1234567890");
            accountDAO.addUser(USER2, "1234567890");
            accountDAO.addUser(USER3, "1234567890");
            accountDAO.addRole(TEST_ROLE2);
            accountDAO.addRole(TEST_ROLE1);

            accountDAO.addUserToRole(USER1, TEST_ROLE1);
            accountDAO.addUserToRole(USER2, TEST_ROLE2);
            accountDAO.addUserToRole(USER3, TEST_ROLE2);
            accountDAO.addUserToRole(USER3, TEST_ROLE1);
        } catch (Exception ex) {
            ormQueryFactory.transactionManager().rollback();
            throw new IllegalStateException(ex);
        }
        ormQueryFactory.transactionManager().commit();
    }

    public void addBobjects() {
        boDAO.insert("bobject1", BoStage.APPROVAL, USER1);
        boDAO.insert("bobject2", BoStage.APPROVAL, USER2);
        boDAO.insert("bobject3", BoStage.APPROVAL, USER2);
        boDAO.insert("bobject4", BoStage.APPROVAL, USER3);
        boDAO.insert("bobject5", BoStage.APPROVAL, USER3);
    }

    public void addOneMoreBobject() {
        boDAO.insert("bobject6", BoStage.APPROVAL, USER3);
    }

    public void showStatistic() {
        List<Botable> botables = boDAO.byUser(USER1);
        logger.info("Get all by user: " + USER1);
        logger.info(toString(botables));
        botables = boDAO.byUser(USER2);
        logger.info("Get all by user: " + USER2);
        logger.info(toString(botables));
        botables = boDAO.byUser(USER3);
        logger.info("Get all by user: " + USER3);
        logger.info(toString(botables));

        botables = boDAO.byRole(TEST_ROLE1);
        logger.info("Get all by Role: " + TEST_ROLE1);
        logger.info(toString(botables));
        botables = boDAO.byRole(TEST_ROLE2);
        logger.info("Get all by Role: " + TEST_ROLE2);
        logger.info(toString(botables));

    }

    private String toString(List<Botable> botables) {
        StringBuilder toString = new StringBuilder("{\n");
        for (Botable botable : botables) {
            toString.append(ToStringBuilder.reflectionToString(botable)).append("\n");
        }
        return toString.append("\n}").toString();
    }
}
