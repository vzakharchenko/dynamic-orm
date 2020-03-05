package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.dml.SQLDeleteClause;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public class DeleteModelBuilderImpl<MODEL extends DMLModel>
        implements DeleteModelBuilder<MODEL> {
    private final QueryContextImpl queryContext;

    private final Class<MODEL> modelClass;

    private final RelationalPath<?> qTable;
    private final LinkedList<ModifyItem<MODEL>> batch = new LinkedList<>();
    private final AfterModify<MODEL> afterModify;
    private final Path<?> versionColumn;

    protected DeleteModelBuilderImpl(RelationalPath<?> qTable, Class<MODEL> modelClass,
                                     MODEL model, QueryContextImpl queryContext,
                                     Path<?> versionColumn) {
        this.qTable = qTable;
        this.queryContext = queryContext;
        this.modelClass = modelClass;
        this.afterModify = new AfterModifyImpl<>(qTable, modelClass, queryContext);
        this.versionColumn = versionColumn;
        addDeleteItem(model);

    }

    private void addDeleteItem(MODEL model) {
        ModifyItem<MODEL> newDeleteItem = createNewDeleteItem();
        newDeleteItem.set(ModelHelper.getPrimaryKeyColumn(qTable),
                ModelHelper.getPrimaryKeyValue(model, qTable, Object.class));
        batch.add(newDeleteItem);
    }

    private ModifyItem<MODEL> createNewDeleteItem() {
        return new ModifyItem<>(qTable, modelClass);
    }

    private ModifyItem<MODEL> getCurrentItem() {
        return batch.getLast();
    }

    private void whereAnd(BooleanExpression and) {
        getCurrentItem().and(and);
    }

    @Override
    public DeleteModelBuilder<MODEL> byId() {
        ModifyItem<MODEL> currentItem = getCurrentItem();
        Assert.isTrue(currentItem.byId(), " Primary key is not found " + qTable);
        return this;
    }

    @Override
    public DeleteModelBuilder<MODEL> where(BooleanExpression predicate) {
        whereAnd(predicate);
        return this;
    }

    @Override
    public DeleteModelBuilder<MODEL> batch(MODEL model) {
        addDeleteItem(model);
        return this;
    }


    @Override
    public Long delete() {
        if (ModelHelper.hasPrimaryKey(qTable)) {
            List<Serializable> pkeys = new ArrayList<>();
            for (ModifyItem<MODEL> modifyItem : batch) {
                if (!modifyItem.isEmpty()) {
                    pkeys.add((Serializable) modifyItem.getPrimaryKeyValue());
                }
            }
            try {
                return queryContext.getOrmQueryFactory()
                        .modify(qTable, modelClass).deleteByIds(pkeys);
            } finally {
                batch.clear();
            }
        }
        DBHelper.transactionCheck();
        Assert.notEmpty(batch);
        Connection connection = DataSourceUtils.getConnection(queryContext.getDataSource());
        try {
            SQLDeleteClause sqlDeleteClause = new SQLDeleteClause(connection,
                    queryContext.getDialect(), qTable);
            for (ModifyItem<MODEL> modifyItem : batch) {
                if (!modifyItem.isEmpty()) {
                    sqlDeleteClause = sqlDeleteClause.where(modifyItem.getWhere()).addBatch();
                }
            }
            long execute = sqlDeleteClause.execute();
            if (versionColumn != null) {
                DBHelper.invokeExceptionIfNoAction(execute, batch.size());
            }
            afterModify.cleanQueryCache();
            return execute;
        } finally {
            DataSourceUtils.releaseConnection(connection, queryContext.getDataSource());
            batch.clear();
        }
    }
}
