package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.querydsl.core.types.Path;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDelete;

import java.util.List;

/**
 *
 */
public abstract class DynamicTableHelper {

    public static PKGenerator<?> getPkGenerator(QDynamicTable qDynamicTable) {
        return qDynamicTable.getPkGenerator();
    }

    public static Path<?> getVersionColumn(QDynamicTable qDynamicTable) {
        return qDynamicTable.getVersionColumn();
    }

    public static SoftDelete<?> getSoftDelete(QDynamicTable qDynamicTable) {
        return qDynamicTable.getSoftDelete();
    }

    public static List<IndexData> getIndexDatas(QDynamicTable qDynamicTable) {
        return qDynamicTable.getIndexDatas();
    }
}
