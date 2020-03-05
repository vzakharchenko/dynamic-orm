package com.github.vzakharchenko.dynamic.orm.core.query;

import com.querydsl.sql.RelationalPath;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;

public interface QueryContext {
    RelationalPath<?> getQModel(Class<? extends DMLModel> modelClass);
}
