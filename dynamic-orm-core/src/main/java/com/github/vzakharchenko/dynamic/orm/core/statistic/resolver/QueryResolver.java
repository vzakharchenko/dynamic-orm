package com.github.vzakharchenko.dynamic.orm.core.statistic.resolver;

import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticRegistrator;

/**
 *
 */
public interface QueryResolver<TYPE> {
    void resolve(QueryStatisticRegistrator queryStatisticRegistrator, TYPE type);
}
