package com.github.vzakharchenko.dynamic.orm.core.mapper;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.google.common.collect.Maps;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.sql.RelationalPath;

import java.util.List;
import java.util.Map;

/**
 * Created by vzakharchenko on 04.08.14.
 */
public class StaticTableMappingProjection<MODEL extends DMLModel>
        extends AbstractMappingProjection<MODEL> {

    protected Class<MODEL> modelClass;
    protected Path[] paths;
    protected ComparableExpressionBase primaryKey;
    protected RelationalPath<?> qTable;

    private StaticTableMappingProjection(Class<? super MODEL> type,
                                         Expression<?>... args) {
        super(type, args);
    }

    public StaticTableMappingProjection(RelationalPath<?> qTable, Class<MODEL> modelClass) {
        this(modelClass, buildTypeArrayFromTable(qTable));
        this.paths = buildTypeArrayFromTable(qTable);
        this.modelClass = modelClass;
        this.primaryKey = ModelHelper.getPrimaryKey(qTable);
        this.qTable = qTable;
    }

    protected static Path[] buildTypeArrayFromTable(RelationalPath<?> qTable) {
        List columns = qTable.getColumns();
        return (Path[]) columns.toArray(new Path[columns.size()]);
    }


    @Override
    public Map<Path<?>, Object> createMap(RelationalPath<?> relationalPath, MODEL model) {
        try {
            Map<Path<?>, Object> maps = Maps.newHashMapWithExpectedSize(paths.length);
            for (Path column : paths) {
                maps.put(column, ModelHelper.getValueFromModelByColumn(model, column));
            }
            return maps;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public MODEL map(Tuple row) {
        try {

            if (primaryKey != null && row.get((Expression<Object>) primaryKey) == null) {
                return null;
            }

            MODEL model = createModel();
            for (Path path : paths) {
                Object value = row.get(path);
                ModelHelper.setColumnValue(model, path, value);
            }
            return model;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
