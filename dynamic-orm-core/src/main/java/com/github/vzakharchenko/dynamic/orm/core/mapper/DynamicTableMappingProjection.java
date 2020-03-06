package com.github.vzakharchenko.dynamic.orm.core.mapper;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.google.common.collect.Maps;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;

import java.util.Collection;
import java.util.Map;

/**
 *
 */
public class DynamicTableMappingProjection
        extends StaticTableMappingProjection<DynamicTableModel> {

    private final QDynamicTable qDynamicTable;


    protected DynamicTableMappingProjection(QDynamicTable qTable,
                                            Class<DynamicTableModel> dynamicModelClass) {
        super(qTable, dynamicModelClass);
        qDynamicTable = qTable;
    }


    @Override
    public DynamicTableModel map(Tuple row) {
        try {

            if (primaryKey != null && row.get(primaryKey) == null) {
                return null;
            }

            DynamicTableModel model = createModel();
            for (Path path : paths) {
                model.addColumnValue(ModelHelper.getColumnName(path), row.get(path));
            }
            return model;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Map<Path<?>, Object> createMap(RelationalPath<?> relationalPath,
                                          DynamicTableModel model) {
        String tableName = ModelHelper.getTableName(qDynamicTable);
        if (!model.isTable(tableName)) {
            throw new IllegalStateException("expected " + tableName +
                    " DML model,  but found " + model.getTableName());
        }
        try {

            Collection<String> columnNames = model.getColumnNames();
            Map<Path<?>, Object> maps = Maps.newHashMapWithExpectedSize(columnNames.size());
            for (String columnName : columnNames) {
                Object columnValue = model.getValue(columnName, Object.class);
                qDynamicTable.checkColumn(columnName, columnValue);
                maps.put((Path<?>) qDynamicTable
                        .getColumnByName(columnName, Object.class), columnValue);
            }
            return maps;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    protected DynamicTableModel createModel() {
        return new DynamicTableModel(qDynamicTable);
    }
}
