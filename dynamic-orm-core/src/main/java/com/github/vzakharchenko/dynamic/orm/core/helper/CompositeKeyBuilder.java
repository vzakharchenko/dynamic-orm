package com.github.vzakharchenko.dynamic.orm.core.helper;

import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class CompositeKeyBuilder {

    private final Map<Path<? extends Serializable>, Serializable>
            compositeMap = new HashMap<>();
    private final RelationalPath qTable;

    private CompositeKeyBuilder(RelationalPath qTable) {
        this.qTable = qTable;
    }

    public static CompositeKeyBuilder create(RelationalPath qTable) {
        return new CompositeKeyBuilder(qTable);
    }

    public CompositeKeyBuilder addPrimaryKey(
            Path column, Serializable value) {
        compositeMap.put(column, value);
        return this;
    }

    public CompositeKey build() {
        return new CompositeKey(qTable, compositeMap);
    }


}
