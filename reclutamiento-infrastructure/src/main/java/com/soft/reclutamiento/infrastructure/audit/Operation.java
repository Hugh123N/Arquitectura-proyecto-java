package com.soft.reclutamiento.infrastructure.audit;

/**
 * Enum para operaciones de auditoría
 * Equivalente a Operation.cs del proyecto .NET
 */
public enum Operation {
    CREATE("To audit created entity"),
    UPDATE("To audit updated entity"),
    DELETE("To audit deleted entity");

    private final String description;

    Operation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
