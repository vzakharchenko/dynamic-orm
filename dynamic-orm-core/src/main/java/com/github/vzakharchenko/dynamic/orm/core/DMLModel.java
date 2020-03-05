package com.github.vzakharchenko.dynamic.orm.core;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 13.04.15
 * Time: 18:16
 */
public interface DMLModel {

    default boolean isDynamicModel() {
        return false;
    }
}
