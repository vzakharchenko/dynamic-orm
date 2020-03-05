package com.github.vzakharchenko.dynamic.orm.core;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicTableModel;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContext;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 13.04.15
 * Time: 18:21
 */
public interface RawModel extends Serializable {
    <TYPE> TYPE getValueByColumnName(String columnName, Class<TYPE> columnType);

    <TYPE> TYPE getColumnValue(Path<TYPE> column);

    <TYPE> TYPE getAliasValue(Expression<TYPE> column);

    Map<Expression<?>, Object> getRawMap();

    boolean isEmpty();

    boolean isNotEmpty();

    Object getValueByPosition(int pos);

    DynamicTableModel getDynamicModel(QDynamicTable qDynamicTable);

    <T extends DMLModel> T getModel(RelationalPath<?> qModel, Class<T> modelClass);

    <T extends DMLModel> T getModel(Class<T> modelClass, QueryContext queryContext);

    <T extends DMLModel> T getModel(Class<T> modelClass, OrmQueryFactory ormQueryFactory);

    List<Expression<?>> getColumns();
}
