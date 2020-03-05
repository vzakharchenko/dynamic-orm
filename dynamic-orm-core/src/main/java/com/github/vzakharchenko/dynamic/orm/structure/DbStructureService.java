package com.github.vzakharchenko.dynamic.orm.structure;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 12.04.15
 * Time: 22:29
 */
public interface DbStructureService {
    void save();

    void load();

    void clear();

    void unlock();

    String generateSql();
}
