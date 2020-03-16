package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.google.common.collect.ImmutableList;
import com.querydsl.sql.RelationalPath;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class QueryCacheContext implements CacheSupport<QueryCacheContext> {
    private final Collection<RelationalPath> qRelatedTables = new ArrayList<>();

    @Override
    public QueryCacheContext registerRelatedTables(Collection<RelationalPath> qTables) {
        qRelatedTables.addAll(qTables);
        return this;
    }

    public Collection<RelationalPath> getqRelatedTables() {
        return ImmutableList.copyOf(qRelatedTables);
    }
}

