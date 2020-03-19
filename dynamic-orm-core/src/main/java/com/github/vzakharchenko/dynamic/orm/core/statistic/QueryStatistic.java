package com.github.vzakharchenko.dynamic.orm.core.statistic;


import com.github.vzakharchenko.dynamic.orm.core.query.cache.StatisticCacheHolder;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import com.querydsl.sql.RelationalPath;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public interface QueryStatistic {

    List<RelationalPath> getTables();

    StatisticCacheHolder get(TransactionalCache transactionalCache,
                             String sql,
                             List<? extends Serializable> primaryKeys);
}
