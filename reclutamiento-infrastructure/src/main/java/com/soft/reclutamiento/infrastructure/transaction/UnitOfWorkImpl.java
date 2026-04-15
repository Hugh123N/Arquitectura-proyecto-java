package com.soft.reclutamiento.infrastructure.transaction;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.function.Supplier;

/**
 * Implementación de Unit of Work
 * Maneja transacciones con commit/rollback automático
 * Equivalente a UnitOfWork.cs del proyecto .NET
 */
@Component
public class UnitOfWorkImpl implements IUnitOfWork {

    private final EntityManager entityManager;
    private final PlatformTransactionManager transactionManager;
    private final ThreadLocal<ITransaction> currentTransaction = new ThreadLocal<>();

    public UnitOfWorkImpl(EntityManager entityManager, PlatformTransactionManager transactionManager) {
        this.entityManager = entityManager;
        this.transactionManager = transactionManager;
    }

    @Override
    public ITransaction beginTransaction() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);

        ITransaction transaction = new TransactionImpl(status, transactionManager);
        currentTransaction.set(transaction);
        return transaction;
    }

    @Override
    public ITransaction getCurrentTransaction() {
        return currentTransaction.get();
    }

    @Override
    public <TResult> TResult execute(Supplier<TResult> operation) {
        try {
            TResult result = operation.get();
            commit();
            return result;
        } catch (Exception e) {
            clearChangeTracker();
            throw e;
        }
    }

    @Override
    public <TResult> TResult executeInTransaction(Supplier<TResult> operation) {
        // Verificar si ya hay una transacción en curso
        ITransaction existingTransaction = getCurrentTransaction();
        if (existingTransaction != null && existingTransaction.getTransactionId() != null) {
            // Ya hay transacción, ejecutar dentro de ella
            return operation.get();
        }

        // No hay transacción, crear una nueva
        try (ITransaction transaction = beginTransaction()) {
            try {
                TResult result = operation.get();
                transaction.commit();
                return result;
            } catch (Exception e) {
                transaction.rollback();
                clearChangeTracker();
                throw e;
            } finally {
                currentTransaction.remove();
            }
        }
    }

    @Override
    public void execute(Runnable operation) {
        try {
            operation.run();
            commit();
        } catch (Exception e) {
            clearChangeTracker();
            throw e;
        }
    }

    @Override
    public void executeInTransaction(Runnable operation) {
        // Verificar si ya hay una transacción en curso
        ITransaction existingTransaction = getCurrentTransaction();
        if (existingTransaction != null && existingTransaction.getTransactionId() != null) {
            // Ya hay transacción, ejecutar dentro de ella
            operation.run();
            return;
        }

        // No hay transacción, crear una nueva
        try (ITransaction transaction = beginTransaction()) {
            try {
                operation.run();
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                clearChangeTracker();
                throw e;
            } finally {
                currentTransaction.remove();
            }
        }
    }

    @Override
    public void commit() {
        entityManager.flush();
    }

    @Override
    public void clearChangeTracker() {
        entityManager.clear();
    }
}
