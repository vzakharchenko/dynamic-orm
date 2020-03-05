package orm.query.examples.ehcache.dao;

import orm.query.examples.models.Botable;

import java.util.List;

/**
 *
 */
public interface BoDAO {
    void insert(String name, BoStage type, String userName);

    List<Botable> byUser(String userName);

    List<Botable> byRole(String userName);
}
