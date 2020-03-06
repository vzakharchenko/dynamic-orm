package com.github.vzakharchenko.dynamic.orm.core.helper;


import com.querydsl.core.QueryFlag;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.ProjectableSQLQuery;
import com.querydsl.sql.SQLQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 13.04.15
 * Time: 21:18
 */
public abstract class SQLBuilderHelper {

    public static SubQueryExpression<Tuple> createSubQuery(SQLQuery sqlQuery,
                                                           SimpleExpression... columns) {
        return createSubQuery(sqlQuery, (Expression[]) columns);
    }

    public static <T> SubQueryExpression<T> createSubQuery(SQLQuery sqlQuery,
                                                           Expression... columns) {
        return (SubQueryExpression<T>) sqlQuery.select(columns);
    }

    public static <T> SubQueryExpression<T> createSubQuery(
            SQLQuery sqlQuery,
            List<? extends Expression<?>> columns) {
        return (SubQueryExpression<T>) sqlQuery.select(columns
                .toArray(new Expression[columns.size()]));
    }

    public static <T> SubQueryExpression<T> createSubQuery(
            SQLQuery sqlQuery, Expression<T> column) {
        return sqlQuery.select(column);
    }

    public static void subQueryWrapper(List<SubQueryExpression<?>> subQueries) {
        for (SubQueryExpression<?> subQuery : subQueries) {
            SQLBuilderHelper.subQueryWrapper(subQuery);
        }
    }

    public static void subQueryWrapper(SubQueryExpression subQueryExpression) {
        if (subQueryExpression instanceof ProjectableSQLQuery) {
            ProjectableSQLQuery projectableSQLQuery = (ProjectableSQLQuery) subQueryExpression;
            subQueryWrapper(projectableSQLQuery);
        }
    }

    public static void subQueryWrapper(ProjectableSQLQuery projectableSQLQuery) {
        if (!StringUtils.startsWith(Objects.toString(projectableSQLQuery), "(")) {
            projectableSQLQuery.addFlag(QueryFlag.Position.START, "(");
            projectableSQLQuery.addFlag(QueryFlag.Position.END, ")");
        }
    }
}
