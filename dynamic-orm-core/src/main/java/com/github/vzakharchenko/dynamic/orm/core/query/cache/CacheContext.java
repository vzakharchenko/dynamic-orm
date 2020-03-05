package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.lang3.StringUtils;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.statistic.QueryStatistic;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;


/**
 *
 */
public class CacheContext {
    private final ConcurrentMap<String, RequestCache> requestCacheMap =
            new ConcurrentHashMap<>();

    public void registerColumn(Path column) {
        String tableName = StringUtils.upperCase(ModelHelper.getTableName(column));
        requestCacheMap.putIfAbsent(tableName, new RequestCache());
        requestCacheMap.get(tableName).registerColumn(column);
    }

    public void register(String sqlString, QueryStatistic queryStatistic) {

        Set<RelationalPath> tables = queryStatistic.getTables();

        for (RelationalPath<?> table : tables) {
            String tableName = StringUtils.upperCase(ModelHelper.getTableName(table));
            requestCacheMap.putIfAbsent(tableName, new RequestCache());
            requestCacheMap.get(tableName).registerQuery(sqlString);
        }


    }

    public List<Path<Serializable>> getCachedColumns(String tableName) {
        String tableName0 = tableName;
        tableName0 = StringUtils.upperCase(tableName0);
        List<Path<Serializable>> columns = requestCacheMap.getOrDefault(tableName0,
                new RequestCache()).getColumns();
        return columns != null ? columns : Collections.emptyList();
    }

    public List<String> getCachedQueries(String tableName) {
        String tableName0 = tableName;
        tableName0 = StringUtils.upperCase(tableName0);
        List<String> columns = requestCacheMap.getOrDefault(tableName0,
                new RequestCache()).getSqls();
        return columns != null ? columns : Collections.emptyList();
    }

    public void clear() {
        requestCacheMap.clear();
    }

    private static class RequestCache {

        private final Set<String> sqls = new ConcurrentSkipListSet<>();

        private final Map<String, Path<Serializable>> columns = new ConcurrentHashMap<>();

        public void registerQuery(String sql) {
            sqls.add(sql);
        }

        public void registerColumn(Path column) {
            columns.put(ModelHelper.getColumnName(column), column);
        }

        public List<Path<Serializable>> getColumns() {
            return ImmutableList.copyOf(columns.values());
        }

        public List<String> getSqls() {
            return ImmutableList.copyOf(sqls);
        }
    }
}
