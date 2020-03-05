package com.github.vzakharchenko.dynamic.orm.core.pk;

import com.querydsl.sql.RelationalPath;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;

/**
 *
 */
public final class PKGeneratorLong implements PKGenerator<Long> {

    private static PKGeneratorLong pkGeneratorLong = new PKGeneratorLong();

    private PKGeneratorLong() {
    }

    public static PKGeneratorLong getInstance() {
        return pkGeneratorLong;
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
