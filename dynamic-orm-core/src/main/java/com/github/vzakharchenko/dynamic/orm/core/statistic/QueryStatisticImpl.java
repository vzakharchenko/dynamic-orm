package com.github.vzakharchenko.dynamic.orm.core.statistic;

import com.google.common.collect.ImmutableSet;
import com.querydsl.sql.RelationalPath;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class QueryStatisticImpl implements QueryStatistic, QueryStatisticRegistrator {
    private final Set<RelationalPath> qTables = new HashSet<>();

    @Override
    public void register(RelationalPath<?> qTable) {
        qTables.add(qTable);
    }

    @Override
    public void register(Collection<RelationalPath> qTables0) {
        this.qTables.addAll(qTables0);
    }

    @Override
    public Set<RelationalPath> getTables() {
        return ImmutableSet.copyOf(qTables);
    }
}
