package com.github.vzakharchenko.dynamic.orm.core.cache.event;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.ApplicationEvent;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumn;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModelFactory;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction.TransactionalCombinedEvent;

import java.io.Serializable;
import java.util.*;

/**
 *
 */
public abstract class ModifyEvent<EVENT extends ModifyEvent<EVENT>>
        extends ApplicationEvent
        implements TransactionalCombinedEvent<EVENT>, Cloneable {

    protected final List<EVENT> transactionHistory;
    private final Class<? extends DMLModel> modelClass;
    private final RelationalPath<?> qTable;
    private final String resourceName;
    private DMLModel oldModel;
    private DMLModel newModel;
    private Map<Serializable, DiffColumnModel> diffColumnModelMap;
    private CacheEventType cacheEventType;

    protected ModifyEvent(CacheEventType cacheEventType,
                          RelationalPath<?> qTable,
                          Class<? extends DMLModel> modelClass,
                          Map<Serializable, DiffColumnModel> diffColumnModelMap) {
        super(qTable);
        this.qTable = qTable;
        this.modelClass = modelClass;
        this.diffColumnModelMap = diffColumnModelMap;
        this.cacheEventType = cacheEventType;
        this.resourceName = getClass().getName() + getQTable().getTableName();
        this.transactionHistory = new ArrayList<>();
        this.transactionHistory.add(clone());
    }

    /**
     * All values of the primary keys are involved in the transaction
     *
     * @return list of primary keys
     */
    public final List<Serializable> getListIds() {
        return ImmutableList.copyOf(diffColumnModelMap.keySet());
    }

    /**
     * the difference for a specific value of the primary key
     *
     * @param id value of the primary key
     * @return diff model
     * @see DiffColumnModel
     */
    public final DiffColumnModel getDiffModel(Serializable id) {
        return diffColumnModelMap.get(id);
    }

    public final <TYPE> DiffColumn<TYPE> getDiffColumnValue(Serializable id,
                                                            Path<TYPE> column) {
        DiffColumnModel diffModel = getDiffModel(id);

        if (diffModel == null) {
            throw new IllegalStateException(id + " is not found: " + ToStringBuilder
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
            throw new IllegalStateException(id + " is not found: " +
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
            throw new IllegalStateException(id + " is not found: " +
                    ToStringBuilder.reflectionToString(getListIds()));
        }

        if (oldModel == null) {
            oldModel = DiffColumnModelFactory.buildOldModel(modelClass, diffModel);
        }
        return oldModel;
    }

    public final DMLModel getNewModel(Serializable id) {
        DiffColumnModel diffModel = getDiffModel(id);
        if (diffModel == null) {
            throw new IllegalStateException(id + " is not found: " +
                    ToStringBuilder.reflectionToString(getListIds()));
        }

        if (newModel == null) {
            newModel = DiffColumnModelFactory.buildNewModel(modelClass, diffModel);
        }
        return newModel;
    }


    @Override
    public final RelationalPath<?> getSource() {
        return (RelationalPath) super.getSource();
    }

    public final CacheEventType cacheEventType() {
        return cacheEventType;
    }

    public final boolean isOneOf(RelationalPath... qTables) {
        int indexOf = ArrayUtils.indexOf(qTables, qTable);
        return indexOf > -1;
    }

    public final RelationalPath<?> getQTable() {
        return qTable;
    }

    @Override
    public void combinate(EVENT modelModifyEvent) {
        Map<Serializable, DiffColumnModel> newDiffColumnModelMap = new HashMap<>();
        newDiffColumnModelMap.putAll(diffColumnModelMap);
        Map<Serializable, DiffColumnModel> diffColumnModelMap0 = modelModifyEvent
                .getDiffColumnModelMap();
        for (Map.Entry<Serializable, DiffColumnModel> entry : diffColumnModelMap0.entrySet()) {
            Serializable key = entry.getKey();
            DiffColumnModel diffColumnModelMerge = entry.getValue();
            DiffColumnModel diffColumnModelOrigin = this.diffColumnModelMap.get(key);
            if (diffColumnModelOrigin == null) {
                newDiffColumnModelMap.put(key, diffColumnModelMerge);
            } else {
                Map<Path<?>, DiffColumn<?>> onlyChangedColumns = diffColumnModelMerge
                        .getOnlyChangedColumns();
                Map<Path<?>, DiffColumn<?>> diffModels = diffColumnModelOrigin.getDiffModels();
                Map<Path<?>, DiffColumn<?>> newDiffModels = Maps
                        .newHashMapWithExpectedSize(onlyChangedColumns.size());
                newDiffModels.putAll(diffModels);
                for (Map.Entry<Path<?>, DiffColumn<?>> columnEntry : onlyChangedColumns
                        .entrySet()) {
                    Path<?> column = columnEntry.getKey();
                    DiffColumn<?> diffColumn = diffModels.get(column);
                    newDiffModels.put(column, DiffColumnModelFactory
                            .buildDiffColumn((Path) column, diffColumn.getOldValue(),
                                    columnEntry.getValue().getNewValue()));
                }
                newDiffColumnModelMap.put(key, DiffColumnModelFactory
                        .buildDiffColumnModel(qTable, newDiffModels));
            }
        }
        this.diffColumnModelMap = newDiffColumnModelMap;
        this.transactionHistory.add(modelModifyEvent);
        this.cacheEventType = CacheEventType.BATCH;
        oldModel = null;
        newModel = null;
    }

    @Override
    public Serializable resourceName() {

        return resourceName;
    }

    public final Class<? extends DMLModel> getModelClass() {
        return modelClass;
    }

    @Override
    public final List<EVENT> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    protected Map<Serializable, DiffColumnModel> getDiffColumnModelMap() {
        return diffColumnModelMap;
    }

    @Override
    protected EVENT clone() {
        try {
            return (EVENT) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }
}
