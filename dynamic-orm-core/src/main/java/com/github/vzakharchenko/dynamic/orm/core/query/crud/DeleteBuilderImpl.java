package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModelFactory;
import com.github.vzakharchenko.dynamic.orm.core.cache.MapModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.RawCacheBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.dml.SQLDeleteClause;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.sql.Connection;
import java.util.*;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public class DeleteBuilderImpl<MODEL extends DMLModel> implements DeleteBuilder<MODEL> {

    private final QueryContextImpl queryContext;

    private final Class<MODEL> modelClass;

    private final RelationalPath<?> qTable;
    private final Path<?> versionColumn;
    private final AfterModify<MODEL> afterModify;

    // CHECKSTYLE:OFF
    protected DeleteBuilderImpl(Class<MODEL> modelClass, RelationalPath<?> qTable,
                                QueryContextImpl queryContext, Path<?> versionColumn) {
        this.modelClass = modelClass;
        this.qTable = qTable;
        this.queryContext = queryContext;
        this.afterModify = new AfterModifyImpl<>(qTable, modelClass, queryContext);
        this.versionColumn = versionColumn;
    }
    // CHECKSTYLE:ON

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
            ids.add(ModelHelper.getPrimaryKeyValue(model, qTable, Serializable.class));
        }
        return softDeleteByIds(ids);
    }

    @Override
    public Long softDeleteByIds(Serializable... ids) {
        return softDeleteByIds(Arrays.asList(ids));
    }

    @Override
    public Long softDeleteByIds(List<Serializable> ids) {
        SoftDelete<?> softDelete = queryContext.getSoftDeleteColumn(qTable, modelClass);
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
            updateModelBuilder = updateModelBuilder
                    .set(ModelHelper.getPrimaryKeyColumn(qTable), id)
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
        DBHelper.transactionCheck();
        ComparableExpressionBase primaryKey = ModelHelper.getPrimaryKey(qTable);
        Assert.notNull(primaryKey);
        RawCacheBuilder cacheQuery = (RawCacheBuilder) queryContext.getOrmQueryFactory()
                .modelCacheBuilder(qTable, modelClass);

        Map<Serializable, MapModel> oldModelMaps = cacheQuery.findAllOfMapByIds(ids);
        Map<Serializable, DiffColumnModel> diffColumnModelMap = foundDiff(oldModelMaps);
        Assert.isTrue(Objects.equals(oldModelMaps.size(), ids.size()));
        Connection connection = DataSourceUtils.getConnection(queryContext.getDataSource());
        try {
            SQLDeleteClause sqlDeleteClause = new SQLDeleteClause(connection,
                    queryContext.getDialect(), qTable);
            sqlDeleteClause = sqlDeleteClause.where(primaryKey.in(ids));
            long execute = sqlDeleteClause.execute();
            afterModify.afterDelete(diffColumnModelMap);
            DBHelper.invokeExceptionIfNoAction(execute, ids.size());
            return execute;
        } finally {
            DataSourceUtils.releaseConnection(connection, queryContext.getDataSource());
        }
    }

    protected Map<Serializable, DiffColumnModel> foundDiff(Map<Serializable,
            MapModel> oldModels) {
        Map<Serializable, DiffColumnModel> diffMap = new HashMap<>();
        if (ModelHelper.hasPrimaryKey(qTable)) {
            for (Map.Entry<Serializable, MapModel> entry : oldModels.entrySet()) {
                MapModel mapModelOld = entry.getValue();
                DiffColumnModel diffColumnModel = DiffColumnModelFactory
                        .buildDiffColumnModel(mapModelOld, null);
                diffMap.put(entry.getKey(), diffColumnModel);

            }
        }
        return diffMap;

    }

}
