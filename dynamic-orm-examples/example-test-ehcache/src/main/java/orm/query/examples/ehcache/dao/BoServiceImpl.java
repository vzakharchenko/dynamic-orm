package orm.query.examples.ehcache.dao;

import com.querydsl.sql.SQLCommonQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.github.vzakharchenko.dynamic.orm.core.cache.LazyList;
import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGeneratorInteger;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheBuilder;
import orm.query.examples.models.Botable;
import orm.query.examples.models.Userdata;
import orm.query.examples.qmodels.QBotable;
import orm.query.examples.qmodels.QRole;
import orm.query.examples.qmodels.QUserrole;

import java.sql.Timestamp;
import java.util.List;

/**
 *
 */
@Service
public class BoServiceImpl implements BoDAO {

    @Autowired
    private OrmQueryFactory ormQueryFactory;
    @Autowired
    private AccountDAO accountDAO;

    private PKGeneratorInteger pkGenerator;


    @Override
    @Transactional
    public void insert(String name, BoStage type, String userName) {
        Botable botable = new Botable();
        botable.setName(name);
        botable.setStage(type.getStage());
        Userdata userByName = accountDAO.getUserByName(userName);
        Assert.notNull(userByName);
        botable.setUserId(userByName.getId());
        botable.setVersion(new Timestamp(System.currentTimeMillis()));
        pkGenerator = PKGeneratorInteger.getInstance();
        Long insert = ormQueryFactory.modify(Botable.class).primaryKeyGenerator(pkGenerator).insert(botable);
        DBHelper.invokeExceptionIfNoAction(insert);
    }

    @Override
    public List<Botable> byUser(String userName) {
        Userdata userByName = accountDAO.getUserByName(userName);
        Assert.notNull(userByName);
        CacheBuilder<Botable> cacheBuilder = ormQueryFactory.modelCacheBuilder(Botable.class);
        LazyList<Botable> list = cacheBuilder.findAllByColumn(QBotable.botable.userId, userByName.getId());
        return list.getModelList();
    }


    @Override
    public List<Botable> byRole(String roleName) {
        SQLCommonQuery<?> query = ormQueryFactory.buildQuery()
                .from(QBotable.botable)
                .innerJoin(QUserrole.userrole)
                .on(QUserrole.userrole.userId.eq(QBotable.botable.userId))
                .innerJoin(QRole.role)
                .on(QRole.role.id.eq(QUserrole.userrole.roleId))
                .where(QRole.role.name.eq(roleName));
        return ormQueryFactory.selectCache().findAll(query, Botable.class);
    }
}
