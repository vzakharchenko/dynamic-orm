package com.github.vzakharchenko.dynamic.orm.core.transaction;

/**
 * transaction Manager
 */
public interface TransactionBuilder {

    /**
     * start transaction synchronisation
     * <p>
     * example:
     * <p>
     * boolean isStarted = transaction.startTransactionIfNeeded()
     * <p>
     * try{
     * ....
     * if(isStarted){
     * transaction.commit()
     * } catch(Exception ex){
     * transaction.rollback()
     * ....
     * }
     *
     * @return true if is started the transaction. false if transaction is already started
     * @see org.springframework.transaction.annotation.Transactional
     * @see org.springframework.transaction.support.TransactionTemplate
     * @see org.springframework.transaction.support.TransactionSynchronizationManager
     */
    boolean startTransactionIfNeeded();

    /**
     * commit a transaction
     */
    void commit();

    /**
     * rollback a transaction
     */
    void rollback();
}
