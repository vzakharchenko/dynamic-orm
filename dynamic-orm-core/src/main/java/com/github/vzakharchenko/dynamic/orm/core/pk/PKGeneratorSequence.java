package com.github.vzakharchenko.dynamic.orm.core.pk;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLExpressions;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public final class PKGeneratorSequence<NUMBER extends Number>
        implements PKGenerator<NUMBER> {

    private static final PKGeneratorSequence INSTANCE = new PKGeneratorSequence();
    private String sequenceName;

    private PKGeneratorSequence() {
        super();
    }

    public PKGeneratorSequence(String sequanceName) {
        this.sequenceName = sequanceName;
    }

    public static PKGeneratorSequence getInstance() {
        return INSTANCE;
    }

    @Override
    public NUMBER generateNewValue(OrmQueryFactory ormQueryFactory,
                                   RelationalPath<?> qTable, DMLModel model) {
        String sequanceName0;
        if (StringUtils.isNotEmpty(this.sequenceName)) {
            sequanceName0 = this.sequenceName;
        } else {
            sequanceName0 = ModelHelper.getSequanceNameFromModel(model.getClass());
        }

        Path<NUMBER> path = (Path<NUMBER>) PrimaryKeyHelper
                .getPrimaryKeyColumns(qTable).get(0);
        Class<? extends NUMBER> type = path.getType();
        SimpleExpression<? extends NUMBER> nextval = SQLExpressions.nextval(type, sequanceName0);

        return ormQueryFactory.select().findOne(ormQueryFactory.buildQuery(), nextval);
    }

    @Override
    public Class<NUMBER> getTypedClass() {
        return (Class<NUMBER>) Number.class;
    }

    @Override
    public PrimaryKeyGenerators getGeneratorType() {
        return PrimaryKeyGenerators.SEQUENCE;
    }

    @Override
    public String name() {
        return sequenceName;
    }
}
