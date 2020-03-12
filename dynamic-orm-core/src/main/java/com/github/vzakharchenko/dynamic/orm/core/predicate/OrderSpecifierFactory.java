package com.github.vzakharchenko.dynamic.orm.core.predicate;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;

import java.util.Objects;

public abstract class OrderSpecifierFactory {

    public static OrderSpecifier getOrderSpecifierbyPosition(Order order,
                                                             Integer position) {
        return new OrderSpecifier(order, Expressions
                .stringTemplate(Objects.toString(position)));
    }

    public static OrderSpecifier getOrderSpecifierbyName(Order order,
                                                         String name) {
        return new OrderSpecifier(order, Expressions
                .stringTemplate(name));
    }
}
