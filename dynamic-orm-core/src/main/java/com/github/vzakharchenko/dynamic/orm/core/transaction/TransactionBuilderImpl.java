package com.github.vzakharchenko.dynamic.orm.core.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.github.vzakharchenko.dynamic.orm.core.helper.DBHelper;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;

import java.lang.reflect.Method;

/**
 *
 */
public class TransactionBuilderImpl implements TransactionBuilder {

    public static final String TRANSACTION_HOLDER = "TRANSACTION_HOLDER";
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionBuilderImpl.class);
    private static final TransactionHolderSynchronization
            TRANSACTION_HOLDER_SYNCHRONIZATION =
            new TransactionHolderSynchronization();
    private final QueryContextImpl queryContext;

    public TransactionBuilderImpl(QueryContextImpl queryContext) {
        this.queryContext = queryContext;
    }

    private static TransactionHolder getTransactionHolder() {
        return (TransactionHolder) TransactionSynchronizationManager
                .getResource(TRANSACTION_HOLDER);
    }

    @Override
    public boolean startTransactionIfNeeded() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            return false;
        }


        TransactionStatus transaction = queryContext.getTransactionManager()
                .getTransaction(queryContext.getTransactionDefinition());
        TransactionSynchronizationManager
                .bindResource(TRANSACTION_HOLDER, new SpringTransactionHolder(transaction));
        TransactionSynchronizationManager
                .registerSynchronization(TRANSACTION_HOLDER_SYNCHRONIZATION);

        LOGGER.info("Transaction started at " + Thread.currentThread().getName());
        return true;
    }

    @Override
    public void commit() {
        DBHelper.transactionCheck();
        TransactionHolder transaction = getTransactionHolder();
        if (transaction == null) {
            throw new IllegalStateException("Elsewhere is initiated transaction " +
                    TransactionSynchronizationManager.getCurrentTransactionName());
        }
        TransactionStatus status = transaction.getTransactionStatus();
        if (status.isCompleted()) {
            String message = "Unable commit, transaction is completed: " + transaction;
            LOGGER.error(message, new IllegalStateException(message));
            return;
        }
        queryContext.getTransactionManager().commit(status);
        LOGGER.info("Transaction commited at " + Thread
                .currentThread().getName() + " : " + transaction);
    }

    @Override
    public void rollback() {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionHolder transaction = getTransactionHolder();
        if (transaction == null) {
            if (isRollBackSpringTest()) {
                return;
            }
            TransactionStatus transactionStatus = TransactionAspectSupport
                    .currentTransactionStatus();
            if (transactionStatus != null) {
                transactionStatus.setRollbackOnly();
            }
            return;
        }
        TransactionStatus status = transaction.getTransactionStatus();
        if (status.isCompleted()) {
            String message = "Unable rollback, transaction is completed: " + transaction;
            LOGGER.error(message, new IllegalStateException(message));
            return;
        }
        status.setRollbackOnly();
        queryContext.getTransactionManager().commit(status);
        LOGGER.error("Transaction rolled back at " + Thread
                .currentThread().getName() + " : " + transaction);
    }

    private boolean isRollBackSpringTest() {
        try {
            Class<?> aClass = Class.forName(
                    "org.springframework.test.context.transaction.TestTransaction");
            Method flagForRollback = aClass.getDeclaredMethod("flagForRollback");
            flagForRollback.invoke(aClass);
            return true;
        } catch (RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            return false;
        }
    }

    private static class TransactionHolderSynchronization
            extends TransactionSynchronizationAdapter {
        @Override
        public void afterCompletion(int status) {
            TransactionSynchronizationManager.unbindResource(TRANSACTION_HOLDER);
        }
    }
}
