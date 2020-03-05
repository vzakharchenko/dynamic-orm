package com.github.vzakharchenko.dynamic.orm.core.pk;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.sql.RelationalPath;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;

import java.io.Serializable;

/**
 *
 */
public interface PKGenerator<TYPE> extends Serializable {

    TYPE generateNewValue(OrmQueryFactory ormQueryFactory,
                          RelationalPath<?> qTable, DMLModel model);

    Class<TYPE> getTypedClass();

    default void generate(OrmQueryFactory ormQueryFactory,
                          RelationalPath<?> qTable, DMLModel model) {
        ComparableExpressionBase primaryKey = ModelHelper.getPrimaryKey(qTable);
        if (primaryKey == null) {
            throw new IllegalStateException(qTable + ".Primary Key is not Found");
        }
        ModelHelper.setColumnValue(model, (Path) primaryKey,
                generateNewValue(ormQueryFactory, qTable, model));
    }
}
