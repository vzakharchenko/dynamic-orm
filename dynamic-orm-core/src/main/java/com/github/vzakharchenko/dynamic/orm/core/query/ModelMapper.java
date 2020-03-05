package com.github.vzakharchenko.dynamic.orm.core.query;

import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDelete;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class ModelMapper {
    private Map<String, Class<? extends DMLModel>> dmlModelMap = new ConcurrentHashMap<>();
    private Map<Class<? extends DMLModel>, RelationalPath> qModelMap = new ConcurrentHashMap<>();
    private Map<String, Path<?>> versionMap = new ConcurrentHashMap<>();
    private Map<String, SoftDelete<?>> softDeleteMap = new ConcurrentHashMap<>();


    public void validateModel(RelationalPath<?> qTable, Class<? extends DMLModel> modelClass) {
        String tableName = StringUtils.upperCase(qTable.getTableName());
        if (DynamicModel.class.isAssignableFrom(modelClass)) {
            if (dmlModelMap.containsKey(tableName)) {
                throw new IllegalStateException(qTable + " is not Dynamic table");
            }
            return;
        }
        Class<? extends DMLModel> aClass = dmlModelMap.get(tableName);

        if (aClass != null) {
            if (ObjectUtils.notEqual(aClass, modelClass)) {
                throw new IllegalStateException(
                        "for table " + tableName + "expected " + aClass +
                                " but found " + modelClass);
            }
        } else {
            dmlModelMap.put(tableName, modelClass);
            qModelMap.put(modelClass, qTable);
        }
    }

    public Path<?> getVersionColumn(RelationalPath<?> qTable,
                                    Class<? extends DMLModel> modelClass) {
        String tableName = StringUtils.upperCase(qTable.getTableName());
        Path<?> versionColumn = versionMap.get(tableName);
        if (versionColumn == null) {

            versionColumn = ModelHelper.getVersonFromModel(qTable, modelClass);
            if (versionColumn != null) {
                versionMap.put(tableName, versionColumn);
            }
        }
        return versionColumn;
    }

    public SoftDelete<?> getSoftDeleteColumn(RelationalPath<?> qTable,
                                             Class<? extends DMLModel> modelClass) {
        String tableName = StringUtils.upperCase(qTable.getTableName());
        SoftDelete<?> softDelete = softDeleteMap.get(tableName);
        if (softDelete == null) {

            softDelete = ModelHelper.getSoftDeleteFromModel(qTable, modelClass);
            if (softDelete != null) {
                softDeleteMap.put(tableName, softDelete);
            }
        }
        return softDelete;
    }

    public RelationalPath<?> getQModel(Class<? extends DMLModel> modelClass) {
        if (DynamicModel.class.isAssignableFrom(modelClass)) {
            throw new IllegalStateException("dynamicModel is not supported. use another method");
        }
        RelationalPath<?> qTable = qModelMap.get(modelClass);
        if (qTable != null) {
            return qTable;
        }
        if (ModelHelper.hasQTableInModel(modelClass)) {
            RelationalPath<?> tableFromModel = ModelHelper.getQTableFromModel(modelClass);
            validateModel(tableFromModel, modelClass);
            return tableFromModel;
        }
        return null;
    }

    public void validateVersionColumn(Path<?> versionColumn) {

        String tableName = StringUtils.upperCase(ModelHelper.getTableName(versionColumn));
        Path<?> oldVersionColumn = versionMap.get(tableName);
        if (oldVersionColumn != null && ObjectUtils.notEqual(oldVersionColumn, versionColumn)) {
            throw new IllegalStateException("for table " + tableName +
                    "expected version Column" + versionColumn +
                    " but found " + oldVersionColumn);
        }
    }

    public void clear() {
        dmlModelMap.clear();
        qModelMap.clear();
        versionMap.clear();
        softDeleteMap.clear();
    }
}
