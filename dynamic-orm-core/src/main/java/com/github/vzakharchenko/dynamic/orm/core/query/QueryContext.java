package com.github.vzakharchenko.dynamic.orm.core.query;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import com.querydsl.sql.RelationalPath;

public interface QueryContext {
    TransactionalCache getTransactionCache();

    RelationalPath<?> getQModel(Class<? extends DMLModel> modelClass);
}
