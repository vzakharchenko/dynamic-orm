package com.github.vzakharchenko.dynamic.orm.core.cache.event;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumn;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModelFactory;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction.TransactionalCombinedEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.*;

/**
 * Modify Event.
 */
public abstract class ModifyEvent<EVENT extends ModifyEvent<EVENT>>
        extends ApplicationEvent
        implements TransactionalCombinedEvent<EVENT>,
        ModifyEventBuilder<EVENT>,
        Cloneable {

    public static final String IS_NOT_FOUND = " is not found: ";
    private final RelationalPath<?> qTable;
    protected List<EVENT> transactionHistory = new ArrayList<>();
    private Class<? extends DMLModel> modelClass0;
    private final String resourceName;
    private DMLModel oldModel;
    private DMLModel newModel;
    private Map<Serializable, DiffColumnModel> diffColumnModelMap0;
    private CacheEventType cacheEventType0;

    protected ModifyEvent(RelationalPath<?> qTable) {
        super(qTable);
        this.qTable = qTable;
        this.resourceName = getClass().getName() + getQTable().getTableName();
    }

    @Override
    public ModifyEventBuilder<EVENT> modelClass(Class<? extends DMLModel> modelClass) {
        this.modelClass0 = modelClass;
        return this;
    }

    @Override
    public ModifyEventBuilder<EVENT> diffColumnModelMap(
            Map<Serializable, DiffColumnModel> diffColumnModelMap) {
        this.diffColumnModelMap0 = diffColumnModelMap;
        return this;
    }

    @Override
    public ModifyEventBuilder<EVENT> cacheEventType(
            CacheEventType cacheEventType) {
        this.cacheEventType0 = cacheEventType;
        return this;
    }

    @Override
    public EVENT create() {
        this.transactionHistory.add(clone());
        return (EVENT) this;
    }

    /**
     * All values of the primary keys are involved in the transaction
     *
     * @return list of primary keys
     */
    public final List<Serializable> getListIds() {
        return ImmutableList.copyOf(diffColumnModelMap0.keySet());
    }

    /**
     * the difference for a specific value of the primary key
     *
     * @param id value of the primary key
     * @return diff model
     * @see DiffColumnModel
     */
    public final DiffColumnModel getDiffModel(Serializable id) {
        return diffColumnModelMap0.get(id);
    }

    public final <TYPE> DiffColumn<TYPE> getDiffColumnValue(Serializable id,
                                                            Path<TYPE> column) {
        DiffColumnModel diffModel = getDiffModel(id);

        if (diffModel == null) {
            throw new IllegalStateException(id + IS_NOT_FOUND + ToStringBuilder
                    .reflectionToString(getListIds()));
        }

        return diffModel.getColumnDiff(column);
    }

    /**
     * get only changed columns for a specific value of the primary key
     *
     * @param id
     * @return
     * @see DiffColumn
     */
    public final Map<Path<?>, DiffColumn<?>> onlyChangedColumns(Serializable id) {
        DiffColumnModel diffModel = getDiffModel(id);

        if (diffModel == null) {
            throw new IllegalStateException(id + IS_NOT_FOUND +
                    ToStringBuilder.reflectionToString(getListIds()));
        }

        return diffModel.getOnlyChangedColumns();
    }

    public final <TYPE> List<DiffColumn<TYPE>> getDiffColumnValues(Path<TYPE> column) {
        List<Serializable> listIds = getListIds();
        List<DiffColumn<TYPE>> diffColumns = new ArrayList<>(listIds.size());
        for (Serializable key : listIds) {
            diffColumns.add(getDiffColumnValue(key, column));
        }
        return Collections.unmodifiableList(diffColumns);
    }

    public final DMLModel getOldModel(Serializable id) {
        DiffColumnModel diffModel = getDiffModel(id);
        if (diffModel == null) {
            throw new IllegalStateException(id + IS_NOT_FOUND +
                    ToStringBuilder.reflectionToString(getListIds()));
        }

        if (oldModel == null) {
            oldModel = DiffColumnModelFactory.buildOldModel(modelClass0, diffModel);
        }
        return oldModel;
    }

    public final DMLModel getNewModel(Serializable id) {
        DiffColumnModel diffModel = getDiffModel(id);
        if (diffModel == null) {
            throw new IllegalStateException(id + IS_NOT_FOUND +
                    ToStringBuilder.reflectionToString(getListIds()));
        }

        if (newModel == null) {
            newModel = DiffColumnModelFactory.buildNewModel(modelClass0, diffModel);
        }
        return newModel;
    }


    @Override
    public final RelationalPath<?> getSource() {
        return (RelationalPath) super.getSource();
    }

    public final CacheEventType cacheEventType() {
        return cacheEventType0;
    }

//    public final boolean isOneOf(RelationalPath... qTables) {
//        int indexOf = ArrayUtils.indexOf(qTables, qTable);
//        return indexOf > -1;
//    }

    public final RelationalPath<?> getQTable() {
        return qTable;
    }

    private void combine(
            Map<Serializable, DiffColumnModel> newDiffColumnModelMap,
            Map.Entry<Serializable, DiffColumnModel> entry
    ) {
        Serializable key = entry.getKey();
        DiffColumnModel diffColumnModelMerge = entry.getValue();
        DiffColumnModel diffColumnModelOrigin = this.diffColumnModelMap0.get(key);
        if (diffColumnModelOrigin == null) {
            newDiffColumnModelMap.put(key, diffColumnModelMerge);
        } else {
            Map<Path<?>, DiffColumn<?>> onlyChangedColumns = diffColumnModelMerge
                    .getOnlyChangedColumns();
            Map<Path<?>, DiffColumn<?>> diffModels = diffColumnModelOrigin.getDiffModels();
            Map<Path<?>, DiffColumn<?>> newDiffModels = Maps
                    .newHashMapWithExpectedSize(onlyChangedColumns.size());
            newDiffModels.putAll(diffModels);
            onlyChangedColumns.forEach((column, value) -> {
                DiffColumn<?> diffColumn = diffModels.get(column);
                newDiffModels.put(column, DiffColumnModelFactory
                        .buildDiffColumn((Path) column, diffColumn.getOldValue(),
                                value.getNewValue()));
            });

            newDiffColumnModelMap.put(key, DiffColumnModelFactory
                    .buildDiffColumnModel(qTable, newDiffModels));
        }
    }

    @Override
    public void combine(EVENT modelModifyEvent) {
        Map<Serializable, DiffColumnModel> newDiffColumnModelMap
                = new HashMap<>(diffColumnModelMap0);
        Map<Serializable, DiffColumnModel> diffColumnMap = modelModifyEvent
                .getDiffColumnModelMap();
        for (Map.Entry<Serializable, DiffColumnModel> entry : diffColumnMap.entrySet()) {
            combine(newDiffColumnModelMap, entry);
        }
        this.diffColumnModelMap0 = newDiffColumnModelMap;
        this.transactionHistory.add(modelModifyEvent);
        this.cacheEventType0 = CacheEventType.BATCH;
        oldModel = null;
        newModel = null;
    }

    @Override
    public Serializable getResourceName() {

        return resourceName;
    }

    public final Class<? extends DMLModel> getModelClass() {
        return modelClass0;
    }

    @Override
    public final List<EVENT> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    protected Map<Serializable, DiffColumnModel> getDiffColumnModelMap() {
        return diffColumnModelMap0;
    }

    @Override
    public EVENT clone() {
        try {
            return (EVENT) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }
}
