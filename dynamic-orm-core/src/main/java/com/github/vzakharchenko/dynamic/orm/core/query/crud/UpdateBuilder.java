package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.querydsl.core.types.dsl.BooleanExpression;

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
