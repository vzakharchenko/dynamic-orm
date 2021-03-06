package orm.query.examples.ehcache.dao;

import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGeneratorInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import orm.query.examples.models.Role;
import orm.query.examples.models.Userdata;
import orm.query.examples.models.Userrole;
import orm.query.examples.qmodels.QRole;
import orm.query.examples.qmodels.QUserdata;

/**
 *
 */
@Component
public class AccountServiceImpl implements AccountDAO {

    @Autowired
    private OrmQueryFactory ormQueryFactory;
    private PKGeneratorInteger pkGenerator = PKGeneratorInteger.getInstance();

    @Override
    public Userdata getUserByName(String name) {
        return ormQueryFactory.selectCache()
                .findOne(ormQueryFactory.buildQuery()
                        .where(QUserdata.userdata.name.eq(name)), Userdata.class);
    }

    @Override
    public Role getRoleByName(String name) {
        return ormQueryFactory.selectCache()
                .findOne(ormQueryFactory.buildQuery()
                        .where(QRole.role.name.eq(name)), Role.class);
    }

    @Override
    @Transactional
    public void addUser(String name, String password) {
        Userdata userdata = new Userdata();
        userdata.setName(name);
        userdata.setPassword(password);
        ormQueryFactory.modify(Userdata.class).primaryKeyGenerator(pkGenerator).insert(userdata);

    }

    @Override
    @Transactional
    public void addRole(String name) {
        Role role = new Role();
        role.setName(name);
        ormQueryFactory.modify(Role.class).primaryKeyGenerator(pkGenerator).insert(role);
    }

    @Override
    @Transactional
    public void addUserToRole(String userName, String roleName) {
        Userrole userrole = new Userrole();
        Role roleByName = getRoleByName(roleName);
        Assert.notNull(roleByName);
        Userdata userByName = getUserByName(userName);
        Assert.notNull(userByName);
        userrole.setRoleId(roleByName.getId());
        userrole.setUserId(userByName.getId());
        ormQueryFactory.modify(Userrole.class).primaryKeyGenerator(pkGenerator).insert(userrole);
    }

}
