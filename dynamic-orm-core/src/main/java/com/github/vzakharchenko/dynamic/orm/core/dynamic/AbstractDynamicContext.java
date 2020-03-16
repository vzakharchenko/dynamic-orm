package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import liquibase.database.Database;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AbstractDynamicContext {
    public static final String DYNAMIC_METADATA = "DYNAMIC_METADATA";
    public static final String DYNAMIC_VIEW_METADATA = "DYNAMIC_VIEW_METADATA";
    public static final String DYNAMIC_SEQUENCE_METADATA = "DYNAMIC_SEQUENCE_METADATA";

    protected final Map<String, QDynamicTable> dynamicTableMap = new ConcurrentHashMap<>();
    protected final Map<String, ViewDataHolder> viewMap = new ConcurrentHashMap<>();
    protected final Map<String, SequanceModel> sequenceModelMap = new ConcurrentHashMap<>();
    protected final Database database;
    private final OrmQueryFactory ormQueryFactory;

    public AbstractDynamicContext(Database database, OrmQueryFactory ormQueryFactory) {
        this.ormQueryFactory = ormQueryFactory;
        this.database = database;
    }

    private void registerQTable(QDynamicTable qDynamicTable) {
        dynamicTableMap.put(StringUtils.upperCase(qDynamicTable.getTableName()), qDynamicTable);
    }

    private void registerQView(ViewDataHolder viewDataHolder) {
        viewMap.put(StringUtils.upperCase(viewDataHolder.getDynamicTable().getTableName()), viewDataHolder);
    }

    private void registerSequence(SequanceModel sequanceModel) {
        sequenceModelMap.put(StringUtils.upperCase(sequanceModel.getName()), sequanceModel);
    }

    public void registerQ(Map<String, QDynamicTable> qDynamicTableMap,
                          Collection<QDynamicTable> qDynamicTables, String cacheName) {
        for (QDynamicTable qDynamicTable : qDynamicTables) {
            qDynamicTableMap.put(StringUtils.upperCase(
                    qDynamicTable.getTableName()), qDynamicTable);
        }
        updateCache(qDynamicTableMap, cacheName);
    }

    public void registerQTables(Collection<QDynamicTable> qDynamicTables) {
        registerQ(dynamicTableMap, qDynamicTables, DYNAMIC_METADATA);
    }

    public void registerQViews(Collection<ViewDataHolder> qDynamicTables) {
        for (ViewDataHolder viewDataHolder : qDynamicTables) {
            registerQView(viewDataHolder);
        }
        CacheViewStorage cacheStorage = new CacheViewStorage();
        cacheStorage.setViews(new ArrayList<>(viewMap.values()));
        ormQueryFactory.getContext().getTransactionCache().putToCache(DYNAMIC_VIEW_METADATA, cacheStorage);
    }

    private void updateCache(Map<String, QDynamicTable> qDynamicTableMap,
                             String cacheName) {
        CacheStorageImpl cacheStorage = new CacheStorageImpl();
        cacheStorage.setDynamicTables(new ArrayList<>(qDynamicTableMap.values()));
        ormQueryFactory.getContext().getTransactionCache().putToCache(cacheName, cacheStorage);
    }

    protected void updateDynamicTables() {
        CacheStorage<QDynamicTable> cacheStorage = ormQueryFactory.getContext().getTransactionCache()
                .getFromCache(DYNAMIC_METADATA, CacheStorage.class);
        if (cacheStorage != null) {
            cacheStorage.getDynamicObjects().forEach(this::registerQTable);
        }
    }

    protected void updateDynamicViews() {
        CacheStorage<ViewDataHolder> cacheStorage = ormQueryFactory.getContext().getTransactionCache()
                .getFromCache(DYNAMIC_VIEW_METADATA, CacheStorage.class);
        if (cacheStorage != null) {
            cacheStorage.getDynamicObjects().forEach(this::registerQView);
        }
    }

    public QDynamicTable createQTable(String tableName) {
        updateDynamicTables();
        QDynamicTable qDynamicTable = dynamicTableMap.get(StringUtils.upperCase(tableName));
        return qDynamicTable != null ? qDynamicTable : new QDynamicTable(tableName);
    }

    public OrmQueryFactory getOrmQueryFactory() {
        return ormQueryFactory;
    }

    public void registerViews(Collection<ViewModel> viewModels) {
        List<ViewDataHolder> qDynamicTables = viewModels.stream().map(viewModel -> {
            ViewDataHolder viewDataHolder = new ViewDataHolder();
            viewDataHolder.setDynamicTable(QViewUtils.transform(database, viewModel));
            viewDataHolder.setViewModel(viewModel);
            return viewDataHolder;
        })
                .collect(Collectors.toList());
        registerQViews(qDynamicTables);
    }

    public void registerSequences(Map<String, SequanceModel> sequenceModels) {
        sequenceModelMap.putAll(sequenceModels);
        SequenceCacheStorage cacheStorage = new SequenceCacheStorage();
        cacheStorage.setSequenceModels(new ArrayList<>(sequenceModels.values()));
        ormQueryFactory.getContext().getTransactionCache()
                .putToCache(DYNAMIC_SEQUENCE_METADATA, cacheStorage);
    }

    protected void updateSequences() {
        CacheStorage<SequanceModel> cacheStorage = ormQueryFactory.getContext().getTransactionCache()
                .getFromCache(DYNAMIC_SEQUENCE_METADATA, CacheStorage.class);
        if (cacheStorage != null) {
            cacheStorage.getDynamicObjects().forEach(this::registerSequence);
        }
    }
}
