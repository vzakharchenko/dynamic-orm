package com.github.vzakharchenko.dynamic.orm.core.statistic.resolver;

import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatisticRegistrator;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.*;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;

/**
 *
 */
public abstract class QueryResolverFactory {
    public static final ConstantQueryResolver CONSTANT_QUERY_RESOLVER =
            new ConstantQueryResolver();
    private static final PathMetadataResolver PATH_METADATA_RESOLVER =
            new PathMetadataResolver();
    private static final QueryMetadataResolver QUERY_METADATA_RESOLVER =
            new QueryMetadataResolver();
    private static final RelationalPathResolver RELATIONAL_PATH_BASE_RESOLVER =
            new RelationalPathResolver();
    private static final OperationResolver OPERATION_RESOLVER = new OperationResolver();
    private static final PathResolver PATH_RESOLVER = new PathResolver();
    private static final SubQueryResolver SUB_QUERY_RESOLVER = new SubQueryResolver();
    private static final TemplateExpressionResolver TEMPLATE_EXPRESSION_RESOLVER =
            new TemplateExpressionResolver();

    public static void fillSQLQueryStatistic(QueryStatisticRegistrator queryStatistic,
                                             SQLQuery sqlQuery) {
        QueryMetadata metadata = sqlQuery.getMetadata();
        fillStatistic(queryStatistic, metadata);
    }


    public static void fillStatistic(QueryStatisticRegistrator queryStatistic, Object object) {
        if (object instanceof QueryMetadata) {
            QUERY_METADATA_RESOLVER.resolve(queryStatistic, (QueryMetadata) object);
        } else if (object instanceof RelationalPath) {
            RELATIONAL_PATH_BASE_RESOLVER.resolve(queryStatistic, (RelationalPath<?>) object);
        } else if (object instanceof Operation) {
            OPERATION_RESOLVER.resolve(queryStatistic, (Operation) object);
        } else if (object instanceof Path) {
            PATH_RESOLVER.resolve(queryStatistic, (Path) object);
        } else if (object instanceof SubQueryExpression) {
            SUB_QUERY_RESOLVER.resolve(queryStatistic, (SubQueryExpression) object);
        } else if (object instanceof TemplateExpression) {
            TEMPLATE_EXPRESSION_RESOLVER.resolve(queryStatistic, (TemplateExpression) object);
        } else if (object instanceof PathMetadata) {
            PATH_METADATA_RESOLVER.resolve(queryStatistic, (PathMetadata) object);
        } else if (object instanceof Constant) {
            CONSTANT_QUERY_RESOLVER.resolve(queryStatistic, (Constant) object);
        } else {
            throw new UnsupportedOperationException("Unsupport " + object);
        }
    }


}
