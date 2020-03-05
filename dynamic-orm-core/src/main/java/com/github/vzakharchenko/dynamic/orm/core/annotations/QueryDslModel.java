package com.github.vzakharchenko.dynamic.orm.core.annotations;

import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;
import com.querydsl.sql.RelationalPath;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryDslModel {
    Class<? extends RelationalPath<?>> qTableClass();

    String tableName();

    PrimaryKeyGenerators primaryKeyGenerator() default PrimaryKeyGenerators.DEFAULT;
}
