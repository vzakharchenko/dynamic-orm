package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.CacheHelper;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class MapModel implements Serializable {
    private final Map<Path<?>, Object> diffModel;
    private final RelationalPath<?> qTable;

    protected MapModel(RelationalPath<?> qTable, Map<Path<?>, Object> diffModel) {
        this.diffModel = Collections.unmodifiableMap(new HashMap<>(diffModel));
        this.qTable = qTable;
    }

    protected MapModel(RelationalPath<?> qTable, DMLModel model) {
        this(qTable, Collections.unmodifiableMap(CacheHelper.buildMapFromModel(qTable, model)));
    }

    public Map<Path<?>, Object> getDiffModel() {
        return diffModel;
    }

    public List<Path<?>> getEffectedColumns() {
        return ImmutableList.copyOf(diffModel.keySet());
    }

    public Object getColumnValue(Path column) {
        return diffModel.get(column);
    }

    public <TYPE> TYPE getColumnValue(Path column, Class<TYPE> typeClass) {
        return (TYPE) getColumnValue(column);
    }

    public boolean contains(Path column) {
        return diffModel.containsKey(column);
    }

    public RelationalPath<?> getQTable() {
        return qTable;
    }
}
