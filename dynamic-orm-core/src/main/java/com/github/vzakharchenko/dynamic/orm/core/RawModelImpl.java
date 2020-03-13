package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.FormatHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.raw.RawColumnHelper;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vzakharchenko on 08.12.14.
 */
public class RawModelImpl implements RawModel {

    protected final Map<Expression<?>, Serializable> rawMap = new LinkedHashMap<>();
    private transient List<Expression<?>> columns;
    private transient List<Object> values;

    public RawModelImpl(Tuple tuple, Collection<? extends Expression<?>> columns) {
        for (Expression column : columns) {
            rawMap.put(column, (Serializable) tuple.get(column));
        }
    }

    // wildcard
    public RawModelImpl(Object[] values, Expression column) {
        assert column.equals(Wildcard.all);
        for (int i = 0; i < values.length; i++) {
            rawMap.put(Expressions.path(Serializable.class,
                    Objects.toString(i)), FormatHelper.transformObjectValue(values[i]));
        }
    }

    @Override
    public <TYPE> TYPE getValueByColumnName(String columnName, Class<TYPE> columnType) {
        return (TYPE) RawColumnHelper.getValue(this, columnName);
    }

    @Override
    public <TYPE> TYPE getColumnValue(Path<TYPE> column) {
        for (Map.Entry<Expression<?>, Serializable> pathObjectEntry : rawMap.entrySet()) {
            Expression expression = pathObjectEntry.getKey();
            Expression searchColumn = ModelHelper.getColumnFromExpression(expression);
            if (Objects.equals(searchColumn, column)) {
                return (TYPE) pathObjectEntry.getValue();
            }
        }
        return null;
    }

    @Override
    public <TYPE> TYPE getAliasValue(Expression<TYPE> column) {
        return (TYPE) rawMap.get(column);
    }


    @Override
    public Map<Expression<?>, Object> getRawMap() {
        return Collections.unmodifiableMap(rawMap);
    }

    public List<Object> getValues() {
        if (values == null) {
            values = new ArrayList<>(rawMap.size());
            values.addAll(rawMap.entrySet().stream()
                    .map(Map.Entry::getValue).collect(Collectors.toList()));
        }
        return values;
    }

    @Override
    public boolean isEmpty() {
        return MapUtils.isEmpty(rawMap);
    }

    @Override
    public boolean isNotEmpty() {
        return MapUtils.isNotEmpty(rawMap);
    }

    @Override
    public Object getValueByPosition(int pos) {
        List<Object> values0 = getValues();
        return values0.get(pos);
    }

    @Override
    public DynamicTableModel getDynamicModel(QDynamicTable qDynamicTable) {
        DynamicTableModel dynamicTableModel = new DynamicTableModel(qDynamicTable);
        for (Path<?> column : qDynamicTable.getColumns()) {
            dynamicTableModel.addColumnValue(column
                    .getMetadata().getName(), getColumnValue(column));
        }
        return dynamicTableModel;
    }

    @Override
    public <T extends DMLModel> T getModel(RelationalPath<?> qModel, Class<T> modelClass) {
        try {
            T model = modelClass.newInstance();
            for (Path<?> column : qModel.getColumns()) {
                ModelHelper.setColumnValue(model, column, getColumnValue(column));
            }
            return model;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Expression<?>> getColumns() {
        if (isNotEmpty() && CollectionUtils.isEmpty(columns)) {
            columns = ImmutableList.copyOf(rawMap.keySet());
        }
        return columns;
    }
}
