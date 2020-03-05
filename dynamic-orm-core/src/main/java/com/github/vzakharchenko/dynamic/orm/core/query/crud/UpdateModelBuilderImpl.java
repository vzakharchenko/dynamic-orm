package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumn;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModelFactory;
import com.github.vzakharchenko.dynamic.orm.core.cache.MapModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.VersionHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.RawCacheBuilder;

import java.io.Serializable;
import java.sql.Connection;
import java.util.*;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public class UpdateModelBuilderImpl<MODEL extends DMLModel>
        implements UpdateModelBuilder<MODEL> {

    private static Logger logger = LoggerFactory.getLogger(UpdateModelBuilderImpl.class);

    private final RelationalPath<?> qTable;
    private final QueryContextImpl queryContext;
    private final Class<MODEL> modelClass;
    private final LinkedList<ModifyItem<MODEL>> setsBatch = new LinkedList<>();
    private final AfterModify<MODEL> afterModify;
    private final Path<?> versionColumn;


    public UpdateModelBuilderImpl(RelationalPath<?> qTable, QueryContextImpl queryContext,
                                  Path<?> versionColumn, Class<MODEL> modelClass) {
        this.qTable = qTable;
        this.queryContext = queryContext;
        this.modelClass = modelClass;
        this.afterModify = new AfterModifyImpl<>(qTable, modelClass, queryContext);
        this.versionColumn = versionColumn;
        addUpdateItem();
    }

    private void addUpdateItem() {
        setsBatch.add(createNewUpdateItem());
    }

    private ModifyItem<MODEL> createNewUpdateItem() {
        return new ModifyItem<>(qTable, modelClass);
    }

    private ModifyItem<MODEL> getCurrentItem() {
        return setsBatch.getLast();
    }

    @Override
    public <T> UpdateModelBuilder<MODEL> set(Path<T> column, T value) {
        getCurrentItem().set(column, value);
        return this;
    }

    @Override
    public UpdateModelBuilder<MODEL> set(Map<Path<?>, Object> setMap) {
        Assert.notNull(setMap);
        getCurrentItem().set(setMap);
        return this;
    }

    @Override
    public UpdateModelBuilder<MODEL> where(BooleanExpression predicate) {
        getCurrentItem().and(predicate);
        return this;
    }

    @Override
    public UpdateModelBuilder<MODEL> byId() {
        ModifyItem<MODEL> currentItem = getCurrentItem();
        Assert.isTrue(currentItem.byId(), " Primary key is not found " + qTable);
        return this;

    }

    @Override
    public UpdateModelBuilder<MODEL> batch() {
        addUpdateItem();
        return this;
    }

    @Override
    public Long updateModelsById(MODEL... models) {
        return updateModelsById(Arrays.asList(models));
    }

    @Override
    public Long updateModelsById(List<MODEL> models) {
        if (CollectionUtils.isEmpty(models)) {
            return 0L;
        }

        UpdateBuilder<MODEL> updateBuilder = null;
        for (MODEL model : models) {
            if (updateBuilder == null) {
                updateBuilder = updateModel(model).byId();
            } else {
                updateBuilder = updateBuilder.addNextBatch(model).byId();
            }
        }
        if (updateBuilder == null) {
            return 0L;
        }
        return updateBuilder.update();
    }

    @Override
    public UpdateBuilder<MODEL> updateModel(MODEL model) {
        return new UpdateBuilderImpl<>(model, qTable, this);
    }


    private Long updateAll() {
        Connection connection = DataSourceUtils.getConnection(queryContext.getDataSource());
        try {
            SQLUpdateClause sqlUpdateClause = new SQLUpdateClause(connection,
                    queryContext.getDialect(), qTable);
            sqlUpdateClause = updateSimpleAll(sqlUpdateClause);

            if (queryContext.isDebugSql()) {
                logger.info("execute: " + ToStringBuilder.reflectionToString(showSql(),
                        ToStringStyle.JSON_STYLE));
            }
            if (sqlUpdateClause.isEmpty()) {
                return 0L;
            }
            afterModify.cleanQueryCache();
            Long aLong = updateAll(sqlUpdateClause);
            if (versionColumn != null) {
                DBHelper.invokeExceptionIfNoAction(aLong, setsBatch.size());
            }
            return aLong;
        } finally {
            DataSourceUtils.releaseConnection(connection, queryContext.getDataSource());
            setsBatch.clear();
        }
    }

    private Long updateAll(SQLUpdateClause sqlUpdateClause) {
        return sqlUpdateClause.execute();
    }

    private SQLUpdateClause updateSimpleAll(SQLUpdateClause sqlUpdateClause) {
        SQLUpdateClause sqlUpdateClause0 = sqlUpdateClause;
        for (ModifyItem<MODEL> updateBatch : setsBatch) {
            if (!updateBatch.isEmpty()) {
                for (Map.Entry<Path<?>, Object> entry : updateBatch.getSetMap().entrySet()) {
                    sqlUpdateClause0 = sqlUpdateClause0.set((Path<Object>) entry.getKey(),
                            entry.getValue());
                }
                if (updateBatch.getWhere() != null) {
                    sqlUpdateClause0 = sqlUpdateClause0.where(updateBatch.getWhere());
                }
                sqlUpdateClause0 = sqlUpdateClause0.addBatch();
            }
        }

        return sqlUpdateClause0;
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

    @Override
    public Long update() {
        try {
            if (ModelHelper.hasPrimaryKey(qTable)) {
                return updateWithCache();
            }
            return updateAll();
        } catch (Exception e) {
            throw new IllegalStateException("query error: " + showSql(), e);
        }


    }

    private BooleanExpression getVersionPredicate(ModifyItem<MODEL> modifyItem) {
        MODEL model = queryContext.getOrmQueryFactory()
                .modelCacheBuilder(qTable, modelClass)
                .findOneById((Serializable) modifyItem.getPrimaryKeyValue());

        Serializable currentVersion = VersionHelper
                .getCurrentVersion((Path) versionColumn, model);
        Serializable newVersion = VersionHelper.incrementVersion(versionColumn, model);
        modifyItem.set((Path) versionColumn, newVersion);
        return ((SimpleExpression<Serializable>) versionColumn).eq(currentVersion);
    }

    private Long updateWithCache() {
        List<Serializable> pkeys = new ArrayList<>();
        for (ModifyItem<MODEL> modifyItem : setsBatch) {
            if (!modifyItem.isEmpty()) {
                Object pkValue = modifyItem.getPrimaryKeyValue();
                Assert.notNull(pkValue, "primary Key is null");
                pkeys.add((Serializable) pkValue);
                if (versionColumn != null) {
                    BooleanExpression versionPredicate = getVersionPredicate(modifyItem);
                    modifyItem.and(versionPredicate);
                }
            }
        }

        Map<Serializable, MapModel> oldModels = ((RawCacheBuilder)
                queryContext
                        .getOrmQueryFactory().modelCacheBuilder(qTable, modelClass))
                .findAllOfMapByIds(pkeys);
        Assert.isTrue(Objects.equals(oldModels.size(), pkeys.size()));
        Map<Serializable, DiffColumnModel> diffMap = foundDiff(oldModels);

        for (ModifyItem<MODEL> updateBatch : setsBatch) {
            if (!updateBatch.isEmpty()) {
                Serializable primaryKeyValue = (Serializable) updateBatch.getPrimaryKeyValue();
                DiffColumnModel diffColumnModel = diffMap.get(primaryKeyValue);
                Assert.notNull(diffColumnModel, " diff is not found " + updateBatch);
                updateBatch.setDiffColumnModel(diffColumnModel);
            }

        }


        Connection connection = DataSourceUtils.getConnection(queryContext.getDataSource());
        try {
            SQLUpdateClause sqlUpdateClause0 = new SQLUpdateClause(connection,
                    queryContext.getDialect(), qTable);

            if (queryContext.isDebugSql()) {
                logger.info("execute: " + showSql());
            }
            sqlUpdateClause0 = updateCacheAll(sqlUpdateClause0);

            if (sqlUpdateClause0.isEmpty()) {
                return 0L;
            }

            Long rowsAffected = updateAll(sqlUpdateClause0);
            if (versionColumn != null) {
                DBHelper.invokeExceptionIfNoAction(rowsAffected, pkeys.size());
            }
            afterModify.afterUpdate(diffMap);
            return rowsAffected;

        } finally {
            DataSourceUtils.releaseConnection(connection, queryContext.getDataSource());
            setsBatch.clear();
        }
    }

    protected Map<Serializable, DiffColumnModel> foundDiff(Map<Serializable,
            MapModel> oldModels) {
        Map<Serializable, DiffColumnModel> diffMap = new HashMap<>();
        setsBatch.stream().filter(modifyItem -> !modifyItem.isEmpty()).forEach(modifyItem -> {
            MapModel mapModelOld = oldModels.get(modifyItem.getPrimaryKeyValue());
            DiffColumnModel diffColumnModel = DiffColumnModelFactory
                    .buildDiffColumnModel(mapModelOld, modifyItem.getMapModel());
            diffMap.put((Serializable) modifyItem.getPrimaryKeyValue(), diffColumnModel);
        });
        return diffMap;

    }


    @Override
    public String showSql() {
        StringBuilder sqls = new StringBuilder("\n");
        SQLUpdateClause sqlUpdateClause0 = new SQLUpdateClause(null,
                queryContext.getDialect(), qTable);
        sqlUpdateClause0 = updateSimpleAll(sqlUpdateClause0);
        sqlUpdateClause0.setUseLiterals(true);
        for (SQLBindings sqlBindings : sqlUpdateClause0.getSQL()) {
            String sql = sqlBindings.getSQL();
            sqls.append(sql).append('\n');
        }
        return sqls.toString();
    }


}
