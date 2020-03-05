package com.github.vzakharchenko.dynamic.orm.core.pk;

import com.querydsl.sql.RelationalPath;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;

import java.util.UUID;

/**
 *
 */
public final class UUIDPKGenerator implements PKGenerator<String> {

    private static UUIDPKGenerator uuidpkGenerator = new UUIDPKGenerator();

    private UUIDPKGenerator() {
    }

    public static UUIDPKGenerator getInstance() {
        return uuidpkGenerator;
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
