package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public abstract class AbstractUpdateModelBuilder<MODEL extends DMLModel>
        implements UpdateModelBuilder<MODEL> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractUpdateModelBuilder.class);

    protected final RelationalPath<?> qTable;
    protected final QueryContextImpl queryContext;
    protected final Class<MODEL> modelClass;
    protected final List<ModifyItem<MODEL>> setsBatch = new ArrayList<>();
    protected final AfterModify<MODEL> afterModify;
    protected final Path<?> versionColumn;


    public AbstractUpdateModelBuilder(RelationalPath<?> qTable, QueryContextImpl queryContext,
                                      Path<?> versionColumn, Class<MODEL> modelClass) {
        this.qTable = qTable;
        this.queryContext = queryContext;
        this.modelClass = modelClass;
        this.afterModify = new AfterModifyImpl<>(qTable, modelClass, queryContext);
        this.versionColumn = versionColumn;
        addUpdateItem();
    }

    protected final void addUpdateItem() {
        setsBatch.add(createNewUpdateItem());
    }

    private ModifyItem<MODEL> createNewUpdateItem() {
        return new ModifyItem<>(qTable, modelClass);
    }

    protected ModifyItem<MODEL> getCurrentItem() {
        return CollectionUtils.isEmpty(setsBatch) ? setsBatch.get(setsBatch.size() - 1) : null;
    }


    protected Long updateAll(SQLUpdateClause sqlUpdateClause) {
        return sqlUpdateClause.execute();
    }

    protected SQLUpdateClause updateSimpleAll(SQLUpdateClause sqlUpdateClause) {
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

    private Long updateAll(Connection connection) {
        SQLUpdateClause sqlUpdateClause = new SQLUpdateClause(connection,
                queryContext.getDialect(), qTable);
        sqlUpdateClause = updateSimpleAll(sqlUpdateClause);

        showQuery();
        if (sqlUpdateClause.isEmpty()) {
            return 0L;
        }
        afterModify.cleanQueryCache();
        Long rowsAffected = updateAll(sqlUpdateClause);
        checkVersion(rowsAffected, setsBatch.size());
        return rowsAffected;
    }

    protected Long updateAll() {
        Connection connection = DataSourceUtils.getConnection(queryContext.getDataSource());
        try {
            return updateAll(connection);
        } finally {
            DataSourceUtils.releaseConnection(connection, queryContext.getDataSource());
            setsBatch.clear();
        }
    }

    protected void showQuery() {
        if (queryContext.isDebugSql()) {
            LOGGER.info("execute: " + showSql());
        }
    }

    protected void checkVersion(Long rowsAffected, long rowsExpected) {
        if (versionColumn != null) {
            DBHelper.invokeExceptionIfNoAction(rowsAffected, rowsExpected);
        }
    }
}
