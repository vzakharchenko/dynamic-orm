package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.CacheHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.google.common.collect.Maps;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 */
public abstract class DiffColumnModelFactory {

    public static <TYPE> DiffColumn<TYPE> buildDiffColumn(
            Path<TYPE> column, TYPE oldValue, TYPE newValue) {
        return new DiffColumn<>(column, oldValue, newValue);
    }

    private static boolean hasOldValue(Map<Path<?>, DiffColumn<?>> primaryDiff) {
        return primaryDiff.entrySet().stream().anyMatch(entry -> entry.getValue()
                .getOldValue() != null);
    }

    private static boolean hasNewValue(Map<Path<?>, DiffColumn<?>> primaryDiff) {
        return primaryDiff.entrySet().stream().anyMatch(entry -> entry.getValue()
                .getNewValue() != null);
    }

    public static <MODEL extends DMLModel> MODEL buildOldModel(
            Class<MODEL> modelClass, DiffColumnModel diffColumnModel) {

        Map<Path<?>, DiffColumn<?>> primaryDiff = diffColumnModel.getColumnDiffPrimaryKey();

        if (!hasOldValue(primaryDiff)) {
            return null;
        }
        RelationalPath<?> qTable = diffColumnModel.getQTable();
        MODEL model = CacheHelper.newInstance(qTable, modelClass);
        for (Path<?> column : qTable.getColumns()) {
            DiffColumn diffColumn = diffColumnModel.getColumnDiff(column);
            ModelHelper.setColumnValue(model, column, diffColumn.getOldValue());
        }
        return model;

    }

    public static <MODEL extends DMLModel> MODEL buildNewModel(
            Class<MODEL> modelClass, DiffColumnModel diffColumnModel) {
        Map<Path<?>, DiffColumn<?>> primaryDiff = diffColumnModel.getColumnDiffPrimaryKey();

        if (!hasNewValue(primaryDiff)) {
            return null;
        }
        RelationalPath<?> qTable = diffColumnModel.getQTable();
        MODEL model = CacheHelper.newInstance(qTable, modelClass);
        for (Path<?> column : qTable.getColumns()) {
            DiffColumn diffColumn = diffColumnModel.getColumnDiff(column);
            ModelHelper.setColumnValue(model, column, diffColumn.getNewValue());
        }
        return model;

    }

    public static DiffColumnModel buildDiffColumnModel(
            RelationalPath<?> qTable, Map<Path<?>, DiffColumn<?>> diffColumnMap) {
        return new DiffColumnModel(qTable, diffColumnMap);
    }

    private static RelationalPath<?> getQTable(MapModel oldMapModel,
                                               MapModel affectedMapModel) {
        RelationalPath<?> qTable;
        if (oldMapModel != null) {
            qTable = oldMapModel.getQTable();
            Assert.isTrue(Objects
                            .equals(oldMapModel.getEffectedColumns().size(), qTable
                                    .getColumns().size()),
                    "the size of the columns old model does not match with the size" +
                            " of the columns in the table");
        } else if (affectedMapModel != null) {
            qTable = affectedMapModel.getQTable();
        } else {
            throw new IllegalStateException("Old model and new  model are null");
        }
        return qTable;
    }

    private static Map<Path<?>, DiffColumn<?>> getDiffColumnMap(
            Map<Path<?>, Object> oldDiffModels,
            Map<Path<?>, Object> newDiffModels,
            List<Path<?>> columns
    ) {
        Map<Path<?>, DiffColumn<?>> diffColumnMap = Maps
                .newHashMapWithExpectedSize(columns.size());
        columns.forEach(column -> {
            boolean containsNewKey = newDiffModels.containsKey(column);
            boolean containsOldKey = oldDiffModels.containsKey(column);
            if (!containsNewKey && !containsOldKey) {
                throw new IllegalStateException(column + " is not loaded from cache");
            }
            Object oldValue = oldDiffModels.get(column);
            DiffColumn<?> diffColumn;
            if (containsNewKey) {
                Object newValue = newDiffModels.get(column);
                diffColumn = buildDiffColumn((Path) column, oldValue, newValue);
            } else {
                diffColumn = buildDiffColumn((Path) column, oldValue, oldValue);
            }
            diffColumnMap.put(column, diffColumn);
        });
        return diffColumnMap;
    }

    public static DiffColumnModel buildDiffColumnModel(
            MapModel oldMapModel, MapModel affectedMapModel) {
        RelationalPath<?> qTable = getQTable(oldMapModel, affectedMapModel);
        Map<Path<?>, Object> oldDiffModels =
                oldMapModel != null ? oldMapModel.getDiffModel() : Collections.emptyMap();
        Map<Path<?>, Object> newDiffModels =
                affectedMapModel != null ? affectedMapModel.getDiffModel() : Collections
                        .emptyMap();
        List<Path<?>> columns = qTable.getColumns();
        Map<Path<?>, DiffColumn<?>> diffColumnMap =
                getDiffColumnMap(oldDiffModels, newDiffModels, columns);
        return new DiffColumnModel(qTable, diffColumnMap);
    }
}
