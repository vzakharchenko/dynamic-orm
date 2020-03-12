package com.github.vzakharchenko.dynamic.orm.core.raw;

import com.github.vzakharchenko.dynamic.orm.core.RawModel;
import com.querydsl.core.types.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RawColumnFactory {

    private static final List<RawColumn> RAW_COLUMNS = new ArrayList<>();

    static {
        RAW_COLUMNS.add(new PathRawColumn());
        RAW_COLUMNS.add(new OperationRawColumn());
        RAW_COLUMNS.add(new TemplateRawColumn());
    }

    private RawColumnFactory() {
    }

    private static RawColumn getValidRawModel(Expression expression) {
        return RAW_COLUMNS.stream().filter(rawColumn ->
                rawColumn.isValid(expression))
                .findFirst().orElseThrow(() ->
                        new IllegalStateException(
                                "Column " + expression.getType() + "does not supported"));
    }

    public static Object getValue(RawModel rawModel, String columnName) {
        Map<Expression<?>, Object> rawMap = rawModel.getRawMap();
        return rawMap.entrySet().stream().filter(entry -> {
            Expression<?> expression = entry.getKey();
            RawColumn rawColumn = getValidRawModel(expression);
            return rawColumn.checkColumn(expression, columnName);
        }).map(Map.Entry::getValue).findFirst().orElse(null);
    }
}
