package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.MapModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.MapModelFactory;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.collections4.MapUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ModifyItem<MODEL extends DMLModel> {

    private final Map<Path<?>, Object> setMap = new HashMap<>();

    private final RelationalPath<?> qTable;

    private final Class<MODEL> modelClass;


    private final PrimaryKey<?> primaryKey;

    private BooleanExpression where;

    private BooleanExpression byId0;

    private MapModel mapModel;

    private DiffColumnModel diffColumnModel;

    public ModifyItem(RelationalPath<?> qTable, Class<MODEL> modelClass) {
        this.qTable = qTable;
        this.modelClass = modelClass;
        this.primaryKey = PrimaryKeyHelper.getPrimaryKey(qTable);

    }

    public Map<Path<?>, Object> getSetMap() {
        return Collections.unmodifiableMap(setMap);
    }

    public <T> void set(Path<T> column, T value) {
        setMap.put(column, value);
        mapModel = null;
        diffColumnModel = null;
    }

    public void set(Map<Path<?>, Object> setMap0) {
        this.setMap.putAll(setMap0);
        if (byId0 != null && setMap0.keySet().containsAll(PrimaryKeyHelper
                .getPrimaryKeyColumns(qTable))) {
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

    public BooleanExpression getPrimaryKey() {
        if (primaryKey == null) {
            return null;
        }
        return PrimaryKeyHelper.getPrimaryKeyExpression(qTable, setMap);
    }

    public CompositeKey getPrimaryKeyValue() {
        if (primaryKey == null) {
            throw new IllegalStateException("primary key is not Found " + qTable);
        }
        return PrimaryKeyHelper.getCompositeKey(qTable, setMap);
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
            where0 = byId0;
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

    public boolean byIdInternal() {
        if (PrimaryKeyHelper.hasPrimaryKey(qTable)) {
            byId0 = PrimaryKeyHelper.getPrimaryKeyExpression(qTable, setMap);
            return true;
        }
        return false;
    }

    public boolean byId() {
        if (byId0 != null) {
            return true;
        } else {
            return byIdInternal();
        }
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
