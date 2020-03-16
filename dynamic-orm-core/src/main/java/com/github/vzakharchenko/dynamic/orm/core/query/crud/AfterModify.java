package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;

import java.util.Map;

/**
 *
 */
public interface AfterModify<MODEL> {
    void afterInsert(Map<CompositeKey, DiffColumnModel> diffColumnModelMap);

    void afterDelete(Map<CompositeKey, DiffColumnModel> diffColumnModelMap);

    void afterUpdate(Map<CompositeKey, DiffColumnModel> diffColumnModelMap);

    void cleanQueryCache();

}
