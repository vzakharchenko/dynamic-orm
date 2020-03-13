package com.github.vzakharchenko.dynamic.orm.core.predicate;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;

public abstract class OrderSpecifierFactory {

    public static OrderSpecifier getOrderSpecifierbyName(Order order,
                                                         String name) {
        return new OrderSpecifier(order, Expressions
                .stringTemplate(name));
    }
}
