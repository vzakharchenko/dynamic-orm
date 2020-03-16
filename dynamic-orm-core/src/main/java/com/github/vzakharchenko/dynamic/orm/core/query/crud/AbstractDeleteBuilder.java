package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModelFactory;
import com.github.vzakharchenko.dynamic.orm.core.cache.MapModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
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
import java.util.stream.Collectors;

public abstract class AbstractDeleteBuilder<MODEL extends DMLModel> implements DeleteBuilder<MODEL> {

    protected final QueryContextImpl queryContext;

    protected final Class<MODEL> modelClass;

    protected final RelationalPath<?> qTable;
    protected Path<?> versionColumn;
    protected final AfterModify<MODEL> afterModify;

    protected AbstractDeleteBuilder(Class<MODEL> modelClass, RelationalPath<?> qTable,
                                    QueryContextImpl queryContext) {
        this.modelClass = modelClass;
        this.qTable = qTable;
        this.queryContext = queryContext;
        this.afterModify = new AfterModifyImpl<>(qTable, modelClass, queryContext);
    }



    public Long deleteByIds0(List<Serializable> ids) {
        DBHelper.transactionCheck();
        if (PrimaryKeyHelper.hasCompositePrimaryKey(qTable)) {
            List<CompositeKey> compositeKeys = ids.stream()
                    .map(serializable -> PrimaryKeyHelper
                            .getCompositeKey(serializable, qTable))
                    .collect(Collectors.toList());
            return deleteByCompositeIds(compositeKeys);
        } else {
            return deleteByIds(ids, (ComparableExpressionBase) PrimaryKeyHelper
                    .getPrimaryKeyColumns(qTable).get(0));
        }
    }

    public Long deleteByCompositeIds(List<CompositeKey> compositeKeys) {
        long value = 0;
        for (CompositeKey compositeKey : compositeKeys) {
            value += deleteByCompositeId(compositeKey);
        }
        return Long.valueOf(value);
    }

    public Long deleteByCompositeId(CompositeKey compositeKey) {
        Connection connection = DataSourceUtils.getConnection(queryContext.getDataSource());
        try {
            RawCacheBuilder cacheQuery = (RawCacheBuilder) queryContext.getOrmQueryFactory()
                    .modelCacheBuilder(qTable, modelClass);
            Map<CompositeKey, MapModel> oldModelMaps = cacheQuery.findAllOfMapByIds(
                    Collections.singletonList(compositeKey));
            Map<CompositeKey, DiffColumnModel> diffColumnModelMap = foundDiff(oldModelMaps);
            Assert.isTrue(Objects.equals(oldModelMaps.size(), 1));
            SQLDeleteClause sqlDeleteClause = new SQLDeleteClause(connection,
                    queryContext.getDialect(), qTable);
            sqlDeleteClause = sqlDeleteClause.where(compositeKey.getWherePart());
            long execute = sqlDeleteClause.execute();
            afterModify.afterDelete(diffColumnModelMap);
            DBHelper.invokeExceptionIfNoAction(execute, 1);
            return execute;
        } finally {
            DataSourceUtils.releaseConnection(connection, queryContext.getDataSource());
        }
    }

    private Long deleteByIds(List<Serializable> ids, ComparableExpressionBase primaryKey) {
        return deleteByIds0(PrimaryKeyHelper.getCompositeKeys(ids, qTable), primaryKey);
    }

    private Long deleteByIds0(List<CompositeKey> ids, ComparableExpressionBase primaryKey) {
        DBHelper.transactionCheck();
        Assert.notNull(primaryKey);
        RawCacheBuilder cacheQuery = (RawCacheBuilder) queryContext.getOrmQueryFactory()
                .modelCacheBuilder(qTable, modelClass);

        Map<CompositeKey, MapModel> oldModelMaps = cacheQuery.findAllOfMapByIds(
                PrimaryKeyHelper.getCompositeKeys(ids, qTable));
        Map<CompositeKey, DiffColumnModel> diffColumnModelMap = foundDiff(oldModelMaps);
        Assert.isTrue(Objects.equals(oldModelMaps.size(), ids.size()));
        Connection connection = DataSourceUtils.getConnection(queryContext.getDataSource());
        try {
            SQLDeleteClause sqlDeleteClause = new SQLDeleteClause(connection,
                    queryContext.getDialect(), qTable);
            sqlDeleteClause = sqlDeleteClause.where(primaryKey.in(ids.stream()
                    .map(compositeKey -> compositeKey.getColumn((Path) primaryKey))
                    .collect(Collectors.toList())));
            long execute = sqlDeleteClause.execute();
            afterModify.afterDelete(diffColumnModelMap);
            DBHelper.invokeExceptionIfNoAction(execute, ids.size());
            return execute;
        } finally {
            DataSourceUtils.releaseConnection(connection, queryContext.getDataSource());
        }
    }

    protected Map<CompositeKey, DiffColumnModel> foundDiff(Map<CompositeKey,
            MapModel> oldModels) {
        Map<CompositeKey, DiffColumnModel> diffMap = new HashMap<>();
        if (PrimaryKeyHelper.hasPrimaryKey(qTable)) {
            for (Map.Entry<CompositeKey, MapModel> entry : oldModels.entrySet()) {
                MapModel mapModelOld = entry.getValue();
                DiffColumnModel diffColumnModel = DiffColumnModelFactory
                        .buildDiffColumnModel(mapModelOld, null);
                diffMap.put(entry.getKey(), diffColumnModel);

            }
        }
        return diffMap;

    }
}
