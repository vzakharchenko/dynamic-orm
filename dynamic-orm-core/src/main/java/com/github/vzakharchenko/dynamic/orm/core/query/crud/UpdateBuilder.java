package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public interface UpdateBuilder<MODEL extends DMLModel> {
    UpdateBuilder<MODEL> byId();

    UpdateBuilder<MODEL> where(BooleanExpression... predicates);

    Long update();

    UpdateModelBuilder<MODEL> addNextSet();

    UpdateBuilder<MODEL> addNextBatch(MODEL model);
}
