package com.github.vzakharchenko.dynamic.orm.core.cache.event;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumn;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModelFactory;
import com.github.vzakharchenko.dynamic.orm.core.helper.CompositeKey;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction.TransactionalCombinedEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Modify Event.
 */
public abstract class AbstractModifyEvent<EVENT extends AbstractModifyEvent<EVENT>>
        extends ApplicationEvent
        implements TransactionalCombinedEvent<EVENT>,
        ModifyEventBuilder<EVENT>,
        Cloneable {

    public static final String IS_NOT_FOUND = " is not found: ";
    private final RelationalPath<?> qTable;
    private final String resourceName;
    protected List<EVENT> transactionHistory = new ArrayList<>();
    protected Class<? extends DMLModel> modelClass0;
    protected DMLModel oldModel;
    protected DMLModel newModel;
    protected Map<CompositeKey, DiffColumnModel> diffColumnModelMap0;
    protected CacheEventType cacheEventType0;

    protected AbstractModifyEvent(RelationalPath<?> qTable) {
        super(qTable);
        this.qTable = qTable;
        this.resourceName = getClass().getName() + getQTable().getTableName();
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

    protected void combine(
            Map<CompositeKey, DiffColumnModel> newDiffColumnModelMap,
            Map.Entry<CompositeKey, DiffColumnModel> entry
    ) {
        CompositeKey key = entry.getKey();
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


    public final Class<? extends DMLModel> getModelClass() {
        return modelClass0;
    }

    protected Map<CompositeKey, DiffColumnModel> getDiffColumnModelMap() {
        return diffColumnModelMap0;
    }


    @Override
    public Serializable getResourceName() {
        return resourceName;
    }
}
