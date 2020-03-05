package com.github.vzakharchenko.dynamic.orm.core.transaction;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 26.04.15
 * Time: 20:55
 */
public class TransactionNameManager extends DataSourceTransactionManager {

    private static Logger logger = LoggerFactory.getLogger(TransactionNameManager.class);

    private TransactionNameGenerator transactionNameGenerator =
            new DefaultTransactionNameGenerator();

    @Override
    protected void prepareSynchronization(DefaultTransactionStatus status,
                                          TransactionDefinition definition) {
        super.prepareSynchronization(status, definition);
        setTransactionName(status, definition);

    }

    private void setTransactionName(DefaultTransactionStatus status,
                                    TransactionDefinition definition) {
        try {
            String transactionName;
            if (status.isNewSynchronization()) {
                if (StringUtils.isNotEmpty(definition.getName())) {
                    transactionName = definition.getName();
                } else {
                    transactionName = transactionNameGenerator.generateName(status, definition);
                }
                logger.debug("start new Trab=nsaction: " + transactionName);
                TransactionSynchronizationManager.setCurrentTransactionName(transactionName);
            } else {
                if (StringUtils.isEmpty(TransactionSynchronizationManager
                        .getCurrentTransactionName())) {
                    throw new IllegalStateException(
                            "The name of the transaction could not be established");
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "The name of the transaction could not be established: " + e.getMessage(), e);
        }
    }

    public void setTransactionNameGenerator(TransactionNameGenerator transactionNameGenerator) {
        this.transactionNameGenerator = transactionNameGenerator;
    }
}
