package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.dml.SQLDeleteClause;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;

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
    private final LinkedList<ModifyItem<MODEL>> batch0 = new LinkedList<>();
    private final AfterModify<MODEL> afterModify;
    private final Path<?> versionColumn;

    // CHECKSTYLE:OFF
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
    // CHECKSTYLE:ON

    private void addDeleteItem(MODEL model) {
        ModifyItem<MODEL> newDeleteItem = createNewDeleteItem();
        if (PrimaryKeyHelper.hasPrimaryKey(qTable)) {
            CompositeKey compositeKey = PrimaryKeyHelper.getPrimaryKeyValues(model, qTable);
            newDeleteItem.set(compositeKey.getCompositeMap());
        }
        batch0.add(newDeleteItem);
    }

    private ModifyItem<MODEL> createNewDeleteItem() {
        return new ModifyItem<>(qTable, modelClass);
    }

    private ModifyItem<MODEL> getCurrentItem() {
        return batch0.getLast();
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


    private Long deleteWithPrimaryKey() {
        List<Serializable> pkeys = new ArrayList<>();
        for (ModifyItem<MODEL> modifyItem : batch0) {
            if (!modifyItem.isEmpty()) {
                pkeys.add((Serializable) modifyItem.getPrimaryKeyValue());
            }
        }
        try {
            return queryContext.getOrmQueryFactory()
                    .modify(qTable, modelClass).deleteByIds(pkeys);
        } finally {
            batch0.clear();
        }
    }

    public Long deleteWithOutPrimaryKey() {
        DBHelper.transactionCheck();
        Assert.notEmpty(batch0);
        Connection connection = DataSourceUtils.getConnection(queryContext.getDataSource());
        try {
            SQLDeleteClause sqlDeleteClause = new SQLDeleteClause(connection,
                    queryContext.getDialect(), qTable);
            for (ModifyItem<MODEL> modifyItem : batch0) {
                if (!modifyItem.isEmpty()) {
                    sqlDeleteClause = sqlDeleteClause.where(modifyItem.getWhere()).addBatch();
                }
            }
            long execute = sqlDeleteClause.execute();
            if (versionColumn != null) {
                DBHelper.invokeExceptionIfNoAction(execute, batch0.size());
            }
            afterModify.cleanQueryCache();
            return execute;
        } finally {
            DataSourceUtils.releaseConnection(connection, queryContext.getDataSource());
            batch0.clear();
        }
    }

    @Override
    public Long delete() {
        if (PrimaryKeyHelper.hasPrimaryKey(qTable)) {
            return deleteWithPrimaryKey();
        }
        return deleteWithOutPrimaryKey();
    }
}
