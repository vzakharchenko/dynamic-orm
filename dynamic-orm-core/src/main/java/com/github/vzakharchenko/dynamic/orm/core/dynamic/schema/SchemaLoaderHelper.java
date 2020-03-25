package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTableFactory;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models.*;
import com.github.vzakharchenko.dynamic.orm.core.helper.ClassHelper;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SchemaLoaderHelper {
    private SchemaLoaderHelper() {
    }

    private static void loadSequences(QDynamicTableFactory dynamicTableFactory,
                                      List<SchemaSequence> sequences) {
        sequences.forEach(schemaSequence -> dynamicTableFactory
                .createSequence(schemaSequence.getName())
                .initialValue(ClassHelper.transform(schemaSequence.getInitial()))
                .increment(ClassHelper.transform(schemaSequence.getIncrement()))
                .max(ClassHelper.transform(schemaSequence.getMax()))
                .min(ClassHelper.transform(schemaSequence.getMin()))
                .addSequence());
    }


    private static List<Expression<?>> getFromViewColumn(List<SchemaColumn> columns) {
        return columns.stream().map((Function<SchemaColumn, Expression<?>>) schemaColumn -> {
            String className = schemaColumn.getaType();
            if (!Objects.equals(className, "ARRAY")) {
                return ExpressionUtils.path(ClassHelper.getNumberClass(className),
                        schemaColumn.getName());
            } else {
                return ExpressionUtils.path(byte[].class, schemaColumn.getName());
            }
        }).collect(Collectors.toList());
    }

    private static void loadView(QDynamicTableFactory dynamicTableFactory,
                                 SchemaView schemaView) {
        List<SchemaColumn> columns = schemaView.getColumns();
        dynamicTableFactory.createView(schemaView.getName()).resultSet(schemaView.getSql(),
                getFromViewColumn(columns)).addView();
    }

    private static void loadViews(QDynamicTableFactory dynamicTableFactory,
                                  List<SchemaView> views) {
        views.forEach(schemaView -> loadView(dynamicTableFactory, schemaView));
    }

    public static void loadStructure(QDynamicTableFactory dynamicTableFactory,
                                     Schema schema) {
        List<SchemaSequence> sequences = schema.getSequences();
        if (sequences != null) {
            loadSequences(dynamicTableFactory, sequences);
        }
        List<SchemaTable> tables = schema.getTables();
        if (tables != null) {
            SchemaTableLoaderHelper.loadTables(dynamicTableFactory, tables);
        }
        List<SchemaView> views = schema.getViews();
        if (views != null) {
            loadViews(dynamicTableFactory, views);
        }
    }
}
