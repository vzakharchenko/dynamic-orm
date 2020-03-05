package com.github.vzakharchenko.dynamic.orm.model;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 26.04.15
 * Time: 20:47
 */
public class Testtable implements DMLModel {
    private Integer id;

    private Integer test2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTest2() {
        return test2;
    }

    public void setTest2(Integer test2) {
        this.test2 = test2;
    }

}
