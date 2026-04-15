package com.soft.reclutamiento.infrastructure.transaction;

import java.util.function.Supplier;

/**
 * Interface para Unit of Work
 * Maneja transacciones y ejecución de operaciones con commit/rollback automático
 * Equivalente a IUnitOfWork.cs del proyecto .NET
 */
public interface IUnitOfWork {

    /**
     * Inicia una nueva transacción
     */
    ITransaction beginTransaction();

    /**
     * Obtiene la transacción actual si existe
     */
    ITransaction getCurrentTransaction();

    /**
     * Ejecuta una operación con manejo automático de commit/rollback
     * Si la operación lanza excepción, se hace rollback automáticamente
     */
    <TResult> TResult execute(Supplier<TResult> operation);

    /**
     * Ejecuta una operación dentro de una transacción explícita
     * Si la operación lanza excepción, se hace rollback automáticamente
     */
    <TResult> TResult executeInTransaction(Supplier<TResult> operation);

    /**
     * Ejecuta una operación void con manejo automático de commit/rollback
     */
    void execute(Runnable operation);

    /**
     * Ejecuta una operación void dentro de una transacción explícita
     */
    void executeInTransaction(Runnable operation);

    /**
     * Guarda los cambios pendientes (commit del contexto)
     */
    void commit();

    /**
     * Limpia el change tracker (EntityManager)
     */
    void clearChangeTracker();
}
