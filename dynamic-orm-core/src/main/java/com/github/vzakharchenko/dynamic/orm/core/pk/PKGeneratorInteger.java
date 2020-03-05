package com.github.vzakharchenko.dynamic.orm.core.pk;

import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 */
public final class PKGeneratorInteger extends PKGeneratorUUID<Integer> {

    private static PKGeneratorInteger pkGeneratorInteger = new PKGeneratorInteger();

    private PKGeneratorInteger() {
    }

    public static PKGeneratorInteger getInstance() {
        return pkGeneratorInteger;
    }

    @Override
    protected Integer parseString(String uid) {
        return NumberUtils.createInteger(uid);
    }

    @Override
    public Class<Integer> getTypedClass() {
        return Integer.class;
    }
}
