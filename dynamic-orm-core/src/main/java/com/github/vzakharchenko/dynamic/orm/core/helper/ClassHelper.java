package com.github.vzakharchenko.dynamic.orm.core.helper;

import org.apache.commons.lang3.ClassUtils;

import java.math.BigInteger;

public final class ClassHelper {
    private ClassHelper() {
    }

    public static Class<? extends Number> getNumberClass(String className) {
        return (Class<? extends Number>) getClass(className);
    }


    public static Long transform(BigInteger bigInteger) {
        return bigInteger != null ? bigInteger.longValue() : null;
    }


    private static Class getClass(String className) {
        try {
            return ClassUtils.getClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
