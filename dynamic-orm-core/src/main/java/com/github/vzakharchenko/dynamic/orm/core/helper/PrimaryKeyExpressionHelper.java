package com.github.vzakharchenko.dynamic.orm.core.helper;

import com.github.vzakharchenko.dynamic.orm.core.predicate.PredicateFactory;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPath;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class PrimaryKeyExpressionHelper {
    private PrimaryKeyExpressionHelper() {
    }

    public static BooleanExpression getPrimaryKeyExpression(RelationalPath qTable,
                                                            Map<Path<?>, Object> setMap) {
        return getPrimaryKeyExpression(PrimaryKeyHelper.getPrimaryKey(qTable), setMap);
    }

    public static BooleanExpression getPrimaryKeyExpression(PrimaryKey<?> primaryKey,
                                                            Map<Path<?>, Object> setMap) {
        List<SimpleExpression> expressions = primaryKey
                .getLocalColumns().stream().map((Function<Path<?>, SimpleExpression>) path -> {
                    Object value = setMap.get(path);
                    if (value == null) {
                        throw new IllegalStateException("Primary key value is null");
                    }
                    SimpleExpression comparableExpressionBase = (SimpleExpression) path;
                    return comparableExpressionBase.eq(value);
                }).collect(Collectors.toList());
        return PredicateFactory.and(expressions);
    }

    public static List<Expression<?>> getPrimaryKeyExpressionColumns(RelationalPath qTable) {
        return PrimaryKeyHelper.getPrimaryKeyColumns(qTable).stream().map((Function<Path<?>, Expression<?>>)
                path -> path).collect(Collectors.toList());
    }
}
