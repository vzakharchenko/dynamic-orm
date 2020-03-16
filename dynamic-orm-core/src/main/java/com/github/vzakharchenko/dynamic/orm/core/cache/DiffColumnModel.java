package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 */
public final class DiffColumnModel implements Serializable {
    private final RelationalPath<?> qTable;
    private final Map<Path<?>, DiffColumn<?>> diffColumnMap;

    protected DiffColumnModel(
            RelationalPath<?> qTable, Map<Path<?>, DiffColumn<?>> diffColumnMap) {
        this.qTable = qTable;
        this.diffColumnMap = Collections.unmodifiableMap(diffColumnMap);

    }


    public Map<Path<?>, DiffColumn<?>> getDiffModels() {
        return diffColumnMap;
    }

    public Map<Path<?>, DiffColumn<?>> getOnlyChangedColumns() {
        Map<Path<?>, DiffColumn<?>> onlyChangedColumns = new HashMap<>();
        for (Map.Entry<Path<?>, DiffColumn<?>> entry : diffColumnMap.entrySet()) {
            DiffColumn<?> value = entry.getValue();
            if (ObjectUtils.notEqual(value.getOldValue(), value.getNewValue())) {
                onlyChangedColumns.put(entry.getKey(), value);
            }
        }
        return Collections.unmodifiableMap(onlyChangedColumns);
    }

    public List<Path<?>> getEffectedColumns() {
        return ImmutableList.copyOf(diffColumnMap.keySet());
    }

    public <TYPE> DiffColumn<TYPE> getColumnDiff(Path<TYPE> column) {
        return (DiffColumn<TYPE>) diffColumnMap.get(column);
    }

    public Map<Path<?>, DiffColumn<?>> getColumnDiffPrimaryKey() {
        Map<Path<?>, DiffColumn<?>> primaryColumnMap = new HashMap<>();
        PrimaryKeyHelper.getPrimaryKeyColumns(qTable).forEach(
                (Consumer<Path<?>>) path -> primaryColumnMap.put(path, getColumnDiff(path)));
        return primaryColumnMap;
    }

    public <TYPE> DiffColumn<TYPE> getActualColumnDiff(Path<TYPE> column) {
        return (DiffColumn<TYPE>) diffColumnMap.get(column);
    }

    public RelationalPath<?> getQTable() {
        return qTable;
    }
}
