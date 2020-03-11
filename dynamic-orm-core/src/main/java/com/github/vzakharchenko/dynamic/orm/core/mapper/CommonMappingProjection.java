package com.github.vzakharchenko.dynamic.orm.core.mapper;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.MappingProjection;

/**
 * Created by vzakharchenko on 19.03.15.
 */
public abstract class CommonMappingProjection<TYPE> extends MappingProjection<TYPE> {

    public CommonMappingProjection(Class<? super TYPE> type, Expression<?>... args) {
        super(type, args);
    }

    @Override
    public abstract TYPE map(Tuple row);
}
