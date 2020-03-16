package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public class DeleteBuilderImpl<MODEL extends DMLModel> extends AbstractDeleteBuilder<MODEL> {


    protected DeleteBuilderImpl(Class<MODEL> modelClass,
                                RelationalPath<?> qTable,
                                QueryContextImpl queryContext) {
        super(modelClass, qTable, queryContext);
    }


    protected DeleteBuilderImpl<MODEL> versionColumn(Path column) {
        versionColumn = column;
        return this;
    }

    @Override
    public DeleteModelBuilder<MODEL> delete(MODEL model) {
        return new DeleteModelBuilderImpl<>(qTable,
                modelClass, model, queryContext, versionColumn);
    }

    @Override
    public Long deleteModelByIds(MODEL... models) {
        return deleteModelByIds(Arrays.asList(models));
    }

    @Override
    public Long deleteModelByIds(List<MODEL> models) {
        DeleteModelBuilder<MODEL> deleteModelBuilder = null;

        for (MODEL model : models) {
            if (deleteModelBuilder == null) {
                deleteModelBuilder = delete(model).byId();
            } else {
                deleteModelBuilder = deleteModelBuilder.batch(model).byId();
            }
        }
        if (deleteModelBuilder == null) {
            return 0L;
        }
        return deleteModelBuilder.delete();
    }

    @Override
    public Long softDeleteModelByIds(MODEL... models) {
        return softDeleteModelByIds(Arrays.asList(models));
    }

    @Override
    public Long softDeleteModelByIds(List<MODEL> models) {
        List<Serializable> ids = new ArrayList<>(models.size());
        for (MODEL model : models) {
            ids.add(PrimaryKeyHelper.getPrimaryKeyValues(model, qTable));
        }
        return softDeleteByIds(ids);
    }

    @Override
    public Long softDeleteByIds(Serializable... ids) {
        return softDeleteByIds(Arrays.asList(ids));
    }

    @Override
    public Long softDeleteByIds(List<Serializable> ids) {
        SoftDelete softDelete = queryContext.getSoftDeleteColumn(qTable, modelClass);
        if (softDelete == null) {
            throw new IllegalStateException(com.github.vzakharchenko.dynamic.orm.core
                    .annotations.SoftDelete.class +
                    " is not found or softDelete column is not set on Dynamic table");
        }
        UpdateModelBuilder<MODEL> updateModelBuilder = null;
        for (Serializable id : ids) {
            if (updateModelBuilder == null) {
                updateModelBuilder = queryContext
                        .getOrmQueryFactory().modify(qTable, modelClass)
                        .versionColumn(versionColumn).updateBuilder();
            }
            PrimaryKeyHelper.updateModelBuilder(
                    updateModelBuilder, qTable, id);
            updateModelBuilder = updateModelBuilder
                    .set(softDelete.getColumn(), softDelete.getDeletedValue()).byId().batch();
        }
        return updateModelBuilder != null ? updateModelBuilder.update() : null;
    }

    @Override
    public Long deleteByIds(Serializable... ids) {
        return deleteByIds(Arrays.asList(ids));
    }

    @Override
    public Long deleteByIds(List<Serializable> ids) {
        if (!PrimaryKeyHelper.hasPrimaryKey(qTable)) {
            throw new IllegalArgumentException(qTable.getTableName() +
                    " does not contains Primary Key");
        }
        return deleteByIds0(ids);
    }

}
