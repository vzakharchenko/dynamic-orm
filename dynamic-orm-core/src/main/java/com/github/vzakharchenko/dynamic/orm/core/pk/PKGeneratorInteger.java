package com.github.vzakharchenko.dynamic.orm.core.pk;

import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 */
public final class PKGeneratorInteger extends PKGeneratorUUID<Integer> {

    private static final PKGeneratorInteger INSTANCE = new PKGeneratorInteger();

    private PKGeneratorInteger() {
        super();
    }

    public static PKGeneratorInteger getInstance() {
        return INSTANCE;
    }

    @Override
    protected Integer parseString(String uid) {
        return NumberUtils.createInteger(uid);
    }

    @Override
    public Class<Integer> getTypedClass() {
        return Integer.class;
    }

    @Override
    public PrimaryKeyGenerators getGeneratorType() {
        return PrimaryKeyGenerators.INTEGER;
    }
}
