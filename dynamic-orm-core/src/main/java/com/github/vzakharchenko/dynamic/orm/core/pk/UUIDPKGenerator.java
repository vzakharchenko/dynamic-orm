package com.github.vzakharchenko.dynamic.orm.core.pk;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.querydsl.sql.RelationalPath;

import java.util.UUID;

/**
 *
 */
public final class UUIDPKGenerator implements PKGenerator<String> {

    private static final UUIDPKGenerator INSTANCE = new UUIDPKGenerator();

    private UUIDPKGenerator() {
        super();
    }

    public static UUIDPKGenerator getInstance() {
        return INSTANCE;
    }

    @Override
    public String generateNewValue(OrmQueryFactory ormQueryFactory,
                                   RelationalPath<?> qTable, DMLModel dmlModel) {
        return UUID.randomUUID().toString();
    }

    @Override
    public Class<String> getTypedClass() {
        return String.class;
    }
}
