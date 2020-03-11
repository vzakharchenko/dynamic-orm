package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.SQLCommonQuery;

import java.util.Arrays;
import java.util.List;

public class QViewBuilderImpl implements QViewBuilder {
    private final QDynamicBuilderContext dynamicBuilderContext;

    private final ViewModel viewModel;

    public QViewBuilderImpl(QDynamicBuilderContext dynamicBuilderContext, String viewName) {
        this.dynamicBuilderContext = dynamicBuilderContext;
        this.viewModel = new ViewModel(viewName);
    }


    @Override
    public QViewBuilder resultSet(SQLCommonQuery<?> query, Expression<?>... columns) {
        viewModel.setExpressions(Arrays.asList(columns));
        OrmQueryFactory ormQueryFactory = dynamicBuilderContext.getDynamicContext().getOrmQueryFactory();
        viewModel.setSql(ormQueryFactory.select().showSql(query, columns));
        return this;
    }

    @Override
    public QViewBuilder resultSet(String sql, List<Expression<?>> columns) {
        viewModel.setExpressions(columns);
        viewModel.setSql(sql);
        return this;
    }

    @Override
    public QDynamicTableFactory finish() {
        dynamicBuilderContext.getViewSequances().put(viewModel.getName(), viewModel);
        return dynamicBuilderContext;
    }
}
