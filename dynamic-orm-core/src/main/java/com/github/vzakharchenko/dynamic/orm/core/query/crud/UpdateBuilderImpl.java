package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.CacheHelper;
import com.github.vzakharchenko.dynamic.orm.core.predicate.PredicateFactory;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.RelationalPath;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public class UpdateBuilderImpl<MODEL extends DMLModel> implements UpdateBuilder<MODEL> {


    private UpdateModelBuilder<MODEL> updateModelBuilder;


    public UpdateBuilderImpl(MODEL model, RelationalPath<?> qTable,
                             UpdateModelBuilder<MODEL> updateModelBuilder) {
        this.updateModelBuilder = updateModelBuilder.set(CacheHelper
                .buildMapFromModel(qTable, model));
    }

    private void whereAnd(BooleanExpression and) {
        updateModelBuilder = updateModelBuilder.where(and);
    }

    @Override
    public UpdateBuilder<MODEL> byId() {
        updateModelBuilder = updateModelBuilder.byId();
        return this;
    }

    @Override
    public UpdateBuilder<MODEL> where(BooleanExpression... predicates) {
        whereAnd(PredicateFactory.wrapPredicate(PredicateFactory.and(predicates)));
        return this;
    }

    @Override
    public Long update() {
        return updateModelBuilder.update();
    }

    @Override
    public UpdateModelBuilder<MODEL> addNextSet() {
        updateModelBuilder = updateModelBuilder.batch();
        return updateModelBuilder;
    }

    @Override
    public UpdateBuilder<MODEL> addNextBatch(MODEL model) {
        updateModelBuilder = updateModelBuilder.batch();
        return updateModelBuilder.updateModel(model);
    }


}
