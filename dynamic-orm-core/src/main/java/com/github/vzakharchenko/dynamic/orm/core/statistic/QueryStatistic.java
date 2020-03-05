package com.github.vzakharchenko.dynamic.orm.core.statistic;


import com.querydsl.sql.RelationalPath;

import java.util.Set;

/**
 *
 */
public interface QueryStatistic {

    Set<RelationalPath> getTables();
}
