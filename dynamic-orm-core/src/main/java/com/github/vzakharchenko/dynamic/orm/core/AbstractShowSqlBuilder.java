package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.mapper.TableMappingProjectionFactory;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDelete;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;
import com.querydsl.sql.SQLQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 *
 */
public abstract class AbstractShowSqlBuilder
        extends AbstractUnionSelectBuilder implements ShowSqlBuilder {

    public AbstractShowSqlBuilder(QueryContextImpl queryContext) {
        super(queryContext);
    }


    protected SQLCommonQuery<?> validateQuery(SQLCommonQuery<?> sqlQuery, RelationalPath<?> qTable,
                                              Class<? extends DMLModel> modelClass) {
        SQLCommonQuery<?> sqlQuery0 = sqlQuery;
        if (!searchQModel(sqlQuery0, qTable)) {
            if (CollectionUtils.isEmpty(DBHelper.castProjectionQueryToSqlQuery(sqlQuery0)
                    .getMetadata().getJoins())) {
                SoftDelete<?> softDeleteColumn = queryContext.getSoftDeleteColumn(qTable,
                        modelClass);
                sqlQuery0 = sqlQuery0.from(qTable);
                return softDeleteColumn != null ? sqlQuery0.where(softDeleteColumn
                        .getActiveExpression()) : sqlQuery0;
            } else {
                throw new IllegalStateException(qTable + " is not found in projection");
            }
        }
        return sqlQuery0;
    }

    private boolean searchQModel(SQLCommonQuery<?> sqlQuery, RelationalPath<?> qTable) {
        List<JoinExpression> joinExpressions = DBHelper.castProjectionQueryToSqlQuery(sqlQuery)
                .getMetadata().getJoins();
        for (JoinExpression joinExpression : joinExpressions) {
            if (joinExpression.getTarget() instanceof RelationalPath) {
                RelationalPath<?> relationalPathBase = (RelationalPath) joinExpression.getTarget();
                if (StringUtils.equals(relationalPathBase.getTableName(), qTable.getTableName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public <MODEL extends DMLModel> String showSql(SQLCommonQuery<?> sqlQuery,
                                                   RelationalPath<?> qTable,
                                                   Class<MODEL> modelClass) {
        queryContext.validateModel(qTable, modelClass);
        return showSql(validateQuery(DBHelper.castProjectionQueryToSqlQuery(sqlQuery).clone(),
                qTable, modelClass), TableMappingProjectionFactory.buildMapper(qTable, modelClass));
    }

    @Override
    public <MODEL extends DMLModel> String showSql(SQLCommonQuery<?> sqlQuery,
                                                   Class<MODEL> modelClass) {
        RelationalPath<?> qTableFromModel = queryContext.getQModel(modelClass);
        return showSql(sqlQuery, qTableFromModel, modelClass);
    }

    @Override
    public String showSql(SQLCommonQuery<?> sqlQuery, Expression<?>... expressions) {
        SQLQuery<?> clone = DBHelper.castProjectionQueryToSqlQuery(sqlQuery).clone();
        clone.setUseLiterals(true);
        return clone.select(expressions).getSQL().getSQL();
    }


    protected String showListSql(SQLCommonQuery<?> sqlQuery, List<? extends Path<?>> columns) {
        return showSql(sqlQuery, columns.toArray(new Expression[0]));
    }
}
