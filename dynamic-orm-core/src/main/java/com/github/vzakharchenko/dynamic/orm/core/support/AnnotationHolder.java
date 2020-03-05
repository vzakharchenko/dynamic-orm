package com.github.vzakharchenko.dynamic.orm.core.support;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.SequanceName;
import com.github.vzakharchenko.dynamic.orm.core.annotations.SoftDelete;
import com.github.vzakharchenko.dynamic.orm.core.annotations.Version;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDeleteFactory;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;

import java.lang.reflect.Field;

/**
 *
 */
public class AnnotationHolder {
    private QueryDslModel queryDslModel;
    private Version version;
    private SoftDelete softDelete;
    private SequanceName sequanceName;

    private Field versionField;
    private Path<?> versionColumn;
    private Field softDeleteField;
    private com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDelete<?> softDeleteColumn;


    private RelationalPath<?> qTable = null;


    public AnnotationHolder(Class<? extends DMLModel> dmlModel) {
        queryDslModel = dmlModel.getAnnotation(QueryDslModel.class);
        sequanceName = dmlModel.getAnnotation(SequanceName.class);
        Field[] fields = dmlModel.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Version.class)) {
                versionField = field;
                version = field.getAnnotation(Version.class);
            }
            if (field.isAnnotationPresent(SoftDelete.class)) {
                softDeleteField = field;
                softDelete = field.getAnnotation(SoftDelete.class);
            }
        }
    }

    public boolean isAnnotationQueryDslModelPresent() {
        return queryDslModel != null;
    }

    public boolean isAnnotationVersionPresent() {
        return version != null;
    }

    public boolean isAnnotationSequanceNamePresent() {
        return sequanceName != null;
    }

    public boolean isAnnotationSoftDeletePresent() {
        return softDelete != null;
    }


    public RelationalPath<?> getQTable() {
        if (qTable == null && isAnnotationQueryDslModelPresent()) {
            try {
                qTable = queryDslModel.qTableClass().getConstructor(String.class)
                        .newInstance(queryDslModel.tableName());
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return qTable;
    }

    public PKGenerator<?> getPkGenerator() {
        PKGenerator<?> pkGenerator = null;
        if (isAnnotationQueryDslModelPresent()) {
            pkGenerator = queryDslModel.primaryKeyGenerator().getPkGenerator();
        }
        return pkGenerator;
    }

    public String getSequanceName() {
        if (isAnnotationSequanceNamePresent()) {
            return sequanceName.value();
        }
        return null;
    }


    public Path<?> getVersionColumn(RelationalPath<?> qTable0) {
        if (versionColumn == null && isAnnotationVersionPresent()) {
            versionColumn = ModelHelper.getColumnByName(qTable0, versionField.getName());
        }
        return versionColumn;
    }

    public com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDelete<?> getSoftDelete(
            RelationalPath<?> qTable0) {
        if (softDeleteColumn == null && isAnnotationSoftDeletePresent()) {
            softDeleteColumn = SoftDeleteFactory.createSoftDeleteString(
                    ModelHelper
                            .getColumnByName(qTable0, softDeleteField.getName()),
                    softDelete.deletedStatus(), softDelete.defaultStatus());
        }
        return softDeleteColumn;
    }


}
