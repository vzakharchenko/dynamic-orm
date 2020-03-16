package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModelFactory;
import com.github.vzakharchenko.dynamic.orm.core.cache.MapModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.MapModelFactory;
import com.github.vzakharchenko.dynamic.orm.core.helper.*;
import com.github.vzakharchenko.dynamic.orm.core.mapper.AbstractMappingProjection;
import com.github.vzakharchenko.dynamic.orm.core.mapper.TableMappingProjectionFactory;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.dml.SQLInsertClause;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.sql.Connection;
import java.util.*;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public class InsertBuilderImpl<MODEL extends DMLModel> implements InsertBuilder<MODEL> {

    private final QueryContextImpl queryContext;

    private final Class<MODEL> modelClass;

    private final RelationalPath<?> qTable;
    private final AfterModify<MODEL> afterModify;
    private final PKGenerator<?> pkGenerator;
    private final Path<?> versionColumn;
    private final SoftDelete<?> softDelete;
    private AbstractMappingProjection<MODEL> mappingProjection;

    // CHECKSTYLE:OFF
    protected InsertBuilderImpl(Class<MODEL> modelClass, RelationalPath<?> qTable,
                                QueryContextImpl queryContext, AfterModify<MODEL> afterModify,
                                Path<?> versionColumn, PKGenerator<?> pkGenerator) {
        this.modelClass = modelClass;
        this.qTable = qTable;
        this.queryContext = queryContext;
        this.afterModify = afterModify;
        this.pkGenerator = pkGenerator;
        this.versionColumn = versionColumn;
        this.softDelete = queryContext.getSoftDeleteColumn(qTable, modelClass);
    }
    // CHECKSTYLE:ON

    @SafeVarargs
    @Override
    public final Long insert(MODEL... models) {
        return insert(Arrays.asList(models));
    }

    // CHECKSTYLE:OFF
    @Override
    public Long insert(List<MODEL> models) {
        DBHelper.transactionCheck();
        Assert.notEmpty(models);
        if (PrimaryKeyHelper.hasPrimaryKey(qTable) &&
                !PrimaryKeyHelper.hasCompositePrimaryKey(qTable)) {
            setNextPrimaryKey(models);
        }
        for (MODEL model : models) {
            if (PrimaryKeyHelper.hasPrimaryKey(qTable)) {
                if (PrimaryKeyHelper.isPrimaryKeyValueNull(qTable, model)) {
                    throw new IllegalStateException("model for " + qTable.getTableName() +
                            " has empty Primary Key : " + model);
                }
                CacheHelper.checkModelIsDeleted(queryContext, model, qTable);
            }
            if (versionColumn != null) {
                Serializable currentVersion = VersionHelper.getCurrentVersion(
                        versionColumn, model);
                if (currentVersion == null) {
                    VersionHelper.setInitialVersion((Path) versionColumn, model);
                }
            }

            if (softDelete != null
                    && softDelete.getDefaultValue() != null
                    && ModelHelper.getValueFromModelByColumn(model,
                    softDelete.getColumn()) == null) {
                ModelHelper.setColumnValue(model, softDelete.getColumn(),
                        softDelete.getDefaultValue());
            }
        }

        Connection connection = DataSourceUtils.getConnection(queryContext.getDataSource());

        SQLInsertClause sqlInsertClause = new SQLInsertClause(connection,
                queryContext.getDialect(), qTable);
        try {
            Map<CompositeKey, DiffColumnModel> diffColumnModelMap = createDiff(models);
            for (MODEL model : models) {
                AbstractMappingProjection<MODEL> mappingProjection0 = getMappingProjection();
                sqlInsertClause.populate(model, mappingProjection0).addBatch();
            }
            long mCount = sqlInsertClause.execute();
            if (versionColumn != null) {
                DBHelper.invokeExceptionIfNoAction(mCount, models.size());
            }
            afterModify.afterInsert(diffColumnModelMap);
            return mCount;
        } finally {
            DataSourceUtils.releaseConnection(connection, queryContext.getDataSource());
        }

    }

    // CHECKSTYLE:ON
    private AbstractMappingProjection<MODEL> getMappingProjection() {
        if (mappingProjection == null) {
            mappingProjection = TableMappingProjectionFactory.buildMapper(qTable, modelClass);
        }
        return mappingProjection;
    }

    protected void setNextPrimaryKey(Collection<MODEL> batchModels) {
        PKGenerator<?> pkGenerator0 = this.pkGenerator;
        for (MODEL batchModel : batchModels) {
            CompositeKey compositeKey = PrimaryKeyHelper
                    .getOnePrimaryKeyValues(batchModel, qTable);
            if (pkGenerator0 == null) {
                pkGenerator0 = ModelHelper.getPrimaryKeyGeneratorFromModel(batchModel);
            }
            if (pkGenerator0 != null && compositeKey == null) {
                pkGenerator0.generate(queryContext.getOrmQueryFactory(), qTable, batchModel);
            }
        }
    }

    protected Map<CompositeKey, DiffColumnModel> createDiff(List<MODEL> models) {
        Map<CompositeKey, DiffColumnModel> diffMap = new HashMap<>();
        if (PrimaryKeyHelper.hasPrimaryKey(qTable)) {
            for (MODEL model : models) {
                MapModel mapModelNew = MapModelFactory.buildMapModel(qTable, model);
                DiffColumnModel diffColumnModel = DiffColumnModelFactory
                        .buildDiffColumnModel(null, mapModelNew);
                diffMap.put(PrimaryKeyHelper.getOnePrimaryKeyValues(model, qTable),
                        diffColumnModel);

            }
        }
        return diffMap;

    }
}
