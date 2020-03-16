package com.github.vzakharchenko.dynamic.orm.core.helper;

import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.springframework.util.Assert;

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
            Path column, Object value) {
        Assert.notNull(column, "Column is Null");
        Assert.notNull(value, "Value is Null");
        Assert.isTrue(PrimaryKeyHelper.getPrimaryKeyColumns(qTable).contains(column),
                qTable + " does not contain " + column + " primary key");
        Assert.isTrue(value.getClass().isAssignableFrom(column.getType()),
                "Type mismatch: " + value.getClass() + " is not accessible from " +
                        column.getType());
        compositeMap.put(column, (Serializable) value);
        return this;
    }

    public CompositeKey build() {
        return new CompositeKey(qTable, compositeMap);
    }


}
