package com.github.vzakharchenko.dynamic.orm.core.helper;

import com.querydsl.core.types.Path;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 */
public abstract class VersionHelper {

    public static Serializable getCurrentVersion(Path versionColumn, DMLModel dmlModel) {
        return (Serializable) ModelHelper.getValueFromModelByColumn(dmlModel, versionColumn);
    }

    public static Serializable incrementVersion(Path versionColumn, DMLModel dmlModel) {
        Serializable currentVersion = getCurrentVersion(versionColumn, dmlModel);
        return increment(currentVersion, versionColumn.getType());
    }

    public static void setInitialVersion(Path versionColumn, DMLModel dmlModel) {
        Serializable currentVersion = getCurrentVersion(versionColumn, dmlModel);
        if (currentVersion != null) {
            return;
        }
        Serializable increment = increment(null, versionColumn.getType());
        ModelHelper.setColumnValue(dmlModel, versionColumn, increment);
    }

    private static Serializable increment(Serializable currentVersion, Class typeClass) {
        if (Number.class.isAssignableFrom(typeClass)) {
            return incrementNumber(currentVersion, typeClass);
        } else if (Date.class.isAssignableFrom(typeClass)) {
            return incrementDate(typeClass);
        }
        throw new IllegalStateException(typeClass + " is not supported as version type.");
    }

    private static <TYPE extends Serializable> TYPE incrementDate(Class<TYPE> typeClass) {
        return buildCurrentDate(typeClass);
    }

    private static <TYPE extends Serializable> TYPE incrementNumber(TYPE currentVersion,
                                                                    Class<TYPE> typeClass) {
        if (currentVersion == null) {
            return buildDefaultNumber(typeClass);
        }
        if (Integer.class.isAssignableFrom(typeClass)) {
            Integer intVersion = (Integer) currentVersion + 1;
            return (TYPE) intVersion;
        } else if (Long.class.isAssignableFrom(typeClass)) {
            Long longVersion = (Long) currentVersion + 1;
            return (TYPE) longVersion;
        } else if (BigInteger.class.isAssignableFrom(typeClass)) {
            BigInteger bigIntegerVersion = (BigInteger) currentVersion;
            bigIntegerVersion = bigIntegerVersion.add(BigInteger.ONE);
            return (TYPE) bigIntegerVersion;
        }
        throw new IllegalStateException(typeClass + " is not supported as version type.");
    }

    private static <TYPE extends Serializable> TYPE buildDefaultNumber(Class<TYPE> typeClass) {
        try {
            return typeClass.getConstructor(String.class).newInstance("0");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static <TYPE extends Serializable> TYPE buildCurrentDate(Class<TYPE> typeClass) {
        try {

            return typeClass.getConstructor(long.class).newInstance(System.currentTimeMillis());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
