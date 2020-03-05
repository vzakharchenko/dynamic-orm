package com.github.vzakharchenko.dynamic.orm.core.dynamic.dml;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;

import java.util.Collection;

/**
 *
 */
public interface DynamicModel extends DMLModel, Cloneable {
    void addColumnValue(String column, Object value);

    <T> T getValue(String column, Class<T> tClass);

    Object getValue(String column);

    Collection<String> getColumnNames();

    DynamicTableModel clone();

    @Override
    default boolean isDynamicModel() {
        return true;
    }

    QDynamicTable getQTable();
}
