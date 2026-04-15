package com.soft.reclutamiento.infrastructure.transaction;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.UUID;

/**
 * Implementación de ITransaction
 * Wrapper sobre Spring TransactionStatus
 * Equivalente a Transaction.cs del proyecto .NET
 */
public class TransactionImpl implements ITransaction {

    private final TransactionStatus transactionStatus;
    private final PlatformTransactionManager transactionManager;
    private final UUID transactionId;

    public TransactionImpl(TransactionStatus transactionStatus, PlatformTransactionManager transactionManager) {
        this.transactionStatus = transactionStatus;
        this.transactionManager = transactionManager;
        this.transactionId = UUID.randomUUID();
    }

    @Override
    public UUID getTransactionId() {
        return transactionId;
    }

    @Override
    public void commit() {
        if (!transactionStatus.isCompleted()) {
            transactionManager.commit(transactionStatus);
        }
    }

    @Override
    public void rollback() {
        if (!transactionStatus.isCompleted()) {
            transactionManager.rollback(transactionStatus);
        }
    }

    @Override
    public void close() {
        // Si la transacción no fue completada (ni commit ni rollback), hacer rollback
        if (!transactionStatus.isCompleted()) {
            rollback();
        }
    }
}
