package com.github.vzakharchenko.dynamic.orm.core.dynamic.dml;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class DynamicTableModel implements DynamicModel {
    private Map<String, Object> columns = new HashMap<>();

    private QDynamicTable qDynamicTable;

    public DynamicTableModel(QDynamicTable qDynamicTable) {
        this.qDynamicTable = qDynamicTable;
    }


    @Override
    public void addColumnValue(String column, Object value) {
        qDynamicTable.checkColumn(column, value);
        Assert.hasText(column);
        columns.put(StringUtils.upperCase(column), value);
    }

    @Override
    public <T> T getValue(String column, Class<T> tClass) {
        Assert.hasText(column);
        return (T) getValue(column);
    }

    @Override
    public Object getValue(String column) {
        return columns.get(StringUtils.upperCase(column));
    }

    @Override
    public Collection<String> getColumnNames() {
        return columns.keySet();
    }

    @Override
    public DynamicTableModel clone() {
        try {
            DynamicTableModel dynamicTableModel = (DynamicTableModel) super.clone();
            dynamicTableModel.columns = new HashMap<>();
            dynamicTableModel.columns.putAll(columns);
            return this;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getTableName() {
        return ModelHelper.getTableName(qDynamicTable);
    }

    @Override
    public QDynamicTable getQTable() {
        return qDynamicTable;
    }

    public boolean isTable(String tableName) {
        return StringUtils.equalsIgnoreCase(tableName, getTableName());
    }
}
