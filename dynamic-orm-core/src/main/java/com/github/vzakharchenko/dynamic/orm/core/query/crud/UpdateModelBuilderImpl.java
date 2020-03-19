package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public class UpdateModelBuilderImpl<MODEL extends DMLModel>
        extends CacheUpdateModelBuilder<MODEL> {

    public UpdateModelBuilderImpl(RelationalPath<?> qTable,
                                  QueryContextImpl queryContext,
                                  Path<?> versionColumn, Class<MODEL> modelClass) {
        super(qTable, queryContext, versionColumn, modelClass);
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

    @Override
    public Long update() {
        try {
            if (PrimaryKeyHelper.hasPrimaryKey(qTable)) {
                return updateWithCache();
            }
            return updateAll();
        } catch (Exception e) {
            throw new IllegalStateException("query error: " + showSql(), e);
        }
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
