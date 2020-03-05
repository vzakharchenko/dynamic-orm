package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.querydsl.core.types.dsl.BooleanExpression;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public interface DeleteModelBuilder<MODEL extends DMLModel> {
    DeleteModelBuilder<MODEL> byId();

    DeleteModelBuilder<MODEL> where(BooleanExpression predicate);

    DeleteModelBuilder<MODEL> batch(MODEL model);

    Long delete();
}
