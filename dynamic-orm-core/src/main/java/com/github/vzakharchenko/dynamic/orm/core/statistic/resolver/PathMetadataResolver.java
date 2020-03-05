package com.github.vzakharchenko.dynamic.orm.core.statistic.resolver;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticRegistrator;

/**
 *
 */
public class PathMetadataResolver implements QueryResolver<PathMetadata> {

    @Override
    public void resolve(QueryStatisticRegistrator queryStatisticRegistrator,
                        PathMetadata metadata) {
        Path parent = metadata.getParent();
        if (parent != null) {
            QueryResolverFactory.fillStatistic(queryStatisticRegistrator, parent);
        }
    }
}
