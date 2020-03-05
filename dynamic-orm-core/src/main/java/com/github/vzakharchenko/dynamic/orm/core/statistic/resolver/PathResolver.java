package com.github.vzakharchenko.dynamic.orm.core.statistic.resolver;

import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticRegistrator;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;

/**
 *
 */
public class PathResolver implements QueryResolver<Path> {
    @Override
    public void resolve(QueryStatisticRegistrator queryStatisticRegistrator, Path path) {
        PathMetadata metadata = path.getMetadata();
        if (metadata != null) {
            QueryResolverFactory.fillStatistic(queryStatisticRegistrator, metadata);
        }
    }
}
