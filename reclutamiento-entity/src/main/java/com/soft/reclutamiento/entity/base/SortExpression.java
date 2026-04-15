package com.soft.reclutamiento.entity.base;

/**
 * Clase para expresiones de ordenamiento
 * Equivalente a SortExpression.cs del proyecto .NET
 *
 * @param <TEntity> Tipo de la entidad
 */
public class SortExpression<TEntity> {
    private SortDirection direction;
    private String property;

    public SortExpression() {
    }

    public SortExpression(SortDirection direction, String property) {
        this.direction = direction;
        this.property = property;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
