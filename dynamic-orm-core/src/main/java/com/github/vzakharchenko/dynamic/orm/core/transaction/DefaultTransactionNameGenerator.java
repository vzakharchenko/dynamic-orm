package com.github.vzakharchenko.dynamic.orm.core.transaction;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.UUID;

/**
 * DefaultTransactionNameGenerator Set the transaction name as a method name where
 * a synchronization is started
 */
public class DefaultTransactionNameGenerator implements TransactionNameGenerator {

    @Override
    public String generateName(DefaultTransactionStatus status,
                               TransactionDefinition definition) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            if (Objects.equals(stackTraceElement.getClassName(),
                    TransactionTemplate.class.getCanonicalName())) {
                StackTraceElement stackTraceElementNext = stackTrace[++i];
                return stackTraceElementNext.getClassName() + "." +
                        stackTraceElementNext.getMethodName() + "@" +
                        stackTraceElementNext.getLineNumber();
            }
            if (Objects.equals(stackTraceElement.getClassName(),
                    TransactionBuilderImpl.class.getCanonicalName())) {
                StackTraceElement stackTraceElementNext = stackTrace[++i];
                return stackTraceElementNext.getClassName() + "." +
                        stackTraceElementNext.getMethodName() + "@" +
                        stackTraceElementNext.getLineNumber();
            }
        }
        return UUID.randomUUID().toString();
    }
}
