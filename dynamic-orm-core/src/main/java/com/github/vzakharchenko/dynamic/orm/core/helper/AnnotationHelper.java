package com.github.vzakharchenko.dynamic.orm.core.helper;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.support.AnnotationHolder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 */
public abstract class AnnotationHelper {
    private static final ConcurrentMap<Class<? extends DMLModel>, AnnotationHolder>
            ANNOTATION_HOLDER_CONCURRENT_MAP = new ConcurrentHashMap<>();

    public static AnnotationHolder getAnnotationHolder(
            Class<? extends DMLModel> modelClass) {
        AnnotationHolder annotationHolder = ANNOTATION_HOLDER_CONCURRENT_MAP
                .get(modelClass);
        if (annotationHolder == null) {
            ANNOTATION_HOLDER_CONCURRENT_MAP.putIfAbsent(modelClass,
                    new AnnotationHolder(modelClass));
            annotationHolder = ANNOTATION_HOLDER_CONCURRENT_MAP
                    .get(modelClass);

        }
        return annotationHolder;
    }
}
