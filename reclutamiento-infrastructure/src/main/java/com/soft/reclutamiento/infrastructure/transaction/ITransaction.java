package com.soft.reclutamiento.infrastructure.transaction;

import java.util.UUID;

/**
 * Interface para manejo de transacciones
 * Equivalente a ITransaction.cs del proyecto .NET
 */
public interface ITransaction extends AutoCloseable {

    /**
     * Obtiene el ID único de la transacción
     */
    UUID getTransactionId();

    /**
     * Confirma la transacción (commit)
     */
    void commit();

    /**
     * Revierte la transacción (rollback)
     */
    void rollback();

    /**
     * Cierra y libera recursos de la transacción
     */
    @Override
    void close();
}
