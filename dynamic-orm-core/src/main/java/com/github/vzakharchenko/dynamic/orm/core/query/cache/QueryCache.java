package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class QueryCache implements Serializable {
    private int count;
    private final List<String> sqls = new ArrayList<>();

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

    public boolean valid(QueryCache queryCache) {
        return Objects.equals(count, queryCache.count) || queryCache.count == 0;
    }

    public QueryCache increment() {
        ++count;
        return this;
    }
}
