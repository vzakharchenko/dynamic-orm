package com.github.vzakharchenko.dynamic.orm.core;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 13.04.15
 * Time: 18:16
 */
public interface DMLModel extends Serializable {

    default boolean isDynamicModel() {
        return false;
    }
}
