package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumn;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModelFactory;
import com.github.vzakharchenko.dynamic.orm.core.cache.MapModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.VersionHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.RawCacheBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.apache.commons.collections4.MapUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public abstract class CacheUpdateModelBuilder<MODEL extends DMLModel>
        extends AbstractUpdateModelBuilder<MODEL> {

    public CacheUpdateModelBuilder(RelationalPath<?> qTable,
                                   QueryContextImpl queryContext,
                                   Path<?> versionColumn,
                                   Class<MODEL> modelClass) {
        super(qTable, queryContext, versionColumn, modelClass);
    }

    private BooleanExpression getVersionPredicate(ModifyItem<MODEL> modifyItem) {
        MODEL model = queryContext.getOrmQueryFactory()
                .modelCacheBuilder(qTable, modelClass)
                .findOneById(modifyItem.getPrimaryKeyValue());

        Serializable currentVersion = VersionHelper
                .getCurrentVersion((Path) versionColumn, model);
        Serializable newVersion = VersionHelper.incrementVersion(versionColumn, model);
        modifyItem.set((Path) versionColumn, newVersion);
        return ((SimpleExpression<Serializable>) versionColumn).eq(currentVersion);
    }

    protected void versionConnect(ModifyItem<MODEL> modifyItem) {
        if (versionColumn != null) {
            BooleanExpression versionPredicate = getVersionPredicate(modifyItem);
            modifyItem.and(versionPredicate);
        }
    }


    private List<CompositeKey> getPrivateKeys() {
        return setsBatch.stream().filter(modifyItem -> !modifyItem.isEmpty())
                .map(modifyItem -> {
                    CompositeKey pkValue = modifyItem.getPrimaryKeyValue();
                    Assert.notNull(pkValue, "primary Key is null");
                    versionConnect(modifyItem);
                    return pkValue;
                }).collect(Collectors.toList());
    }

    private Map<CompositeKey, DiffColumnModel> getDiffWOldModels(List<CompositeKey> pkeys) {
        Map<CompositeKey, MapModel> oldModels = ((RawCacheBuilder)
                queryContext
                        .getOrmQueryFactory().modelCacheBuilder(qTable, modelClass))
                .findAllOfMapByIds(PrimaryKeyHelper.getCompositeKeys(pkeys, qTable));
        Assert.isTrue(Objects.equals(oldModels.size(), pkeys.size()));
        return foundDiff(oldModels);
    }

    private void updateDiff(Map<CompositeKey, DiffColumnModel> diffMap) {
        setsBatch.stream().filter(updateBatch -> !updateBatch.isEmpty())
                .forEach(updateBatch -> {
                    Serializable primaryKeyValue = updateBatch.getPrimaryKeyValue();
                    DiffColumnModel diffColumnModel = diffMap.get(primaryKeyValue);
                    Assert.notNull(diffColumnModel, " diff is not found " + updateBatch);
                    updateBatch.setDiffColumnModel(diffColumnModel);
                });
    }

    protected Long updateWithCache(Connection connection,
                                   List<CompositeKey> pkeys,
                                   Map<CompositeKey, DiffColumnModel> diffMap) {
        SQLUpdateClause sqlUpdateClause0 = new SQLUpdateClause(connection,
                queryContext.getDialect(), qTable);
        showQuery();
        sqlUpdateClause0 = updateCacheAll(sqlUpdateClause0);

        if (sqlUpdateClause0.isEmpty()) {
            return 0L;
        }

        Long rowsAffected = updateAll(sqlUpdateClause0);
        checkVersion(rowsAffected, pkeys.size());
        afterModify.afterUpdate(diffMap);
        return rowsAffected;
    }

    protected Long updateWithCache() {
        List<CompositeKey> pkeys = getPrivateKeys();
        Map<CompositeKey, DiffColumnModel> diffMap = getDiffWOldModels(pkeys);
        updateDiff(diffMap);

        Connection connection = DataSourceUtils.getConnection(queryContext.getDataSource());
        try {
            return updateWithCache(connection, pkeys, diffMap);
        } finally {
            DataSourceUtils.releaseConnection(connection, queryContext.getDataSource());
            setsBatch.clear();
        }
    }

    protected Map<CompositeKey, DiffColumnModel> foundDiff(Map<CompositeKey,
            MapModel> oldModels) {
        Map<CompositeKey, DiffColumnModel> diffMap = new HashMap<>();
        setsBatch.stream().filter(modifyItem -> !modifyItem.isEmpty()).forEach(modifyItem -> {
            MapModel mapModelOld = oldModels.get(modifyItem.getPrimaryKeyValue());
            DiffColumnModel diffColumnModel = DiffColumnModelFactory
                    .buildDiffColumnModel(mapModelOld, modifyItem.getMapModel());
            diffMap.put(modifyItem.getPrimaryKeyValue(), diffColumnModel);
        });
        return diffMap;

    }

    private SQLUpdateClause updateCacheAll(SQLUpdateClause sqlUpdateClause) {
        SQLUpdateClause sqlUpdateClause0 = sqlUpdateClause;
        for (ModifyItem<MODEL> modifyItem : setsBatch) {
            if (!modifyItem.isEmpty()) {
                BooleanExpression where = modifyItem.getWhere();
                Map<Path<?>, DiffColumn<?>> onlyChangedColumns = modifyItem
                        .getDiffColumnModel().getOnlyChangedColumns();
                if (MapUtils.isNotEmpty(onlyChangedColumns)) {
                    for (Map.Entry<Path<?>, DiffColumn<?>> entry : onlyChangedColumns
                            .entrySet()) {
                        sqlUpdateClause0 = sqlUpdateClause0.set((Path<Object>) entry
                                .getKey(), entry.getValue().getNewValue());
                    }
                    if (where != null) {
                        sqlUpdateClause0 = sqlUpdateClause0.where(where);
                    }
                    sqlUpdateClause0 = sqlUpdateClause0.addBatch();
                }
            }
        }
        return sqlUpdateClause0;
    }

}
