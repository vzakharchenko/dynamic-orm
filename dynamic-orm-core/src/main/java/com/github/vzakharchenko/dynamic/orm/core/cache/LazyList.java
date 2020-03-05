package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 25.04.15
 * Time: 13:10
 */
public interface LazyList<MODEL extends DMLModel> {
    List<MODEL> getModelList();

    List<Serializable> getPrimaryKeyList();

    int size();

}
