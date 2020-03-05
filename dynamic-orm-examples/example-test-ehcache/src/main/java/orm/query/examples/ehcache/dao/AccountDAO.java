package orm.query.examples.ehcache.dao;

import orm.query.examples.models.Role;
import orm.query.examples.models.Userdata;

/**
 *
 */
public interface AccountDAO {

    Userdata getUserByName(String name);

    Role getRoleByName(String name);

    void addUser(String name, String password);

    void addRole(String name);

    void addUserToRole(String userName, String roleName);
}
