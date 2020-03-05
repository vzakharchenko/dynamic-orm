package com.github.vzakharchenko.dynamic.orm.core.statistic;


import com.querydsl.sql.RelationalPath;

import java.util.Collection;

/**
 *
 */
public interface QueryStatisticRegistrator {
    void register(RelationalPath<?> qTable);

    void register(Collection<RelationalPath> qTables);
}
