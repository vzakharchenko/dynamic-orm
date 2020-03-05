package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.querydsl.sql.RelationalPath;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public abstract class CrudBuilderFactory {

    public static <MODEL extends DMLModel> CrudBuilder<MODEL> buildCrudBuilder(
            Class<MODEL> modelClass,
            RelationalPath<?> qTable,
            QueryContextImpl queryContext) {
        return new CrudBuilderImpl<>(modelClass, qTable, queryContext);
    }
}
