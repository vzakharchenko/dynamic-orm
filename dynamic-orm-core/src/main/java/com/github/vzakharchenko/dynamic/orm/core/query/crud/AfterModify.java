package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;

import java.io.Serializable;
import java.util.Map;

/**
 *
 */
public interface AfterModify<MODEL> {
    void afterInsert(Map<Serializable, DiffColumnModel> diffColumnModelMap);

    void afterDelete(Map<Serializable, DiffColumnModel> diffColumnModelMap);

    void afterUpdate(Map<Serializable, DiffColumnModel> diffColumnModelMap);

    void cleanQueryCache();

}
