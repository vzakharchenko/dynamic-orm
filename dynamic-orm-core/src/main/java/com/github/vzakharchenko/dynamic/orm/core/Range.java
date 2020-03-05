package com.github.vzakharchenko.dynamic.orm.core;


/**
 * Created by vzakharchenko on 17.12.14.
 */
public class Range {
    private final Integer limit;
    private Integer offset;

    public Range(Integer offset, Integer limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public Range(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getLimit() {
        return limit;
    }
}
