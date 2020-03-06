package com.github.vzakharchenko.dynamic.orm.core.pk;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.querydsl.sql.RelationalPath;

/**
 *
 */
public final class PKGeneratorLong implements PKGenerator<Long> {

    private static final PKGeneratorLong INSTANCE = new PKGeneratorLong();


    private PKGeneratorLong() {
        super();
    }

    public static PKGeneratorLong getInstance() {
        return INSTANCE;
    }


    @Override
    public Long generateNewValue(OrmQueryFactory ormQueryFactory,
                                 RelationalPath<?> qTable, DMLModel model) {
        return System.nanoTime();
    }

    @Override
    public Class<Long> getTypedClass() {
        return Long.class;
    }
}
