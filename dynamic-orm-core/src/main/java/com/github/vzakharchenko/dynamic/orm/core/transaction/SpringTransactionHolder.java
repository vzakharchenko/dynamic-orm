package com.github.vzakharchenko.dynamic.orm.core.transaction;

import org.springframework.transaction.TransactionStatus;

/**
 *
 */
public class SpringTransactionHolder implements TransactionHolder {

    private final TransactionStatus transaction;

    public SpringTransactionHolder(TransactionStatus transaction) {
        this.transaction = transaction;
    }

    @Override
    public TransactionStatus getTransactionStatus() {
        return transaction;
    }
}
