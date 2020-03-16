package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.querydsl.core.types.Path;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class IndexData implements Serializable {
    private final List<Path<?>> columns;
    private final boolean unique;
    private final boolean clustered;

    public IndexData(List<Path<?>> columns, boolean unique, boolean clustered) {
        this.columns = columns;
        this.unique = unique;
        this.clustered = clustered;
    }

    public List<Path<?>> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isClustered() {
        return clustered;
    }
}
