package com.github.vzakharchenko.dynamic.orm.core.mapper;


import com.querydsl.core.types.Expression;
import com.querydsl.sql.dml.Mapper;

/**
 *
 */
public abstract class AbstractMappingProjection<MODEL>
        extends CommonMappingProjection<MODEL> implements Mapper<MODEL> {

    protected AbstractMappingProjection(Class<? super MODEL> type, Expression<?>... args) {
        super(type, args);
    }


    protected MODEL createModel() {
        try {
            return this.getType().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
