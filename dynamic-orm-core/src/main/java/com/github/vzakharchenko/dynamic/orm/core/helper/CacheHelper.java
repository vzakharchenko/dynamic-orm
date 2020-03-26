package com.github.vzakharchenko.dynamic.orm.core.helper;

import com.github.vzakharchenko.dynamic.orm.core.AccessQueryContext;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.github.vzakharchenko.dynamic.orm.core.cache.CachedAllData;
import com.github.vzakharchenko.dynamic.orm.core.cache.MapModel;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class CacheHelper {

//    public static void checkModelIsDeleted(
//            QueryContextImpl queryContext, DMLModel model, RelationalPath<?> qTable) {
//        CompositeKey primaryKeyCacheKey = buildPrimaryKeyCacheModel(model, qTable);
//        if (queryContext.getTransactionCache().isDeleted(primaryKeyCacheKey)) {
//            throw new IllegalStateException(qTable + ":" + model + "is deleted");
//        }
//    }

    public static AccessQueryContext getAccessQueryContext(OrmQueryFactory ormQueryFactory) {
        Assert.notNull(ormQueryFactory, ormQueryFactory + " is null");
        return (AccessQueryContext) ormQueryFactory;
    }


    public static CachedAllData buildAllDataCache(RelationalPath<?> qTable) {
        return new CachedAllData(qTable);
    }


//    public static CompositeKey buildPrimaryKeyCacheModel(
//            DMLModel model, RelationalPath<?> qTable) {
//        return PrimaryKeyHelper
//                .getPrimaryKeyValues(model, qTable);
//    }

    public static <MODEL extends DMLModel> MODEL newInstance(
            RelationalPath<?> qTable, Class<MODEL> modelClass) {
        if (qTable instanceof QDynamicTable) {
            if (ObjectUtils.notEqual(DynamicTableModel.class, modelClass)) {
                throw new IllegalStateException("for dynamic Table use only " +
                        DynamicTableModel.class + " DML Model");
            }
            QDynamicTable dynamicTable = (QDynamicTable) qTable;
            return (MODEL) new DynamicTableModel(dynamicTable);
        }
        try {
            return modelClass.newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <MODEL extends DMLModel> MODEL buildModel(
            RelationalPath<?> qTable, Class<MODEL> modelClass, Map<Path<?>, Object> diffModel) {
        try {
            MODEL model = null;
            if (MapUtils.isNotEmpty(diffModel)) {
                model = newInstance(qTable, modelClass);
                List<Path<?>> columns = qTable.getColumns();
                for (Path<?> column : columns) {
                    ModelHelper.setColumnValue(model, column, diffModel.get(column));
                }
            }
            return model;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <MODEL extends DMLModel> MODEL buildModel(
            Class<MODEL> modelClass, MapModel mapModel) {
        try {
            if (mapModel != null) {
                RelationalPath<?> qTable = mapModel.getQTable();
                return buildModel(qTable, modelClass, mapModel.getDiffModel());
            }
            return null;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <MODEL extends DMLModel> Map<Path<?>, Object> buildMapFromModel(
            RelationalPath<?> qTable, MODEL model) {
        try {
            Map<Path<?>, Object> modelMap = new HashMap<>();
            if (model != null) {
                List<Path<?>> columns = qTable.getColumns();
                for (Path column : columns) {
                    Object columnValue = ModelHelper.getValueFromModelByColumn(model, column);
                    modelMap.put(column, columnValue);
                }
            }
            return Collections.unmodifiableMap(modelMap);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
