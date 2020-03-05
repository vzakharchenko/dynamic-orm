package com.github.vzakharchenko.dynamic.orm.core.cache;


import com.querydsl.core.types.Path;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;

/**
 *
 */
public class DiffColumn<TYPE> implements Serializable {
    private final Path<TYPE> column;
    private final TYPE oldValue;
    private final TYPE newValue;

    protected DiffColumn(Path<TYPE> column, TYPE oldValue, TYPE newValue) {
        this.column = column;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Path<TYPE> getColumn() {
        return column;
    }

    public TYPE getOldValue() {
        return oldValue;
    }

    public TYPE getNewValue() {
        return newValue;
    }

    public boolean isChanged() {
        return ObjectUtils.notEqual(oldValue, newValue);
    }
}
