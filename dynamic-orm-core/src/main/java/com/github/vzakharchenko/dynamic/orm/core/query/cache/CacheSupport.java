package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.querydsl.sql.RelationalPath;

import java.util.Collection;

/**
 *
 */
public interface CacheSupport<BUILDER> {
    /**
     * force registration tables used in the query
     * <p>
     * It is should be use If the query uses a Views or Functions
     *
     * @param qTables querydsl Models
     * @return this
     */
    BUILDER registerRelatedTables(RelationalPath... qTables);

    /**
     * force registration tables used in the query
     * <p>
     * It is should be use If the query uses a Views or Functions
     *
     * @param qTables querydsl Models
     * @return this
     */
    BUILDER registerRelatedTables(Collection<RelationalPath> qTables);
}
