package com.github.vzakharchenko.dynamic.orm.core.statistic.resolver;

import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticRegistrator;
import com.querydsl.sql.RelationalPath;

/**
 *
 */
public class RelationalPathResolver implements QueryResolver<RelationalPath<?>> {

    @Override
    public void resolve(QueryStatisticRegistrator queryStatisticRegistrator,
                        RelationalPath<?> relationalPathBase) {
        queryStatisticRegistrator.register(relationalPathBase);
    }
}
