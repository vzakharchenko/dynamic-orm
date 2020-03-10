package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Locale;

public class SequanceModel implements Serializable {

    private final String name;
    private BigInteger initial = BigInteger.valueOf(0L);
    private BigInteger increment = BigInteger.valueOf(1L);
    private BigInteger min;
    private BigInteger max;

    public SequanceModel(String name) {
        this.name = StringUtils.upperCase(name, Locale.US);
    }

    public String getName() {
        return name;
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
