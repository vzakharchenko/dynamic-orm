package com.github.vzakharchenko.dynamic.orm.core.statistic.resolver;

import com.querydsl.sql.RelationalPath;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticRegistrator;

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
