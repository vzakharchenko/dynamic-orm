package com.github.vzakharchenko.dynamic.orm.core.transaction;

import org.springframework.transaction.TransactionStatus;

public interface TransactionHolder {
    TransactionStatus getTransactionStatus();
}
