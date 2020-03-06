package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.querydsl.core.types.Path;

import java.io.Serializable;

/**
 *
 */
public class IndexData implements Serializable {
    private final Path<?> column;
    private final boolean unique;

    protected IndexData(Path<?> column, boolean unique) {
        this.column = column;
        this.unique = unique;
    }

    public Path<?> getColumn() {
        return column;
    }

    public boolean isUnique() {
        return unique;
    }
}
