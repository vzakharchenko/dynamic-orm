package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumn;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.event.CacheEvent;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.querydsl.core.types.Path;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertNotNull;

/**
 *
 */
public class LogAudit implements ApplicationListener<CacheEvent> {
    @Override
    public void onApplicationEvent(CacheEvent cacheEvent) {
        switch (cacheEvent.cacheEventType()) {
            case INSERT: {
                for (Serializable pk : cacheEvent.getListIds()) {
                    cacheEvent.onlyChangedColumns(pk);
                    cacheEvent.getOldModel(pk);
                    cacheEvent.getNewModel(pk);
                    System.out.println("insert table " + cacheEvent.getQTable().getTableName()
                            + " primarykey = " + pk);
                    DiffColumnModel diffModel = cacheEvent.getDiffModel(pk);
                    for (Map.Entry<Path<?>, DiffColumn<?>> entry : diffModel.getDiffModels().entrySet()) {
                        System.out.println(" --- column " + ModelHelper.getColumnRealName(entry.getKey())
                                + " set " + entry.getValue().getNewValue());
                        cacheEvent.getDiffColumnValues(entry.getKey());
                    }
                }
                break;
            }
            case UPDATE: {
                for (Serializable pk : cacheEvent.getListIds()) {
                    System.out.println("update table " + cacheEvent.getQTable().getTableName());
                    DiffColumnModel diffModel = cacheEvent.getDiffModel(pk);
                    for (Map.Entry<Path<?>, DiffColumn<?>> entry : diffModel.getOnlyChangedColumns().entrySet()) {
                        DiffColumn<?> diffColumn = entry.getValue();
                        diffColumn.isChanged();
                        assertNotNull(diffColumn.getColumn());
                        System.out.println(" --- column " + ModelHelper.getColumnRealName(entry.getKey())
                                + " set " + diffColumn.getNewValue()
                                + " old value "
                                + diffColumn.getOldValue());
                    }
                }

                break;
            }
            case SOFT_DELETE:
            case DELETE: {
                System.out.println("delete into table " + cacheEvent.getQTable().getTableName() + " ids = " + ToStringBuilder.reflectionToString(cacheEvent.getListIds(), ToStringStyle.JSON_STYLE));
                break;
            }
            case BATCH: {
                List<? extends CacheEvent> transactionHistory = cacheEvent.getTransactionHistory();
                for (CacheEvent event : transactionHistory) {
                    onApplicationEvent(event);
                }
                break;
            }
            default: {
                throw new IllegalStateException(cacheEvent.cacheEventType() + " is not supported");
            }
        }
    }
}
