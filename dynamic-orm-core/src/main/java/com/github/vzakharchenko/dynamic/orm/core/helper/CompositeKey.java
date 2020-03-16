package com.github.vzakharchenko.dynamic.orm.core.helper;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public final class CompositeKey implements Serializable {
    private final Map<Path<? extends Serializable>, Serializable> compositeMap;
    private final RelationalPath<?> qTable;

    protected CompositeKey(
            RelationalPath<?> qTable,
            Map<Path<? extends Serializable>, Serializable> compositeMap) {
        this.compositeMap = compositeMap;
        this.qTable = qTable;
    }

    public Map<Path<?>, Object> getCompositeMap() {
        return MapUtils.unmodifiableMap(compositeMap);
    }

    public Serializable getColumn(Path path) {
        return compositeMap.get(path);
    }

    public BooleanExpression getWherePart() {
        return PrimaryKeyExpressionHelper.getPrimaryKeyExpression(qTable, getCompositeMap());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompositeKey that = (CompositeKey) o;
        return compositeMap.equals(that.compositeMap) &&
                qTable.equals(that.qTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compositeMap, qTable);
    }

    public RelationalPath<?> getTable() {
        return qTable;
    }
}
