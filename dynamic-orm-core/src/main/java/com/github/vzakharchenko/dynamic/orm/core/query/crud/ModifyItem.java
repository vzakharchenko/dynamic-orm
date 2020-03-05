package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.Assert;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.MapModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.MapModelFactory;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 */
public class ModifyItem<MODEL extends DMLModel> {

    private final Map<Path<?>, Object> setMap = new HashMap<>();

    private final RelationalPath<?> qTable;

    private final Class<MODEL> modelClass;


    private Path<?> primaryKey;

    private BooleanExpression where;

    private BooleanExpression byId;

    private MapModel mapModel = null;

    private DiffColumnModel diffColumnModel = null;

    public ModifyItem(RelationalPath<?> qTable, Class<MODEL> modelClass) {
        this.qTable = qTable;
        this.modelClass = modelClass;
        this.primaryKey = ModelHelper.getPrimaryKeyColumn(qTable);

    }

    public Map<Path<?>, Object> getSetMap() {
        return Collections.unmodifiableMap(setMap);
    }

    public <T> void set(Path<T> column, T value) {
        setMap.put(column, value);
        if (byId != null && Objects.equals(primaryKey, column)) {
            byId();
        }
        mapModel = null;
        diffColumnModel = null;
    }

    public void set(Map<Path<?>, Object> setMap0) {
        this.setMap.putAll(setMap0);
        if (byId != null && setMap0.containsKey(primaryKey)) {
            byId();
        }
        mapModel = null;
        diffColumnModel = null;
    }


    public RelationalPath<?> getQTable() {
        return qTable;
    }

    public Class<MODEL> getModelClass() {
        return modelClass;
    }

    public ComparableExpressionBase<Comparable<?>> getPrimaryKey() {
        if (primaryKey == null) {
            return null;
        }
        return (ComparableExpressionBase<Comparable<?>>) primaryKey;
    }

    public Comparable<?> getPrimaryKeyValue() {
        if (primaryKey == null) {
            throw new IllegalStateException("primary key is not Found " + qTable);
        }
        return getValue(primaryKey, Comparable.class);
    }

    public Object getValue(Path<?> column) {
        return setMap.get(column);
    }

    public <T> T getValue(Path<?> column, Class<T> tClass) {
        return (T) getValue(column);
    }

    public BooleanExpression getWhere() {
        BooleanExpression where0 = null;

        if (byId()) {
            where0 = byId;
        }
        if (this.where != null) {
            if (where0 != null) {
                where0 = where0.and(this.where);
            } else {
                where0 = this.where;
            }
        }

        return where0;
    }


    public void and(BooleanExpression predicate) {
        where = (where == null) ?
                predicate : where.and(predicate);
    }

    public boolean byId() {
        if (byId != null) {
            return true;
        }
        ComparableExpressionBase<Comparable<?>> primaryKey0 = getPrimaryKey();
        if (primaryKey0 == null) {
            return false;
        }
        Comparable<?> primaryKeyValue = getPrimaryKeyValue();
        Assert.notNull(primaryKeyValue, "primary key is not found " + qTable);
        byId = primaryKey0.eq(primaryKeyValue);
        return true;
    }

    public MapModel getMapModel() {
        if (mapModel == null) {
            mapModel = MapModelFactory.buildMapModel(qTable, setMap);
        }
        return mapModel;
    }

    public DiffColumnModel getDiffColumnModel() {
        return diffColumnModel;
    }

    public void setDiffColumnModel(DiffColumnModel diffColumnModel) {
        this.diffColumnModel = diffColumnModel;
    }

    public boolean isEmpty() {
        return where == null && MapUtils.isEmpty(setMap);
    }
}
