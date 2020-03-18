package com.github.vzakharchenko.dynamic.orm.core.helper;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.DynamicTableHelper;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicModel;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDelete;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 13.04.15
 * Time: 19:16
 */
public abstract class ModelHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelHelper.class);

    public static <T> T getValueFromModelByColumn(DMLModel modeltype, Path<T> path) {
        if (path == null) {
            return null;
        }
        if (modeltype instanceof DynamicModel) {
            DynamicModel dynamicModel = (DynamicModel) modeltype;
            return getValueFromModelByColumn(dynamicModel, path);
        }
        try {
            return (T) PropertyUtils.getSimpleProperty(modeltype, getColumnName(path));
        } catch (Exception e) {
            LOGGER.error(
                    "Failed to get property " + getColumnName(path) + " from model :" +
                            modeltype + "; path=" + path);
            throw new IllegalStateException(e);
        }
    }

    public static <T> T getValueFromModelByColumn(DynamicModel modeltype, Path<T> path) {
        if (path == null) {
            return null;
        }
        return (T) modeltype.getValue(ModelHelper.getColumnRealName(path), Object.class);
    }

    public static String getTableName(Path path) {
        if (path == null) {
            return null;
        }
        if (path instanceof RelationalPath) {
            return ((RelationalPath) path).getTableName();
        }
        try {
            RelationalPath<?> qTable = getQTable(path);
            if (qTable == null) {
                throw new IllegalStateException(path + " is not Column");
            }
            return qTable.getTableName();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void setColumnValue(DMLModel modeltype, Path column, Object value) {
        try {
            String columnName = getColumnName(column);
            if (modeltype.isDynamicModel()) {
                DynamicModel dynamicModel = (DynamicModel) modeltype;
                dynamicModel.addColumnValue(columnName, value);
            } else {
                PropertyUtils.setSimpleProperty(modeltype, columnName, value);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }


    public static Path getColumnByName(RelationalPath<?> qTable, String name) {
        List<Path<?>> columns = qTable.getColumns();
        for (Path path : columns) {
            String columnName = getColumnName(path);

            if (StringUtils.equalsIgnoreCase(columnName, name)) {
                return path;
            }
        }
        return null;
    }


    public static RelationalPath<?> getQTable(Path column) {
        if (column == null) {
            return null;
        }
        return (RelationalPath) column.getMetadata().getParent();
    }

    public static boolean hasQTableInModel(Class<? extends DMLModel> dmlModel) {
        return AnnotationHelper.getAnnotationHolder(dmlModel).isAnnotationQueryDslModelPresent();
    }

    public static RelationalPath<?> getQTableFromModel(Class<? extends DMLModel> dmlModel) {
        RelationalPath<?> qTable = AnnotationHelper.getAnnotationHolder(dmlModel).getQTable();
        if (qTable == null) {
            throw new IllegalStateException("annotation " + QueryDslModel.class.getName() +
                    " is not found on class " + dmlModel.getSimpleName());
        }
        return qTable;
    }

    public static Path<?> getVersonFromModel(
            RelationalPath<?> qTable, Class<? extends DMLModel> dmlModel) {
        if (qTable instanceof QDynamicTable) {
            QDynamicTable qDynamicTable = (QDynamicTable) qTable;
            return DynamicTableHelper.getVersionColumn(qDynamicTable);
        }
        return AnnotationHelper.getAnnotationHolder(dmlModel).getVersionColumn(qTable);
    }

    public static SoftDelete<?> getSoftDeleteFromModel(
            RelationalPath<?> qTable, Class<? extends DMLModel> dmlModel) {
        if (qTable instanceof QDynamicTable) {
            QDynamicTable qDynamicTable = (QDynamicTable) qTable;
            return DynamicTableHelper.getSoftDelete(qDynamicTable);
        }
        return AnnotationHelper.getAnnotationHolder(dmlModel).getSoftDelete(qTable);
    }

    public static String getSequanceNameFromModel(Class<? extends DMLModel> dmlModel) {
        return AnnotationHelper.getAnnotationHolder(dmlModel).getSequanceName();
    }

    public static RelationalPath<?> getQTableFromModel(DMLModel dmlModel) {
        if (dmlModel.isDynamicModel()) {
            DynamicModel dynamicModel = (DynamicModel) dmlModel;
            return dynamicModel.getQTable();
        } else {
            return getQTableFromModel(dmlModel.getClass());
        }
    }

    public static String getColumnRealName(Path column) {
        RelationalPath<?> qTable = getQTable(column);
        Assert.notNull(qTable);
        ColumnMetadata metadata = qTable.getMetadata(column);
        Assert.notNull(metadata, column + " metadata is null");
        return metadata.getName();
    }

    public static int getColumnSize(Path column) {
        RelationalPath<?> qTable = getQTable(column);
        Assert.notNull(qTable);
        ColumnMetadata metadata = qTable.getMetadata(column);
        Assert.notNull(metadata, column + " metadata is null");
        return metadata.getSize();
    }

    public static int getColumnDigitSize(Path column) {
        RelationalPath<?> qTable = getQTable(column);
        Assert.notNull(qTable);
        ColumnMetadata metadata = qTable.getMetadata(column);
        Assert.notNull(metadata, column + " metadata is null");
        return metadata.getDigits();
    }

    public static String getColumnName(Path column) {
        if (column == null) {
            return null;
        }
        return column.getMetadata().getName();
    }


    public static Expression getColumnFromExpression(Expression expression) {
        if (expression instanceof Path) {
            return expression;
        } else {
            if (expression instanceof Operation) {
                Operation operation = (Operation) expression;
                return operation.getArg(0);
            }
        }
        throw new IllegalArgumentException(expression + " is not supported");
    }

    public static Expression getAliasFromExpression(Expression<?> expression) {
        if (expression instanceof Path) {
            return null;
        } else {
            if (expression instanceof Operation) {
                Operation operation = (Operation) expression;
                if (Objects.equals(operation.getOperator(), Ops.ALIAS)) {
                    return operation.getArg(1);
                }
            }
        }
        return null;
    }

    public static <MODEL extends DMLModel> MODEL cloneModel(MODEL model) {
        try {
            if (model instanceof DynamicModel) {
                return (MODEL) ((DynamicModel) model).clone();
            }
            return (MODEL) BeanUtilsBean2.getInstance().cloneBean(model);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
