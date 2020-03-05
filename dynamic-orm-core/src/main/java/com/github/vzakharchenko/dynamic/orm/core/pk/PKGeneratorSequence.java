package com.github.vzakharchenko.dynamic.orm.core.pk;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLExpressions;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public final class PKGeneratorSequence<NUMBER extends Number>
        implements PKGenerator<NUMBER> {

    private static PKGeneratorSequence pkGeneratorSequence = new PKGeneratorSequence();
    private String sequanceName;

    private PKGeneratorSequence() {
    }

    public PKGeneratorSequence(String sequanceName) {
        this.sequanceName = sequanceName;
    }

    public static PKGeneratorSequence getInstance() {
        return pkGeneratorSequence;
    }

    @Override
    public NUMBER generateNewValue(OrmQueryFactory ormQueryFactory,
                                   RelationalPath<?> qTable, DMLModel model) {
        String sequanceName0;
        if (StringUtils.isNotEmpty(this.sequanceName)) {
            sequanceName0 = this.sequanceName;
        } else {
            sequanceName0 = ModelHelper.getSequanceNameFromModel(model.getClass());
        }

        SimpleExpression<NUMBER> nextval = SQLExpressions.nextval(ModelHelper
                .getPrimaryKeyColumn(qTable).getType(), sequanceName0);

        return ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), nextval);
    }

    @Override
    public Class<NUMBER> getTypedClass() {
        return (Class<NUMBER>) Number.class;
    }
}
