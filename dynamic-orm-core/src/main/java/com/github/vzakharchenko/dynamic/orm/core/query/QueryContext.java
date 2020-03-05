package com.github.vzakharchenko.dynamic.orm.core.query;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.querydsl.sql.RelationalPath;

public interface QueryContext {
    RelationalPath<?> getQModel(Class<? extends DMLModel> modelClass);
}
