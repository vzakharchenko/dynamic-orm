package com.github.vzakharchenko.dynamic.orm.structure;

import com.github.vzakharchenko.dynamic.orm.structure.exception.DropAllException;
import com.github.vzakharchenko.dynamic.orm.structure.exception.UpdateException;

import javax.sql.DataSource;

/**
 * Created by vzakharchenko on 24.07.14.
 */
public interface DBStructure {

    String save(DataSource dataSource);

    void update(DataSource dataSource, String fileMask) throws UpdateException;

    void dropAll(DataSource dataSource) throws DropAllException;

    String getSqlScript(DataSource dataSource);

    void unlock(DataSource dataSource);
}
