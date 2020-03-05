package com.github.vzakharchenko.dynamic.orm.core.transaction;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

import java.io.Serializable;

/**
 * Transaction Name Generator
 *
 * @see TransactionNameManager
 * @see DefaultTransactionNameGenerator
 */
public interface TransactionNameGenerator extends Serializable {
    String generateName(DefaultTransactionStatus status, TransactionDefinition definition);
}
