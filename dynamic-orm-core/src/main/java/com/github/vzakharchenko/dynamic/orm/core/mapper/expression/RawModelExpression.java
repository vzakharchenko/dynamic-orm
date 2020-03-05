package com.github.vzakharchenko.dynamic.orm.core.mapper.expression;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.RelationalPath;
import com.github.vzakharchenko.dynamic.orm.core.RawModel;
import com.github.vzakharchenko.dynamic.orm.core.RawModelImpl;
import com.github.vzakharchenko.dynamic.orm.core.mapper.CommonMappingProjection;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vzakharchenko on 08.12.14.
 */
public class RawModelExpression extends CommonMappingProjection<RawModel> {

    protected final Collection<? extends Expression<?>> columns;

    public RawModelExpression(Collection<? extends Expression<?>> columns) {
        super(RawModel.class, RawModelExpressionHelper.getColumns(columns));
        this.columns = Arrays.asList(RawModelExpressionHelper.getColumns(columns));
    }

    public static RawModelExpression createFromTables(Collection<RelationalPath> qTables) {
        Set<Expression<?>> columns = new HashSet<>();
        for (RelationalPath<?> table : qTables) {
            columns.addAll(table.getColumns());
        }
        return new RawModelExpression(columns);
    }

    @Override
    public RawModel map(Tuple row) {
        return new RawModelImpl(row, columns);
    }
}
