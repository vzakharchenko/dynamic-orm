package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.types.Null;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class JavaTypeUtils {

    private static final Map<Integer, Class<?>> DEFAULT_TYPES = new HashMap<>();

    // CHECKSTYLE:OFF
    static {
        registerDefault(-101, Object.class);
        registerDefault(-102, Date.class);
        registerDefault(2012, Object.class);
        registerDefault(2013, Date.class);
        registerDefault(2014, Date.class);

        // BOOLEAN
        registerDefault(Types.BIT, Boolean.class);
        registerDefault(Types.BOOLEAN, Boolean.class);

        // NUMERIC
        registerDefault(Types.BIGINT, Long.class);
        registerDefault(Types.DECIMAL, BigDecimal.class);
        registerDefault(Types.DOUBLE, Double.class);
        registerDefault(Types.FLOAT, Float.class);
        registerDefault(Types.INTEGER, Integer.class);
        registerDefault(Types.NUMERIC, BigDecimal.class);
        registerDefault(Types.REAL, Float.class);
        registerDefault(Types.SMALLINT, Short.class);
        registerDefault(Types.TINYINT, Byte.class);

        // DATE and TIME
        registerDefault(Types.DATE, Date.class);
        registerDefault(Types.TIME, Date.class);
        registerDefault(Types.TIMESTAMP, Date.class);

        // TEXT
        registerDefault(Types.NCHAR, String.class);
        registerDefault(Types.CHAR, String.class);
        registerDefault(Types.NCLOB, String.class);
        registerDefault(Types.CLOB, String.class);
        registerDefault(Types.LONGNVARCHAR, String.class);
        registerDefault(Types.LONGVARCHAR, String.class);
        registerDefault(Types.SQLXML, String.class);
        registerDefault(Types.NVARCHAR, String.class);
        registerDefault(Types.VARCHAR, String.class);

        // byte[]
        registerDefault(Types.BINARY, byte[].class);
        registerDefault(Types.LONGVARBINARY, byte[].class);
        registerDefault(Types.VARBINARY, byte[].class);

        // BLOB
        registerDefault(Types.BLOB, byte[].class);

        // OTHER
        registerDefault(Types.ARRAY, Object[].class);
        registerDefault(Types.DISTINCT, Object.class);
        registerDefault(Types.DATALINK, Object.class);
        registerDefault(Types.JAVA_OBJECT, Object.class);
        registerDefault(Types.NULL, Null.class);
        registerDefault(Types.OTHER, Object.class);
        registerDefault(Types.REF, Object.class);
        registerDefault(Types.ROWID, Object.class);
        registerDefault(Types.STRUCT, Object.class);

    }

    private JavaTypeUtils() {
    }

    private static void registerDefault(int sqlType, Class<?> javaType) {
        DEFAULT_TYPES.put(sqlType, javaType);
    }

    public static Class getBySqlType(int sqlType) {
        return DEFAULT_TYPES.get(sqlType);
    }

    public static Class getPathClass(int sqlType) {
        switch (sqlType) {
            case -102:
            case 2014:
            case Types.TIMESTAMP:
                return DateTimePath.class;
            case 2013:
            case Types.TIME:
                return TimePath.class;
            case Types.DATE:
                return DatePath.class;
            case Types.BIT:
            case Types.BOOLEAN:
                return BooleanPath.class;
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.NUMERIC:
            case Types.REAL:
            case Types.SMALLINT:
            case Types.TINYINT:
                return NumberPath.class;
            case Types.NCHAR:
            case Types.CHAR:
            case Types.NCLOB:
            case Types.CLOB:
            case Types.LONGNVARCHAR:
            case Types.LONGVARCHAR:
            case Types.SQLXML:
            case Types.NVARCHAR:
            case Types.VARCHAR:
                return StringPath.class;
            case Types.BINARY:
            case Types.LONGVARBINARY:
            case Types.VARBINARY:
            case Types.BLOB:
            case Types.ARRAY:
            case Types.DISTINCT:
            case Types.DATALINK:
            case Types.JAVA_OBJECT:
            case Types.NULL:
            case Types.OTHER:
            case Types.REF:
            case Types.ROWID:
            case Types.STRUCT:
                return SimplePath.class;
            default: {
                throw new IllegalStateException(sqlType + " does not support");
            }
        }
    }

    public static Path<?> createPath(String pathClassName, Class type, PathMetadata metadata) {
        switch (pathClassName) {
            case "DateTimePath":
                return Expressions.dateTimePath(type, metadata);
            case "TimePath":
                return Expressions.timePath(type, metadata);
            case "DatePath":
                return Expressions.datePath(type, metadata);
            case "BooleanPath":
                return Expressions.booleanPath(metadata);
            case "NumberPath":
                return Expressions.numberPath(type, metadata);
            case "StringPath":
                return Expressions.stringPath(metadata);
            case "SimplePath":
                return Expressions.simplePath(type, metadata);

            default: {
                throw new IllegalStateException(pathClassName + " does not support");
            }
        }
    }
    // CHECKSTYLE:ON
}
