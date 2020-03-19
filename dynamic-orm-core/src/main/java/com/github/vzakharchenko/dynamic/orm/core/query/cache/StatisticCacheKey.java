package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public class StatisticCacheKey implements Serializable {
    private final String name;

    public StatisticCacheKey(String name) {
        this.name = StringUtils.upperCase(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatisticCacheKey that = (StatisticCacheKey) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
