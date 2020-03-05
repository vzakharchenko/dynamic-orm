package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.List;
import java.util.Map;

/**
 * update builder
 */
public interface UpdateModelBuilder<MODEL extends DMLModel> {

    <T> UpdateModelBuilder<MODEL> set(Path<T> column, T value);

    UpdateModelBuilder<MODEL> set(Map<Path<?>, Object> setMap);

    UpdateModelBuilder<MODEL> where(BooleanExpression predicate);

    UpdateModelBuilder<MODEL> byId();

    <T> UpdateModelBuilder<MODEL> batch();

    Long updateModelsById(MODEL... models);

    Long updateModelsById(List<MODEL> models);

    UpdateBuilder<MODEL> updateModel(MODEL model);

    Long update();

    String showSql();
}
