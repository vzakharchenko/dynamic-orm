package com.github.vzakharchenko.dynamic.orm.core.query;

import com.github.vzakharchenko.dynamic.orm.core.predicate.OrderSpecifierFactory;
import com.querydsl.core.types.Order;

public class OrderByBuilderImpl implements OrderByBuilder {

    private final String columnName;
    private final QueryContextImpl queryContext;
    private final UnionBuilder unionBuilder;

    public OrderByBuilderImpl(String columnName,
                              QueryContextImpl queryContext,
                              UnionBuilder unionBuilder) {
        this.columnName = columnName;
        this.queryContext = queryContext;
        this.unionBuilder = unionBuilder;
    }

    public UnionBuilder sort(Order order) {
        boolean useQuotes = queryContext.getDialect().isUseQuotes();
        unionBuilder.orderBy(
                OrderSpecifierFactory
                        .getOrderSpecifierbyName(order,
                                useQuotes ? "\"" + columnName + "\"" : columnName));
        return unionBuilder;
    }

    @Override
    public UnionBuilder desc() {
        return sort(Order.DESC);
    }

    @Override
    public UnionBuilder asc() {
        return sort(Order.ASC);
    }
}
