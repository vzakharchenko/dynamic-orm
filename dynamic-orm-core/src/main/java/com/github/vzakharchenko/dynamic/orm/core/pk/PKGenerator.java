package com.github.vzakharchenko.dynamic.orm.core.pk;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPath;

import java.io.Serializable;

/**
 *
 */
public interface PKGenerator<TYPE> extends Serializable {

    TYPE generateNewValue(OrmQueryFactory ormQueryFactory,
                          RelationalPath<?> qTable, DMLModel model);

    Class<TYPE> getTypedClass();

    PrimaryKeyGenerators getGeneratorType();

    String name();

    default void generate(OrmQueryFactory ormQueryFactory,
                          RelationalPath<?> qTable, DMLModel model) {
        PrimaryKey<?> primaryKey = PrimaryKeyHelper.getPrimaryKey(qTable);
        if (primaryKey == null || PrimaryKeyHelper.hasCompositePrimaryKey(qTable)) {
            throw new IllegalStateException(qTable + " does not contain Primary Key" +
                    " or has composite PK ");
        }
        ModelHelper.setColumnValue(model, PrimaryKeyHelper.getPrimaryKeyColumns(qTable).get(0),
                generateNewValue(ormQueryFactory, qTable, model));
    }
}
