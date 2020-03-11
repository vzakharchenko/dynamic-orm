package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models;

import java.io.Serializable;
import java.math.BigInteger;

public class SchemaSequence implements Serializable {
    private String name;
    private BigInteger initial;
    private BigInteger increment;
    private BigInteger min;
    private BigInteger max;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getInitial() {
        return initial;
    }

    public void setInitial(BigInteger initial) {
        this.initial = initial;
    }

    public BigInteger getIncrement() {
        return increment;
    }

    public void setIncrement(BigInteger increment) {
        this.increment = increment;
    }

    public BigInteger getMin() {
        return min;
    }

    public void setMin(BigInteger min) {
        this.min = min;
    }

    public BigInteger getMax() {
        return max;
    }

    public void setMax(BigInteger max) {
        this.max = max;
    }
}
