package com.github.vzakharchenko.dynamic.orm.core.mapper;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.querydsl.sql.RelationalPath;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 13.04.15
 * Time: 19:23
 */
public abstract class TableMappingProjectionFactory {

    private static final Map<RelationalPath, StaticTableMappingProjection> CACHE =
            new HashMap<>();

    public static <MODEL extends DMLModel> StaticTableMappingProjection<MODEL> buildMapper(
            RelationalPath<?> qTable, Class<MODEL> modelClass) {
        //todo factory needed
        if (qTable instanceof QDynamicTable) {
            QDynamicTable qDynamicTable = (QDynamicTable) qTable;
            if (!modelClass.isAssignableFrom(DynamicTableModel.class)) {
                throw new IllegalStateException("For dynamic Tables support only " +
                        DynamicTableModel.class + " DML Model");
            }
            return (StaticTableMappingProjection<MODEL>) new DynamicTableMappingProjection(
                    qDynamicTable,
                    DynamicTableModel.class);
        }

        StaticTableMappingProjection staticTableMappingProjection = CACHE.get(qTable);
        if (staticTableMappingProjection != null) {
            return staticTableMappingProjection;
        }
        staticTableMappingProjection = new StaticTableMappingProjection<>(qTable, modelClass);
        CACHE.put(qTable, staticTableMappingProjection);
        return staticTableMappingProjection;
    }


}
