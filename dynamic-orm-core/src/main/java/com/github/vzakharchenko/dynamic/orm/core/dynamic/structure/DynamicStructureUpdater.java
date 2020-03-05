package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;

import java.util.Collection;
import java.util.Map;

/**
 *
 */
public interface DynamicStructureUpdater {
    void update(Map<String, QDynamicTable> qDynamicTables);

    void update(QDynamicTable qDynamicTable);

    void update(Collection<QDynamicTable> qDynamicTables);
}
