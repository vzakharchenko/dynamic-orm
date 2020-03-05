package com.github.vzakharchenko.dynamic.orm.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target(
        {ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(
        RetentionPolicy.RUNTIME)
public @interface SoftDelete {
    String deletedStatus();

    String defaultStatus() default "";
}
