package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.querydsl.sql.RelationalPath;
import org.apache.commons.collections4.CollectionUtils;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.github.vzakharchenko.dynamic.orm.core.query.cache.CacheBuilder;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by vzakharchenko on 13.11.14.
 */
public class LazyListImpl<MODEL extends DMLModel> implements LazyList<MODEL> {

    protected final RelationalPath<?> qTable;

    private final List<Serializable> listPrimaryKey;

    private final Class<MODEL> modelClass;

    private final OrmQueryFactory ormQueryFactory;

    protected LazyListImpl(
            RelationalPath<?> qTable, List<Serializable> listPrimaryKey,
            Class<MODEL> modelClass, QueryContextImpl queryContext) {
        this.qTable = qTable;
        this.modelClass = modelClass;
        this.listPrimaryKey = listPrimaryKey;
        this.ormQueryFactory = queryContext.getOrmQueryFactory();
    }

    @Override
    public List<MODEL> getModelList() {
        if (CollectionUtils.isEmpty(listPrimaryKey)) {
            return Collections.EMPTY_LIST;
        }
        CacheBuilder<MODEL> cacheBuilder = ormQueryFactory.modelCacheBuilder(qTable, modelClass);
        return ModelLazyListFactory.buildModelLazyList(listPrimaryKey, cacheBuilder);
    }

    @Override
    public List<Serializable> getPrimaryKeyList() {
        return listPrimaryKey;
    }

    @Override
    public int size() {
        return listPrimaryKey.size();
    }

}
