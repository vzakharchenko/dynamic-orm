package orm.query.examples.modify;

import com.querydsl.core.types.Path;
import org.springframework.context.event.EventListener;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumn;
import com.github.vzakharchenko.dynamic.orm.core.cache.DiffColumnModel;
import com.github.vzakharchenko.dynamic.orm.core.cache.event.CacheEvent;
import com.github.vzakharchenko.dynamic.orm.core.cache.event.DiffEvent;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class AuditAccountListener {

    @EventListener()
    public void auditEvent(DiffEvent diffEvent) {
        System.out.println("BeforeCommit: " + diffEvent.getQTable().getTableName());
        switch (diffEvent.cacheEventType()) {
            case INSERT: {
                System.out.println("BeforeCommit: insert record");
                List<Serializable> ids = diffEvent.getListIds();
                for (Serializable id : ids) {
                    System.out.println("BeforeCommit: insert record with id =" + id);
                    DiffColumnModel diffColumnModel = diffEvent.getDiffModel(id);
                    for (Map.Entry<Path<?>, DiffColumn<?>> entry : diffColumnModel.getOnlyChangedColumns().entrySet()) {
                        System.out.println("BeforeCommit: field set " + ModelHelper.getColumnRealName(entry.getKey()) + "=" + entry.getValue().getNewValue());
                    }
                }
                break;
            }
            case DELETE: {
                System.out.println("BeforeCommit: delete record");
                List<Serializable> ids = diffEvent.getListIds();
                for (Serializable id : ids) {
                    System.out.println("BeforeCommit: delete record with id =" + id);
                }
                break;
            }
            case UPDATE: {
                System.out.println("BeforeCommit: update record");
                List<Serializable> ids = diffEvent.getListIds();
                for (Serializable id : ids) {
                    System.out.println("BeforeCommit: update record with id =" + id);
                    DiffColumnModel diffColumnModel = diffEvent.getDiffModel(id);
                    for (Map.Entry<Path<?>, DiffColumn<?>> entry : diffColumnModel.getOnlyChangedColumns().entrySet()) {
                        System.out.println("BeforeCommit: field set " + ModelHelper.getColumnRealName(entry.getKey()) + "=" + entry.getValue().getNewValue() + " oldValue = " + entry.getValue().getOldValue());
                    }

                }
                break;
            }
            case BATCH: {
                List<? extends DiffEvent> transactionHistory = diffEvent.getTransactionHistory();
                for (DiffEvent event : transactionHistory) {
                    auditEvent(event);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unsupport " + diffEvent.cacheEventType());
            }

        }
    }

    @EventListener()
    public void afterCommitEvent(CacheEvent cacheEvent) {
        System.out.println("AfterCommit: " + cacheEvent.getQTable().getTableName());
        switch (cacheEvent.cacheEventType()) {
            case INSERT: {
                System.out.println("AfterCommit: insert record");
                List<Serializable> ids = cacheEvent.getListIds();
                for (Serializable id : ids) {
                    System.out.println("AfterCommit: insert record with id =" + id);
                    DiffColumnModel diffForObject = cacheEvent.getDiffModel(id);
                    Map<Path<?>, DiffColumn<?>> diffModels = diffForObject.getOnlyChangedColumns();
                    for (Map.Entry<Path<?>, DiffColumn<?>> entry : diffModels.entrySet()) {
                        System.out.println("AfterCommit: field set " + ModelHelper.getColumnRealName(entry.getKey()) + "=" + entry.getValue());
                    }
                }
                break;
            }
            case DELETE: {
                System.out.println("AfterCommit: delete record");
                List<Serializable> ids = cacheEvent.getListIds();
                for (Serializable id : ids) {
                    System.out.println("AfterCommit: delete record with id =" + id);
                }
                break;
            }
            case UPDATE: {
                System.out.println("AfterCommit: update record");
                List<Serializable> ids = cacheEvent.getListIds();
                for (Serializable id : ids) {
                    System.out.println("AfterCommit: update record with id =" + id);
                    DiffColumnModel diffForObject = cacheEvent.getDiffModel(id);
                    for (Map.Entry<Path<?>, DiffColumn<?>> entry : diffForObject.getOnlyChangedColumns().entrySet()) {
                        System.out.println("AfterCommit: field set " + ModelHelper.getColumnRealName(entry.getKey()) + "=" + entry.getValue().getNewValue() + " oldValue = " + entry.getValue().getOldValue());
                    }
                }
                break;
            }
            case BATCH: {
                List<? extends CacheEvent> transactionHistory = cacheEvent.getTransactionHistory();
                for (CacheEvent event : transactionHistory) {
                    afterCommitEvent(event);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unsupport " + cacheEvent.cacheEventType());
            }
        }
    }

}
