package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatistic;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Set;


/**
 *
 */
public class CacheContext {

    private final TransactionalCache cache;

    public CacheContext(TransactionalCache cache) {
        this.cache = cache;
    }

    private QueryCache getTableCacheOrCreateNew(String tableName) {
        QueryCache queryCache = cache
                .getFromTargetCache(StringUtils.upperCase(tableName), QueryCache.class);
        if (queryCache == null) {
            queryCache = new QueryCache();
        }
        return queryCache;
    }

    private void saveQueryCache(String tableName, QueryCache queryCache) {
        QueryCache fromCache = getTableCacheOrCreateNew(tableName);
        if (queryCache.valid(fromCache)) {
            cache.putToCache(StringUtils.upperCase(tableName), queryCache.increment());
        } else {
            queryCache.getSqls().forEach(fromCache::registerQuery);
            queryCache.getColumns().forEach(fromCache::registerColumn);
            saveQueryCache(tableName, fromCache);
        }
    }

    public void registerColumn(Path column) {
        String tableName = StringUtils.upperCase(ModelHelper.getTableName(column));
        QueryCache queryCache = getTableCacheOrCreateNew(tableName);
        queryCache.registerColumn(column);
        saveQueryCache(tableName, queryCache);
    }

    public void register(String sqlString, QueryStatistic queryStatistic) {

        Set<RelationalPath> tables = queryStatistic.getTables();
        tables.stream().forEach(table -> {
            String tableName = StringUtils.upperCase(ModelHelper.getTableName(table));
            QueryCache queryCache = getTableCacheOrCreateNew(tableName);
            queryCache.registerQuery(sqlString);
            saveQueryCache(tableName, queryCache);
        });
    }

    public List<Path<Serializable>> getCachedColumns(String tableName) {
        QueryCache queryCache = getTableCacheOrCreateNew(StringUtils.upperCase(tableName));
        return queryCache.getColumns();
    }

    public List<String> getCachedQueries(String tableName) {
        QueryCache queryCache = getTableCacheOrCreateNew(StringUtils.upperCase(tableName));
        return queryCache.getSqls();
    }

}
