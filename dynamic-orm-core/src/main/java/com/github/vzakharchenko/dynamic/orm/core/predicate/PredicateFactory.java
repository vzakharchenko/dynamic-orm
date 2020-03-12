package com.github.vzakharchenko.dynamic.orm.core.predicate;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 13.04.15
 * Time: 18:36
 */
public abstract class PredicateFactory {


    public static Predicate and(Predicate... predicates) {
        BooleanExpression[] booleanExpressions = new BooleanExpression[predicates.length];

        for (int i = 0; i < predicates.length; i++) {
            booleanExpressions[i] = (BooleanExpression) predicates[i];
        }
        return and(booleanExpressions);
    }

    public static Predicate and(BooleanExpression... predicates) {
        BooleanExpression booleanExpression = null;
        for (BooleanExpression predicate : predicates) {
            booleanExpression = booleanExpression == null ?
                    predicate :
                    booleanExpression.and(wrapPredicate(predicate));
        }
        return booleanExpression;
    }

    public static BooleanExpression wrapPredicate(Predicate predicate) {
        return Expressions.booleanOperation(Ops.WRAPPED, predicate);
    }

    public static BooleanExpression wrapPredicate(Expression expression) {
        return Expressions.booleanOperation(Ops.WRAPPED, expression);
    }

    public static BooleanExpression alwaysFalsePredicate() {
        return Expressions.booleanTemplate("1 < 0"); // true == false
    }

}
